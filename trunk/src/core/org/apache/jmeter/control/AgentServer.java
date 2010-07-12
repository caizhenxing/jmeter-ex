package org.apache.jmeter.control;

import org.apache.jmeter.util.JMeterUtils;

public class AgentServer {
	private static final String RUN=JMeterUtils.getResString("as_run");
	private static final String STOP=JMeterUtils.getResString("as_stop");
	private String address;
	private String port;
	private String password;
	private String project;
	private int interval;
	private int times;
	private String items;
	
	public String getState() {
		if (project==null || project.equals("")) {
			return STOP;
		}
		return RUN;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public String getItems() {
		return items;
	}
	public void setItems(String items) {
		this.items = items;
	}
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}
	public int getTimes() {
		return times;
	}
	public void setTimes(int times) {
		this.times = times;
	}
}
