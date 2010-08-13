package org.apache.jmeter.reporters;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.visualizers.RunningSample;

/**
 * 
 * @author chenchao.yecc
 * @version jex002A
 */
public class RealTimeSummariser extends Summariser {
	private static final long serialVersionUID = 1L;
	private transient volatile PrintWriter writer= null;
	private WriteTimer timeWriter=new WriteTimer(10);
	private SampleVisualizer sv = new SampleVisualizer();
	public RealTimeSummariser(String s) {
		super(s);
		try {
			writer = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream("JmeterData",
			        false)), SaveService.getFileEncoding("UTF-8")), true);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (writer!=null) {
			timeWriter.setWriter(writer);
		}
		timeWriter.start();
	}

	public void sampleOccurred(SampleEvent e) {
		SampleResult s = e.getResult();

		long now = System.currentTimeMillis() / 1000;// in seconds

		RunningSample myDelta = null;
		RunningSample myTotal = null;
		boolean reportNow = false;

		/*
		 * Have we reached the reporting boundary? Need to allow for a margin of
		 * error, otherwise can miss the slot. Also need to check we've not hit
		 * the window already
		 */
		synchronized (myTotals) {
			if (s != null) {
				myTotals.delta.addSample(s);
			}

			if ((now > myTotals.last + 5) && (now % 3 <= 5)) {
				reportNow = true;

				// copy the data to minimise the synch time
				myDelta = new RunningSample(myTotals.delta);
				myTotals.moveDelta();
				myTotal = new RunningSample(myTotals.total);

				myTotals.last = now; // stop double-reporting
			}
		}
		if (reportNow) {
			String str;
//			str = format(myName, myDelta, "+");
			// 写入结果文件
			// log.info(str);
//			System.out.println(str);
			// Only if we have updated them
			if (myTotal != null && myDelta != null
					&& myTotal.getNumSamples() != myDelta.getNumSamples()) {
				str = format(myName, myTotal, "=");
				// 写入结果文件
				writer.println(str);
				writer.flush();
			}
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
        for (int i=0; i<totals.length; i++) {
            Map.Entry me = (Map.Entry)totals[i];
            String str;
            String name = (String) me.getKey();
            Totals total = (Totals) me.getValue();
            // Only print final delta if there were some samples in the delta
            // and there has been at least one sample reported previously
            total.moveDelta();
            str = format(name, total.total, "=");
            writer.println(str);
            writer.flush();
        }
        writer.close();
    }
    
    protected String format(String name, RunningSample s, String type) {	// jex002C
        StringBuffer tmp = new StringBuffer(20); // for intermediate use
        StringBuffer sb = new StringBuffer(100); // output line buffer
        sb.append(name);
        sb.append(" ");
        sb.append(type);
        sb.append(" ");
        sb.append(longToSb(tmp, s.getNumSamples(), 5));
        sb.append(" in ");
        long elapsed = s.getElapsed();
        sb.append(doubleToSb(tmp, elapsed / 1000.0, 5, 1));
        sb.append("s = ");
        if (elapsed > 0) {
            sb.append(doubleToSb(tmp, s.getRate(), 6, 1));
        } else {
            sb.append("******");// Rate is effectively infinite
        }
        sb.append("/s Avg: ");
        sb.append(longToSb(tmp, s.getAverage(), 5));
        sb.append(" Min: ");
        sb.append(longToSb(tmp, s.getMin(), 5));
        sb.append(" Max: ");
        sb.append(longToSb(tmp, s.getMax(), 5));
        sb.append(" Err: ");
        sb.append(longToSb(tmp, s.getErrorCount(), 5));
        sb.append(" (");
        sb.append(s.getErrorPercentageString());
        sb.append(")");
        return sb.toString();
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
	}
}
