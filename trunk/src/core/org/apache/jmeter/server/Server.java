package org.apache.jmeter.server;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Server信息
 * 
 * @author chenchao.yecc
 * @since jex003A
 * 
 */
public class Server extends AbstractTestElement implements Serializable {

	private static final Logger log = LoggingManager.getLoggerForClass();
	private static final long serialVersionUID = 1L;
	private static final String NEW_LINE = "\n";
	private int cpu_num = 0;
	private boolean longModelEnable = false;
	private int total_mem = 0;
	private int swap_mem = 0;
	private double fre = 0;
	private int enth_num;
	private String cpu_info = "";
	private String mem_info = "";
	private String disk_info = "";
	private String net_info = "";
	private String os_info = "";

	public String getCpuInfo() {
		return cpu_info;
	}

	public String getMemoryInfo() {
		return mem_info;
	}

	public String getDiskInfo() {
		return disk_info;
	}

	public String getNetInfo() {
		return net_info;
	}

	public String getOsInfo() {
		return os_info;
	}

	public void setInfo(String info) {
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
			// String netinfo = jsonObject.getString("net");
			// parseNetInfo(netinfo);
		} catch (JSONException e) {
			log.error("Error in parsing info"+info, e);
		}
	}

	private void parseNetInfo(String info) {
		if (info == null || info.equals("null") || StringUtils.isBlank(info)) {
			return;
		}
	}

	private void parseCpuInfo(String info) {
		if (info == null || info.equals("null") || StringUtils.isBlank(info)) {
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
		sb.append(JMeterUtils.getResString("processor_count")).append(
				kv.get("processor count")).append(NEW_LINE).append(
				JMeterUtils.getResString("processor_type")).append(
				kv.get("model name")).append(NEW_LINE).append(
				JMeterUtils.getResString("frequency"))
				.append(kv.get("cpu MHz")).append(" MHz").append(NEW_LINE)
				.append(JMeterUtils.getResString("l2_cache_size")).append(
						kv.get("cache size"));
		cpu_info = sb.toString();
	}

	private void parseMemInfo(String info) {
		if (info == null || info.equals("null") || StringUtils.isBlank(info)) {
			return;
		}
		Map<String, String> kv = new HashMap<String, String>();
		String[] items = info.split(";");
		for (int i = 0; i < items.length; i++) {
			String[] tmp = items[i].split(":");
			kv.put(tmp[0], tmp[1].trim());
		}
		StringBuilder sb = new StringBuilder();
		sb.append(JMeterUtils.getResString("memory_size")).append(
				kv.get("MemTotal"));
		mem_info = sb.toString();
	}

	private void parseDiskInfo(String info) {
		if (info == null || info.equals("null") || StringUtils.isBlank(info)) {
			return;
		}
		// /dev/hda1:3.6G(Total),3.3G(Used); tmpfs:1014M(Total),0(Used);
		// /dev/hdb:20G(Total),340M(Used);
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		String[] items = info.split(";");
		int count = 1;
		for (int i = 0; i < items.length; i++) {
			if (StringUtils.isBlank(items[i])) {
				continue;
			}
			String[] tmp = items[i].split(":");
			if (tmp[0].trim().equals("tmpfs")) {
				sb1.append(JMeterUtils.getResString("swap")).append(tmp[1])
						.append(NEW_LINE);
			} else {
				sb.append(JMeterUtils.getResString("disk")).append(count)
						.append(" : ").append(tmp[0].trim()).append(":")
						.append(tmp[1]).append(NEW_LINE);
				count++;
			}
		}
		sb.append(sb1);
		disk_info = sb.toString();
	}

	private void parseOSInfo(String info) {
		if (info == null || info.equals("null")) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(JMeterUtils.getResString("version")).append(info).append(
				NEW_LINE);
		os_info = sb.toString();
	}
}
