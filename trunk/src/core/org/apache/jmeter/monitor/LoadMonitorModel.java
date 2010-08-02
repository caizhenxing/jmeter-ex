package org.apache.jmeter.monitor;

public class LoadMonitorModel extends MonitorModel{
	public LoadMonitorModel(){
		super();
		super.localNumberAxisL.setAutoRange(true);    //自动设置数据轴数据范围
	}
}
