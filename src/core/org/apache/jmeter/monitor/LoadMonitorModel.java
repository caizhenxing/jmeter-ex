package org.apache.jmeter.monitor;

import org.jfree.chart.axis.NumberTickUnit;

public class LoadMonitorModel extends MonitorModel{
	public LoadMonitorModel(){
		super();
		double   rangetick = 0.1D;
		//y轴单位间隔为0.1
		super.localNumberAxisL.setTickUnit(new NumberTickUnit(rangetick));
	}
}
