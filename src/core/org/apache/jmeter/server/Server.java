package org.apache.jmeter.server;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.json.JSONException;
import org.json.JSONObject;

public class Server extends AbstractTestElement implements Serializable {

	private static final long serialVersionUID = 1L;
	static final String NEW_LINE="\n";
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
			parseCpuInfo(cpuinfo);
			String diskinfo = jsonObject.getString("disk");
			parseDiskInfo(diskinfo);
			String memoryinfo = jsonObject.getString("memory");
			parseMemInfo(memoryinfo);
			String osinfo = jsonObject.getString("os");
			parseOSInfo(osinfo);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void parseCpuInfo(String info) {
		if (info == null ||info.equals("null")||StringUtils.isBlank(info)) {
			return;
		}
		Map<String, String> kv = new HashMap<String, String>();
		// processor count:2 ;model name:Intel(R) Xeon(R) CPU E5450 @ 3.00GHz
		// ;cpu MHz:2992.632 ;cache size:6144 KB ;cpu cores:2 ;
		String[] items = info.split(";");
		for (int i = 0; i < items.length; i++) {
			String[] tmp = items[i].split(":");
			kv.put(tmp[0].trim(), tmp[1].trim());
		}
		StringBuilder sb = new StringBuilder();
		sb.append("处理器数量：").append(kv.get("processor count")).append(NEW_LINE)
				.append("型号：").append(kv.get("model name")).append(NEW_LINE)
				.append("主频：").append(kv.get("cpu MHz")).append(" MHz").append(NEW_LINE)
				.append("L2缓存大小：").append(kv.get("cache size"));
		cpu_info=sb.toString();
	}
	
	private void parseMemInfo(String info){
		if (info == null ||info.equals("null")||StringUtils.isBlank(info)) {
			return;
		}
		Map<String, String> kv = new HashMap<String, String>();
		String[] items = info.split(";");
		for (int i = 0; i < items.length; i++) {
			String[] tmp = items[i].split(":");
			kv.put(tmp[0], tmp[1].trim());
		}
		StringBuilder sb = new StringBuilder();
		sb.append("内存大小：").append(kv.get("MemTotal"));
		mem_info=sb.toString();
	}
	
	private void parseDiskInfo(String info){
		if (info == null ||info.equals("null")||StringUtils.isBlank(info)) {
			return;
		}
		// /dev/hda1:3.6G(Total),3.3G(Used); tmpfs:1014M(Total),0(Used); /dev/hdb:20G(Total),340M(Used); 
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		String[] items = info.split(";");
		int count=1;
		for (int i = 0; i < items.length; i++) {
			if (StringUtils.isBlank(items[i])) {
				continue;
			}
			String[] tmp = items[i].split(":");
			if (tmp[0].trim().equals("tmpfs")) {
				sb1.append("虚拟内存：").append(tmp[1]).append(NEW_LINE);
			} else {
				sb.append("磁盘 -").append(count).append(" ：").append(tmp[0].trim()).append(":").append(tmp[1]).append(NEW_LINE);
				count++;
			}
		}
		sb.append(sb1);
		disk_info=sb.toString();
	}
	
	private void parseOSInfo(String info){
		if (info == null ||info.equals("null")) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("操作系统信息：").append(info).append(NEW_LINE);
		os_info=sb.toString();
	}
}
