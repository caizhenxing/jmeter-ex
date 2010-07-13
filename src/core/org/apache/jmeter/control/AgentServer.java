package org.apache.jmeter.control;

import java.util.Arrays;
import java.util.List;

import org.apache.jmeter.util.JMeterUtils;

public class AgentServer {
	public static final String RUN = JMeterUtils.getResString("as_run");
	public static final String STOP = JMeterUtils.getResString("as_stop");
	public static final String READY = JMeterUtils.getResString("as_ready");
	private String address = "";
	private String port = "";
	private String password = "";;
	private String project = "";
	private Integer interval = Integer.valueOf(0);
	private Long times = Long.valueOf(0L);
	private String items = "";
	private String pid = "";
	private String state;

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public void setState(String s){
		state=s;
	}
	
	public String getState() {
		return state;
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
		if (project==null ||project.equals("")) {			
			return 0;
		} else {
			return interval==null? 0:interval;
		}
	}

	public void setInterval(Integer interval) {
		this.interval = interval;
	}

	public Long getTimes() {
		if (project==null ||project.equals("")) {			
			return 0L;
		} else {
			return times;
		}
	}

	public void setTimes(Long times) {
		this.times = times;
	}
	
	public List<String> getItemAsList(){
		return Arrays.asList(items.split(","));
	}
}
