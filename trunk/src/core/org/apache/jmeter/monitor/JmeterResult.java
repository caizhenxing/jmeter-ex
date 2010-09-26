package org.apache.jmeter.monitor;

import java.io.Serializable;

import javax.swing.JPanel;

import org.apache.jmeter.testelement.AbstractTestElement;
import org.jfree.chart.ChartPanel;

public class JmeterResult extends AbstractTestElement implements Serializable {
	private static final long serialVersionUID = 1L;

	private MonitorModel monitor = null;
	
	private JPanel checkboxPanel = new JPanel();
	
	private ChartPanel chartPanel = new ChartPanel(null);

	public JmeterResult() {
		super();
	}

	public MonitorModel getMonitorModel() {
		return monitor;
	}

	public void setMonitorModel(MonitorModel monitor) {
		this.monitor = monitor;
		checkboxPanel = monitor.getCheckBoxPanel();
		chartPanel=monitor.getChartPanel();
	}
	
	public void setChartPanel(ChartPanel chartPanel){
		this.chartPanel=chartPanel;
	}
	
	public ChartPanel getChartPanel() {
		return chartPanel;
	}
	
	public JPanel getCheckBoxPanel() {
		return checkboxPanel;
	}
}

