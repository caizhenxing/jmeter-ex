package org.apache.jmeter.monitor;

import org.jfree.chart.axis.NumberTickUnit;

public class LoadMonitorModel extends MonitorModel{
	public LoadMonitorModel(){
		super();
		double   rangetick = 0.1D;
		//y轴单位间隔为0.1
		super.localNumberAxisL.setTickUnit(new NumberTickUnit(rangetick));
		super.localNumberAxisL.setAutoRange(true);    //自动设置数据轴数据范围
		super.localNumberAxisL.setLowerBound(0D);    //自动设置数据轴数据范围
//		super.localNumberAxisL.setAutoRangeMinimumSize(0.1D); //自动设置数据轴数据范围时数据范围的最小跨度

	}
}
