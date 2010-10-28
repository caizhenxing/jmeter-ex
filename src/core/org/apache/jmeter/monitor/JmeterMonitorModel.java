package org.apache.jmeter.monitor;

import java.awt.Color;
import java.util.Date;

import javax.swing.JCheckBox;

import org.apache.commons.lang.StringUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;

public class JmeterMonitorModel extends MonitorModel{
	private static final Logger log = LoggingManager.getLoggerForClass();
	
	public void updateGui(String category, String[] fs, String[] strings) {
			updateAverageTime(strings);
			updateTps(strings);
			updateErrNum(strings);
			updateStdDevTime(strings);
	}
	
	private void updateGui(TimeSeries ts,String value){
		Date time = null;
		try {
			time = new Date(Long.parseLong(value));
		} catch (NumberFormatException e) {
			log.error("Error date value:" + value);
		}
		Long v = Long.parseLong(StringUtils.strip(value));
		updateGui(ts, new Second(time), v);
	}
	
	private void updateAverageTime(String[] strings){
		TimeSeries ts = dataMap.get("jmeter$$avgTime");
		updateGui(ts,strings[2]);
	}
	
	private void updateTps(String[] strings){
		TimeSeries ts = dataMap.get("jmeter$$avgTps");
	}
	
	private void updateErrNum(String[] strings){
		TimeSeries ts = dataMap.get("jmeter$$errNum");
		updateGui(ts,strings[8]);
	}
	
	private void updateStdDevTime(String[] strings){
		TimeSeries ts = dataMap.get("jmeter$$stdDevTime");
		updateGui(ts,strings[3]);
	}
	
	
}
