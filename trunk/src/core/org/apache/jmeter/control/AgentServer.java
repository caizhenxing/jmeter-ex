package org.apache.jmeter.control;

import org.apache.jmeter.util.JMeterUtils;

public class AgentServer {
	private static final String RUN = JMeterUtils.getResString("as_run");
	private static final String STOP = JMeterUtils.getResString("as_stop");
	private String address = "";
	private String port = "";
	private String password = "";;
	private String project = "";
	private Integer interval = Integer.valueOf(0);
	private Long times = Long.valueOf(0L);
	private String items = "";
	private String pid = "";

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getState() {
		if (project == null || project.equals("")) {
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
		return interval==null? 0:interval;
	}

	public void setInterval(Integer interval) {
		this.interval = interval;
	}

	public Long getTimes() {
		return times;
	}

	public void setTimes(Long times) {
		this.times = times;
	}
}
