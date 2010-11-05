package org.apache.jmeter.monitor;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

public class JmeterMonitorDataStat extends MonitorDataStat {
	private String label = null;
	private BigDecimal count;
	private BigDecimal responeTime;
	private BigDecimal minValue;
	private BigDecimal maxValue;
	private BigDecimal stddev;
	private BigDecimal error;
	private BigDecimal tps;

	public void addData(String[] strs){
		// count
		count=new BigDecimal(Long.parseLong(StringUtils.strip(strs[1])));
		responeTime=new BigDecimal(Long.parseLong(StringUtils.strip(strs[2])));
		minValue=new BigDecimal(Long.parseLong(StringUtils.strip(strs[6])));
		maxValue=new BigDecimal(Long.parseLong(StringUtils.strip(strs[5])));
		stddev=new BigDecimal(Long.parseLong(StringUtils.strip(strs[3])));
		BigDecimal errNum=new BigDecimal(Long.parseLong(StringUtils.strip(strs[8])));
		if (!errNum.equals(BigDecimal.ZERO)) {
			System.out.println(errNum);
		}
		error=errNum.divide(count,2,BigDecimal.ROUND_HALF_UP);
		tps=new BigDecimal(Long.parseLong(StringUtils.strip(strs[8])));
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public BigDecimal getCount() {
		return count;
	}

	public void setCount(BigDecimal count) {
		this.count = count;
	}

	public BigDecimal getResponeTime() {
		return responeTime;
	}

	public void setResponeTime(BigDecimal responeTime) {
		this.responeTime = responeTime;
	}

	public BigDecimal getMinValue() {
		return minValue;
	}

	public void setMinValue(BigDecimal minValue) {
		this.minValue = minValue;
	}

	public BigDecimal getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(BigDecimal maxValue) {
		this.maxValue = maxValue;
	}

	public BigDecimal getStddev() {
		return stddev;
	}

	public void setStddev(BigDecimal stddev) {
		this.stddev = stddev;
	}

	public BigDecimal getError() {
		return error;
	}

	public void setError(BigDecimal error) {
		this.error = error;
	}

	public BigDecimal getTps() {
		return tps;
	}

	public void setTps(BigDecimal tps) {
		this.tps = tps;
	}

}
