package org.apache.jmeter.monitor;

/**
 * 创建监控模型
 * @since jex002A
 * @author chenchao.yecc
 *
 */
public class MonitorModelFactory {
	public static MonitorModel getMonitorModel(String type){
		if (type.equals("file")) {
			return new FileMonitorModel();
		} else if (type.equals("jstat_heap")) {
			return new JHeapMonitorModel();
		} else if (type.equals("jstat_gc")) {
			return new JGCMonitorModel();
		} else if (type.equals("memory")) {
			return new MemoryMonitorModel();
		} else if (type.equals("loadavg")) {
			return new LoadMonitorModel();
		} else if (type.equals("pid_io")) {
			return new PIdMonitorModel();
		} else if (type.equals("cpu")) {
			return new CpuMonitorModel();
		} else if (type.equals("io")) {
			return new IOMonitorModel();
		} else if (type.equals("pid_cpu")) {
			return new PIdMonitorModel();
		} else if (type.equals("net")) {
			return new NetMonitorModel();
		} else if (type.equals("jmeter")) {
			return new JmeterMonitorModel();
		} else {
			return null;
		}
	}
}
