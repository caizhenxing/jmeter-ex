package org.apache.jmeter.control;

import java.util.Arrays;
import java.util.List;

import org.apache.jmeter.util.JMeterUtils;

public class AgentServer implements Comparable<AgentServer>{
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
	private String startTime= "";
	private String endTime= "";

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = JMeterUtils.LongToDate(startTime);
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(Long startTime) {
		if (startTime!=null && startTime!=0 &&interval!=null && times!=null && startTime!=null) {
			this.endTime = JMeterUtils.LongToDate(startTime+interval*times*1000);
		} else {
			this.endTime="";
		}
	}

	public String getPid() {
		return pid == null ? "" : pid;
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

	@Override
	public int compareTo(AgentServer as) {
		String anotherString = as.address;
		int len1 = address.length();
		int len2 = anotherString.length();
		int n = Math.min(len1, len2);
		char v1[] = address.toCharArray();
		char v2[] = anotherString.toCharArray();
		int k = 0;
		while (k < n) {
			char c1 = v1[k];
			char c2 = v2[k];
			if (c1 != c2) {
				return c1 - c2;
			}
			k++;
		}
		return len1 - len2;
	}
}
