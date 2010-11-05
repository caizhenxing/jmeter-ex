package org.apache.jmeter.visualizers;

/**
 * 用于计算指定间隔时间内的响应时间，平方差，平方和等，并将结果输出至JmeterDetail
 * @author chenchao.yecc
 * @since jex003A
 *
 */
public class SummariserSamplingStatCalculator extends SamplingStatCalculator {
	public double getSqurSum() {
		return super.calculator.getsumOfSquares();
	}

	public SummariserSamplingStatCalculator(String label) {
		super(label);
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
