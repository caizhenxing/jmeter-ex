package org.apache.jmeter.monitor;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class LoadMonitorModel extends MonitorModel{
	public LoadMonitorModel(){
		super();
		NumberFormat nf22 = new DecimalFormat("00.00"); 
		super.localNumberAxisL.setNumberFormatOverride(nf22);
	}
}
