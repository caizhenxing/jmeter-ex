package org.apache.jmeter.monitor;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class MonitorDataStat {

	String label = "";
	BigDecimal min = new BigDecimal(Long.MAX_VALUE);
	BigDecimal max = BigDecimal.ZERO;
	BigDecimal last = BigDecimal.ZERO;
	BigDecimal average = BigDecimal.ZERO;
	BigDecimal total=BigDecimal.ZERO;
	BigDecimal count=BigDecimal.ZERO;
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
