package org.apache.jmeter.visualizers;

/**
 * 
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

	public void clear() {
		super.clear();
	}
}
