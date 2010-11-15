package org.apache.jmeter.monitor;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.jmeter.monitor.gui.MonitorGui;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;

/**
 * Monitor Model of Net
 * @since jex002A
 * @author chenchao.yecc
 *
 */
public class NetMonitorModel extends MonitorModel {

	public void updateGui(String category, String[] fs, String[] strings) {
		for (int j = 1; j < fs.length; j++) {
			String name = super.pathName + "$$" + fs[j];
			TimeSeries ts = dataMap.get(name);
			if (ts == null) {
				continue;
			}

			Date time = null;
			try {
				time = new Date(Long.parseLong(strings[0]));
			} catch (NumberFormatException e) {
				System.out.println("Error date value:" + strings[j]);
			}
			String type = MonitorGui.MONITOR_CONFIGURE.get(category).getDataType(fs[j]);
			if (type.equals(MonitorModel.TYPE_LONG)) {
				Long v = Long.parseLong(StringUtils.strip(strings[j]));
				updateGui(ts, new Second(time), v);
			} else if (type.equals(MonitorModel.TYPE_DOUBLE)) {
				Double v = null;
				v = Double.parseDouble(StringUtils.strip(strings[j + 9]));
				updateGui(ts, new Second(time), v);
			}
		}
	}
}
