package org.apache.jmeter.monitor;

import java.awt.Color;
import java.util.Date;

import javax.swing.JCheckBox;

import org.apache.commons.lang.StringUtils;
import org.apache.jmeter.monitor.gui.MonitorGui;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;

public class JmeterMonitorModel extends MonitorModel{
	private static final Logger log = LoggingManager.getLoggerForClass();
	
	public synchronized void addTimeSeries(String name, TimeSeries ts) {
		if (MonitorGui.MONITOR_CONFIGURE.get(category).getYAxisCount() == 1) {
			localTimeSeriesCollectionL.addSeries(ts);
		} else {
			String[] tmp = name.split("\\$\\$");
			String item = tmp[1];
			String state = MonitorGui.MONITOR_CONFIGURE.get(category).getShowType(item);
			if (state.equals("1")) {
				localTimeSeriesCollectionL.addSeries(ts);
			} else if(state.equals("2")) {
				localTimeSeriesCollectionR.addSeries(ts);
			} else {
			}
		}
		dataMap.put(name, ts);
		String[] tmp = name.split("\\$\\$");
		JCheckBox jb = createChooseCheckBox(tmp[1], Color.BLACK);
		checkboxPanel.add(jb);
		cbMap.put(jb, tmp[1]);
		// 增加列表行
		MonitorDataStat mds=new MonitorDataStat();
		mds.setLabel(tmp[1]);
		model.insertRow(mds, model.getRowCount());
		tableRowMap.put(tmp[1], mds);
	}
	
	public void updateGui(String category, String[] fs, String[] strings) {
			updateAverageTime(strings);
			updateTps(strings);
			updateErrNum(strings);
			updateStdDevTime(strings);
	}
	
	private void updateGui(TimeSeries ts,String value,String t){
		Date time = null;
		try {
			time = new Date(Long.parseLong(t));
		} catch (NumberFormatException e) {
			log.error("Error date value:" + t);
		}
		Long v = Long.parseLong(StringUtils.strip(value));
		updateGui(ts, new Second(time), v);
	}
	
	private void updateAverageTime(String[] strings){
		TimeSeries ts = dataMap.get("jmeter$$avgTime");
		updateGui(ts,strings[2],strings[0]);
	}
	
	private void updateTps(String[] strings){
		TimeSeries ts = dataMap.get("jmeter$$avgTps");
	}
	
	private void updateErrNum(String[] strings){
		TimeSeries ts = dataMap.get("jmeter$$errNum");
		updateGui(ts,strings[8],strings[0]);
	}
	
	private void updateStdDevTime(String[] strings){
		TimeSeries ts = dataMap.get("jmeter$$stdDevTime");
		updateGui(ts,strings[3],strings[0]);
	}
	
	
}
