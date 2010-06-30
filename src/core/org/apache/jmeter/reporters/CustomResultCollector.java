package org.apache.jmeter.reporters;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.engine.util.NoThreadClone;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.samplers.Clearable;
import org.apache.jmeter.samplers.Remoteable;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.SampleSaveConfiguration;
import org.apache.jmeter.save.CSVSaveService;
import org.apache.jmeter.save.OldSaveService;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestListener;
import org.apache.jmeter.testelement.property.BooleanProperty;
import org.apache.jmeter.testelement.property.ObjectProperty;
import org.apache.jmeter.visualizers.Visualizer;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.jorphan.util.JMeterError;
import org.apache.jorphan.util.JOrphanUtils;
import org.apache.log.Logger;

/**
 * Save sample result as line data;
 * 
 * @author chenchao.yecc
 * @since jex002A
 *
 */
public class CustomResultCollector  extends ResultCollector implements Clearable, Serializable,
        TestListener, Remoteable, NoThreadClone {

    private static final Logger log = LoggingManager.getLoggerForClass();

    private static final long serialVersionUID = 233L;

    // This string is used to identify local test runs, so must not be a valid host name
    private static final String TEST_IS_LOCAL = "*local*"; // $NON-NLS-1$

    public static final String FILENAME = "filename"; // $NON-NLS-1$

    private static final String SAVE_CONFIG = "saveConfig"; // $NON-NLS-1$

    private static final String ERROR_LOGGING = "ResultCollector.error_logging"; // $NON-NLS-1$

    private static final String SUCCESS_ONLY_LOGGING = "ResultCollector.success_only_logging"; // $NON-NLS-1$
    
    // Static variables

    // Lock used to guard static mutable variables
    private static final Object LOCK = new Object();
    
    //@GuardedBy("LOCK")
    private static final Map<String,PrintWriter> files = new HashMap<String,PrintWriter>();

    /**
     * The instance count is used to keep track of whether any tests are currently running.
     * It's not possible to use the constructor or threadStarted etc as tests may overlap
     * e.g. a remote test may be started,
     * and then a local test started whilst the remote test is still running. 
     */
    //@GuardedBy("LOCK")
    private static int instanceCount; // Keep track of how many instances are active

    // Instance variables
    
    private transient volatile PrintWriter out;

    private volatile boolean inTest = false;

    private volatile boolean isStats = false;

    /**
     * No-arg constructor.
     */
    public CustomResultCollector() {
        setErrorLogging(false);
        setSuccessOnlyLogging(false);
        setProperty(new ObjectProperty(SAVE_CONFIG, new SampleSaveConfiguration()));
    }

    // Ensure that the sample save config is not shared between copied nodes
    // N.B. clone only seems to be used for client-server tests
    public Object clone(){
        ResultCollector clone = (ResultCollector) super.clone();
        clone.setSaveConfig((SampleSaveConfiguration)clone.getSaveConfig().clone());
        return clone;
    }

    private void setFilenameProperty(String f) {
        setProperty(FILENAME, f);
    }

    public String getFilename() {
        return getPropertyAsString(FILENAME);
    }

    public boolean isErrorLogging() {
        return getPropertyAsBoolean(ERROR_LOGGING);
    }

    public void setErrorLogging(boolean errorLogging) {
        setProperty(new BooleanProperty(ERROR_LOGGING, errorLogging));
    }

    public void setSuccessOnlyLogging(boolean value) {
        if (value) {
            setProperty(new BooleanProperty(SUCCESS_ONLY_LOGGING, true));
        } else {
            removeProperty(SUCCESS_ONLY_LOGGING);
        }
    }

    public boolean isSuccessOnlyLogging() {
        return getPropertyAsBoolean(SUCCESS_ONLY_LOGGING,false);
    }

    /**
     * Decides whether or not to a sample is wanted based on:<br/>
     * - errorOnly<br/>
     * - successOnly<br/>
     * - sample success<br/>
     * Should only be called for single samples.
     *
     * @param success is sample successful
     * @return whether to log/display the sample
     */
    public boolean isSampleWanted(boolean success){
        boolean errorOnly = isErrorLogging();
        boolean successOnly = isSuccessOnlyLogging();
        return isSampleWanted(success, errorOnly, successOnly);
    }

    /**
     * Decides whether or not to a sample is wanted based on: <br/>
     * - errorOnly <br/>
     * - successOnly <br/>
     * - sample success <br/>
     * This version is intended to be called by code that loops over many samples;
     * it is cheaper than fetching the settings each time.
     * @param success status of sample
     * @param errorOnly if errors only wanted
     * @param successOnly if success only wanted
     * @return whether to log/display the sample
     */
    public static boolean isSampleWanted(boolean success, boolean errorOnly,
            boolean successOnly) {
        return (!errorOnly && !successOnly) ||
               (success && successOnly) ||
               (!success && errorOnly);
        // successOnly and errorOnly cannot both be set
    }
    /**
     * Sets the filename attribute of the ResultCollector object.
     *
     * @param f
     *            the new filename value
     */
    public void setFilename(String f) {
        if (inTest) {
            return;
        }
        setFilenameProperty(f);
    }

    public void testEnded(String host) {
        synchronized(LOCK){
            instanceCount--;
            if (instanceCount <= 0) {
                finalizeFileOutput();
                inTest = false;
            }            
        }
    }

    public synchronized void testStarted(String host) {
        synchronized(LOCK){
            instanceCount++;
            try {
                initializeFileOutput();
                if (getVisualizer() != null) {
                    this.isStats = getVisualizer().isStats();
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }
        inTest = true;
    }

    public void testEnded() {
        testEnded(TEST_IS_LOCAL);
    }

    public void testStarted() {
        testStarted(TEST_IS_LOCAL);
    }

    /**
     * Loads an existing sample data (JTL) file.
     * This can be one of:
     * - XStream format
     * - Avalon format
     * - CSV format
     *
     */
    public void loadExistingFile() {
        final Visualizer visualizer = getVisualizer();
        if (visualizer == null) {
            return; // No point reading the file if there's no visualiser
        }
        boolean parsedOK = false;
        String filename = getFilename();
        File file = new File(filename);
        if (file.exists()) {
            BufferedReader dataReader = null;
            BufferedInputStream bufferedInputStream = null;
            try {
                dataReader = new BufferedReader(new FileReader(file));
                // Get the first line, and see if it is XML
                String line = dataReader.readLine();
                dataReader.close();
                dataReader = null;
                if (line == null) {
                    log.warn(filename+" is empty");
                } else {
                    if (!line.startsWith("<?xml ")){// No, must be CSV //$NON-NLS-1$
                        CSVSaveService.processSamples(filename, visualizer, this);
                        parsedOK = true;
                    } else { // We are processing XML
                        try { // Assume XStream
                            bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
                            SaveService.loadTestResults(bufferedInputStream, 
                                    new ResultCollectorHelper(this, visualizer));
                            parsedOK = true;
                        } catch (Exception e) {
                            log.info("Failed to load "+filename+" using XStream, trying old XML format. Error was: "+e);
                            try {
                                OldSaveService.processSamples(filename, visualizer, this);
                                parsedOK = true;
                            } catch (Exception e1) {
                                log.warn("Error parsing Avalon XML. " + e1.getLocalizedMessage());
                            }
                        }
                    }
                }
            } catch (IOException e) {
                log.warn("Problem reading JTL file: "+file);
            } catch (JMeterError e){
                log.warn("Problem reading JTL file: "+file);
            } catch (RuntimeException e){ // e.g. NullPointerException
                log.warn("Problem reading JTL file: "+file,e);
            } catch (OutOfMemoryError e) {
                log.warn("Problem reading JTL file: "+file,e);
            } finally {
                JOrphanUtils.closeQuietly(dataReader);
                JOrphanUtils.closeQuietly(bufferedInputStream);
                if (!parsedOK) {
                    GuiPackage.showErrorMessage(
                                "Error loading results file - see log file",
                                "Result file loader");
                }
            }
        } else {
            GuiPackage.showErrorMessage(
                    "Error loading results file - could not open file",
                    "Result file loader");
        }
    }

    private static PrintWriter getFileWriter(String filename)
            throws IOException {
        if (filename == null || filename.length() == 0) {
            return null;
        }
        PrintWriter writer = files.get(filename);
        boolean trimmed = true;

        if (writer == null) {
            trimmed = new File(filename).exists();
            // Find the name of the directory containing the file
            // and create it - if there is one
            File pdir = new File(filename).getParentFile();
            if (pdir != null) {
                pdir.mkdirs();// returns false if directory already exists, so need to check again
                if (!pdir.exists()){
                    log.warn("Error creating directories for "+pdir.toString());
                }
            }
            writer = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(filename,
                    trimmed)), SaveService.getFileEncoding("UTF-8")), true); // $NON-NLS-1$
            log.debug("Opened file: "+filename);
            files.put(filename,writer);
        }
        if (!trimmed) {
//            writeFileStart(writer, saveConfig);
        }
        return writer;
    }

    public void sampleStarted(SampleEvent e) {
    }

    public void sampleStopped(SampleEvent e) {
    }

    /**
     * When a test result is received, display it and save it.
     *
     * @param event
     *            the sample event that was received
     */
    public void sampleOccurred(SampleEvent event) {
        SampleResult result = event.getResult();

        if (isSampleWanted(result.isSuccessful())) {
            if (out != null && !isResultMarked(result) && !this.isStats) {
                try {
					SaveService.saveSampleResultHex(event, out);
				} catch (Exception err) {
                    log.error("Error trying to record a sample", err); // should throw exception back to caller
                }
            }
        }
    }

    /**
     * recordStats is used to save statistics generated by visualizers
     *
     * @param e
     * @throws Exception
     */
    // Used by: MonitorHealthVisualizer.add(SampleResult res)
    public void recordStats(TestElement e) throws Exception {
        if (out != null) {
            SaveService.saveTestElement(e, out);
        }
    }

    /**
     * Checks if the sample result is marked or not, and marks it
     * @param res - the sample result to check
     * @return <code>true</code> if the result was marked
     */
    private boolean isResultMarked(SampleResult res) {
        String filename = getFilename();
        return res.markFile(filename);
    }

    private void initializeFileOutput() throws IOException {

        String filename = getFilename();
        if (filename != null) {
            if (out == null) {
                try {
                    out = getFileWriter(filename);
                } catch (FileNotFoundException e) {
                    out = null;
                }
            }
        }
    }

    private void finalizeFileOutput() {
		for (Iterator<String> iterator = files.keySet().iterator(); iterator.hasNext();) {
			String name = iterator.next();
			PrintWriter fe = files.get(name);
			fe.close();
			if (fe.checkError()) {
				log.warn("Problem detected during use of " + name);
			}
		}
		files.clear();
	}

    /*
     * (non-Javadoc)
     *
     * @see TestListener#testIterationStart(LoopIterationEvent)
     */
    public void testIterationStart(LoopIterationEvent event) {
    }

    /**
     * @param saveConfig
     *            The saveConfig to set.
     */
    public void setSaveConfig(SampleSaveConfiguration saveConfig) {
        getProperty(SAVE_CONFIG).setObjectValue(saveConfig);
    }

    // This is required so that
    // @see org.apache.jmeter.gui.tree.JMeterTreeModel.getNodesOfType()
    // can find the Clearable nodes - the userObject has to implement the interface.
    public void clearData() {
    }

}
