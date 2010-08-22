package org.apache.jmeter.visualizers;

import org.apache.jmeter.samplers.SampleResult;

/**
 * 用于计算指定间隔时间内的响应时间，平方差，平方和等，并将结果输出至JmeterDetail
 * @author chenchao.yecc
 * @since jex003A
 *
 */
public class SummariserSamplingStatCalculator extends SamplingStatCalculator {

	protected Integer count=0;
	public double getSqurSum() {
		return super.calculator.getsumOfSquares();
	}

	public SummariserSamplingStatCalculator(String label) {
		super(label);
	}

	 public Sample addSample(SampleResult res) {
	        long rtime, cmean, cstdv, cmedian, cpercent, eCount, endTime;
	        double squrSum;	// jex003A
	        double throughput;
	        boolean rbool;
	        synchronized (calculator) {
	            long byteslength = res.getBytes();
	            // if there was more than 1 loop in the sample, we
	            // handle it appropriately
	            if (res.getSampleCount() > 1) {
	                long time = res.getTime() / res.getSampleCount();
	                long resbytes = byteslength / res.getSampleCount();
	                for (int idx = 0; idx < res.getSampleCount(); idx++) {
	                    calculator.addValue(time);
	                    calculator.addBytes(resbytes);
	                }
	            } else {
	                calculator.addValue(res.getTime());
	                calculator.addBytes(byteslength);
	            }
	            setStartTime(res);
	            eCount = getCurrentSample().getErrorCount();
	            if (!res.isSuccessful()) {
	                eCount++;
	            }
	            endTime = getEndTime(res);
	            long howLongRunning = endTime - firstTime;
	            throughput = ((double) calculator.getCount() / (double) howLongRunning) * 1000.0;
	            if (throughput > maxThroughput) {
	                maxThroughput = throughput;
	            }

	            rtime = res.getTime();
	            cmean = (long)calculator.getMean();
	            cstdv = (long)calculator.getStandardDeviation();
	            cmedian = calculator.getMedian().longValue();
	            cpercent = calculator.getPercentPoint( 0.500 ).longValue();
	            squrSum = calculator.getsumOfSquares();
	            rbool = res.isSuccessful();
	        }
	        synchronized (count) {
	        	count = count + 1;
	        	Sample s =
	        		new Sample( null, rtime, cmean, cstdv, cmedian, cpercent, throughput, eCount, rbool, count, endTime );
	        	return s;
			}
	    }
	 
    public void addSamples(SamplingStatCalculator ssc) {
        calculator.addAll(ssc.calculator);
        if (firstTime > ssc.firstTime) {
            firstTime = ssc.firstTime;
        }
    }

	public void clear() {
		super.clear();
	}
}
