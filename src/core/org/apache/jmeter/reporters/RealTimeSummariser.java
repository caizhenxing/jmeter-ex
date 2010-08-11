package org.apache.jmeter.reporters;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.TestListener;
import org.apache.jmeter.visualizers.RunningSample;
import org.apache.jorphan.util.JOrphanUtils;

/**
 * 
 * @author chenchao.yecc
 * @version jex002A
 */
public class RealTimeSummariser extends AbstractTestElement implements Serializable, SampleListener, TestListener{
	private static final long serialVersionUID = 1L;
	private transient volatile PrintWriter writer= null;
	private transient String myName = "";
	private static String SPLIT = ",";
	private static final DecimalFormat dfDouble = new DecimalFormat("#0.0");
	private static final Hashtable accumulators = new Hashtable();
	private static int instanceCount;
	private double sumRsTime = 0;
	private long firstTime = 0;
	private long endTime = 0;
	private long error = 0;
	private long count = 0;
	
	private Set<String> labels =new HashSet<String>();
	private Map<String, SampleVisualizer> visualizers =new HashMap<String, SampleVisualizer>();
	
	public RealTimeSummariser() {
		super();
		try {
			writer = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream("JmeterData",
					false)), SaveService.getFileEncoding("UTF-8")), true);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void sampleOccurred(SampleEvent e) {
		SampleResult s = e.getResult();

		if (labels.add(s.getSampleLabel())) {
			visualizers.put(s.getSampleLabel(), new SampleVisualizer());
		}
		
		boolean reportNow = false;

		/*
		 * Have we reached the reporting boundary? Need to allow for a margin of
		 * error, otherwise can miss the slot. Also need to check we've not hit
		 * the window already
		 */
		synchronized (visualizers.get(s.getSampleLabel())) {
			if (s != null) {
				visualizers.get(s.getSampleLabel()).analyse(s);
			}

//			if ((now > myTotals.last + 5) && (now % 3 <= 5)) {
				reportNow = true;
//			}
		}
		if (reportNow) {
			String str;
	    	for (Iterator<String> iterator = labels.iterator(); iterator.hasNext();) {
	    		String label = iterator.next();
	            str = format(label);
	            // 写入结果文件
	            writer.println(str);
	            writer.flush();
	        }
		}
	}
	
    protected static class Totals {	// jex002C

        /** Time of last summary (to prevent double reporting) */
        long last = 0;	// jex002C

        final RunningSample delta = new RunningSample("DELTA",0);	// jex002C

        final RunningSample total = new RunningSample("TOTAL",0);	// jex002C

        /**
         * Add the delta values to the total values and clear the delta
         */
        void moveDelta() {	// jex002C
            total.addSample(delta);
            delta.clear();
        }
    }
    
    public void testEnded(String host) {
        Object[] totals = null;
        synchronized (accumulators) {
            instanceCount--;
            if (instanceCount <= 0){
                totals = accumulators.entrySet().toArray();                
            }
        }
        if (totals == null) {// We're not done yet
            return;
        }
        
    	for (Iterator<String> iterator = labels.iterator(); iterator.hasNext();) {
    		String label = iterator.next();
            writer.println(format(label));
            writer.flush();
        }
        writer.close();
    }
    
    protected static StringBuffer longToSb(StringBuffer sb, long l, int len) {	// jex002C
        sb.setLength(0);
        sb.append(l);
        return JOrphanUtils.rightAlign(sb, len);
    }
    
    protected static StringBuffer doubleToSb(StringBuffer sb, double d, int len, int frac) {	// jex002C
        sb.setLength(0);
        dfDouble.setMinimumFractionDigits(frac);
        dfDouble.setMaximumFractionDigits(frac);
        sb.append(dfDouble.format(d));
        return JOrphanUtils.rightAlign(sb, len);
    }
    
    protected String format(String name) {	// jex002C
    	SampleVisualizer sv= visualizers.get(name);
        StringBuffer sb = new StringBuffer(100); // output line buffer
        // 名字
        sb.append(name);
        sb.append(SPLIT);
        //个数
        sb.append(sv.count);
        sb.append(SPLIT);
        
        //平均响应时间
        long res = sv.sumRsTime/sv.count;
        sb.append(res);
        sb.append(SPLIT);
        //最大响应时间
        sb.append(sv.maxRsTime);
        sb.append(SPLIT);
        //最小响应时间
        sb.append(sv.minRsTime);
        sb.append(SPLIT);
        
        //Error
        sb.append(sv.error);
        sb.append(SPLIT);
        
        //时间戳
        sb.append(sv.endTime);
        sb.append(SPLIT);
        
        // 方差
        double dev=Math.sqrt(res)-Math.sqrt(sv.lastRestime);
        sb.append(dev);
        sb.append(SPLIT);

        // 平方和
        double dev=Math.sqrt(res)-Math.sqrt(sv.lastRestime);
        sb.append(dev);
        sv.lastRestime=res;
        return sb.toString();
    }

	@Override
	public void sampleStarted(SampleEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sampleStopped(SampleEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void testEnded() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void testIterationStart(LoopIterationEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void testStarted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void testStarted(String host) {
		// TODO Auto-generated method stub
		
	}
	
	private static class SampleVisualizer{
		long sumRsTime = 0;
		long firstTime = 0;
		long endTime = 0;
		long error = 0;
		long count = 0;
		long lastRestime = 0;
		// 平方和
		long sumSqr = 0;
		
		private double maxTps = Double.MIN_VALUE;
		private double minTps = Double.MAX_VALUE;
		private long maxRsTime = Long.MIN_VALUE;
		private long minRsTime = Long.MAX_VALUE;
		boolean firstTimeInit = false;
		public void analyse(SampleResult s) {
			if (!firstTimeInit) {
				firstTime = endTime;
				firstTimeInit = true;
			}
			long howLongRunning = endTime - firstTime;
			count = count + 1;
			long resTime=s.getTime();
			minRsTime = Math.min(resTime, minRsTime);
			maxRsTime = Math.max(resTime, maxRsTime);
			sumRsTime = sumRsTime + resTime;
			if (howLongRunning != 0) {
				double tps = ((double) count / (double) howLongRunning) * 1000.0;
				minTps = Math.min(tps, minTps);
				maxTps = Math.max(tps, maxTps);
			}
			if (!s.isSuccessful()) {
				error = error + 1;
			}
		}
		public void clearAllData(){
			long sumRsTime = 0;
			long firstTime = 0;
			long endTime = 0;
			long error = 0;
			long count = 0;
			long lastRestime = 0;
			// 平方和
			long sumSqr = 0;
		}
	}
}
