package org.apache.jmeter.server;

import java.io.Serializable;

import org.apache.jmeter.testelement.AbstractTestElement;

public class Server extends AbstractTestElement implements Serializable {

	private static final long serialVersionUID = 1L;
	String cpu_info="";
	String mem_info="";
	String disk_info="";
	String net_info="";
	int cpu_num=0;
	boolean longModelEnable=false;
	int total_mem=0;
	int swap_mem=0;
	double fre=0;
	int enth_num;
	public void setCpuInfo(String info){
		cpu_info=info;
	}
	public String getCpuInfo(){
		return "";
	}
	public String getMemoryInfo(){
		return "";
	}
	public String getDiskInfo(){
		return "";
	}
	public String getNetInfo(){
		return "";
	}
}
