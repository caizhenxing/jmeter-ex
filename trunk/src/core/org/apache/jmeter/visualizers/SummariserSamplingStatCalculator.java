package org.apache.jmeter.visualizers;

import org.apache.jmeter.samplers.SampleResult;

/**
 * 用于计算指定间隔时间内的响应时间，平方差，平方和等，并将结果输出至JmeterDetail
 * 
 * @author chenchao.yecc
 * @since jex003A
 */
public class SummariserSamplingStatCalculator extends SamplingStatCalculator {
    public double getSqurSum() {
        return super.calculator.getsumOfSquares();
    }

    public SummariserSamplingStatCalculator(String label) {
        super(label);
    }

    /**
     * Records a sample.
     */
    public Sample addSample(SampleResult res) {
        long eCount;
        long endTime;
        double throughput;
        synchronized (calculator) {
            long byteslength = res.getBytes();
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
            maxThroughput = throughput;
        }
        return null;
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
