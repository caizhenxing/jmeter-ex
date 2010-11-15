package org.apache.jmeter.monitor;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Monitor data
 * @author chenchao.yecc
 * @version jex002A
 *
 */
public class MonitorDataStat {

	private String label = "";
	private BigDecimal min = new BigDecimal(Long.MAX_VALUE);
	private BigDecimal max = BigDecimal.ZERO;
	private BigDecimal last = BigDecimal.ZERO;
	private BigDecimal average = BigDecimal.ZERO;
	private BigDecimal total=BigDecimal.ZERO;
	private BigDecimal count=BigDecimal.ZERO;
	public static DecimalFormat df = new java.text.DecimalFormat(
			"#0.00");

	public void addData(Number d){
		BigDecimal t= new BigDecimal(String.valueOf(d));
		if (t.compareTo(min)==-1) {
			min=t;
		}
		if (t.compareTo(max)==1) {
			max=t;
		}
		total=total.add(t);
		count=count.add(BigDecimal.ONE);
		average=total.divide(count,2,BigDecimal.ROUND_HALF_UP);
		last=t;
	}
	
	public void addData(String[] d){

	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getMin() {
		return df.format(min);
	}

	public void setMin(BigDecimal min) {
		this.min = min;
	}

	public String getMax() {
		return df.format(max);
	}

	public void setMax(BigDecimal max) {
		this.max = max;
	}

	public String getLast() {
		return df.format(last);
	}

	public void setLast(BigDecimal last) {
		this.last = last;
	}

	public String getAverage() {
		return df.format(average);
	}

	public void setAverage(BigDecimal average) {
		this.average = average;
	}
}
