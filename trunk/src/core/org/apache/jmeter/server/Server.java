package org.apache.jmeter.server;

import java.io.Serializable;

import org.apache.jmeter.testelement.AbstractTestElement;
import org.json.JSONException;
import org.json.JSONObject;

public class Server extends AbstractTestElement implements Serializable {

	private static final long serialVersionUID = 1L;
	String cpu_info="";
	String mem_info="";
	String disk_info="";
	String net_info="";
	String os_info="";
	int cpu_num=0;
	boolean longModelEnable=false;
	int total_mem=0;
	int swap_mem=0;
	double fre=0;
	int enth_num;
	
	public String getCpuInfo(){
		return cpu_info;
	}
	
	public String getMemoryInfo(){
		return mem_info;
	}
	
	public String getDiskInfo(){
		return disk_info;
	}
	
	public String getNetInfo(){
		return net_info;
	}
	
	public String getOsInfo(){
		return os_info;
	}
	
	public void setInfo(String info){
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(info);
			String cpuinfo = jsonObject.getString("cpu");
			String diskinfo = jsonObject.getString("disk");
			String memoryinfo = jsonObject.getString("memory");
			String osinfo = jsonObject.getString("os");
			cpu_info=cpuinfo;
			disk_info=diskinfo;
			mem_info=memoryinfo;
			os_info=osinfo;
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
}
