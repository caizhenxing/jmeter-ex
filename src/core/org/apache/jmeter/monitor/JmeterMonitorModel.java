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
	public synchronized void addTimeSeries(String name, TimeSeries ts) {
//		if (MonitorGui.MONITOR_CONFIGURE.get(category).getYAxisCount() == 1) {
			localTimeSeriesCollectionL.addSeries(ts);
//		} else {
//			String[] tmp = name.split("\\$\\$");
//			String item = tmp[2];
//			String state = MonitorGui.MONITOR_CONFIGURE.get(category).getShowType(item);
//			if (state.equals("1")) {
//				localTimeSeriesCollectionL.addSeries(ts);
//			} else if(state.equals("2")) {
//				localTimeSeriesCollectionR.addSeries(ts);
//			} else {
//			}
//		}
		dataMap.put(name, ts);
		String[] tmp = name.split("\\$\\$");
		JCheckBox jb = super.createChooseCheckBox(tmp[1], Color.BLACK);
		super.checkboxPanel.add(jb);
		cbMap.put(jb, tmp[1]);
		// 增加列表行
		MonitorDataStat mds=new MonitorDataStat();
		mds.setLabel(tmp[1]);
		model.insertRow(mds, model.getRowCount());
		tableRowMap.put(tmp[1], mds);
	}
	
	public void updateGui(String category, String[] fs, String[] strings) {
		for (int j = 1; j < fs.length; j++) {
			String name = pathName + "$$" + fs[j];
			TimeSeries ts = dataMap.get(name);
			if (ts == null) {
				continue;
			}

			Date time = null;
			try {
				time = new Date(Long.parseLong(strings[0]));
			} catch (NumberFormatException e) {
				log.error("Error date value:" + strings[j]);
			}
			Long v = Long.parseLong(StringUtils.strip(strings[j]));
			updateGui(ts, new Second(time), v);
		}
	}
}
