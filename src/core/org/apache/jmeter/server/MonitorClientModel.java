package org.apache.jmeter.server;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

import org.apache.jmeter.exceptions.IllegalUserActionException;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.monitor.Monitor;
import org.apache.jmeter.monitor.gui.MonitorGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.jfree.data.time.TimeSeries;

import com.caucho.hessian.client.HessianProxyFactory;

/**
 * Monitor Client Model
 * @author chenchao.yecc
 * @version jex002A
 *
 */
public class MonitorClientModel implements Runnable{

	private String serviceUrl;
	private String project;
	private long interval = 2000;
	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}
	private RemoteDataService remoteDataService = null;
	private Map<String, ArrayList<HashMap<String,String>>> agents=null;
	private boolean running;
	private HashMap<String,Monitor> linespecMap = null;
	private int periods=30000;
	// 缓存agent，取数据时使用
	private Map<String,Map<String,String>> agentMap=new HashMap<String,Map<String,String>>();
	
	public synchronized void connect() throws MalformedURLException {
		HessianProxyFactory factory = new HessianProxyFactory();
		remoteDataService = (RemoteDataService) factory.create(
                RemoteDataService.class, this.serviceUrl);
		if (this.remoteDataService == null) {
			JOptionPane.showMessageDialog(GuiPackage.getInstance().getMainFrame(), "未连接服务器", "出错了",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		initCategoryGui();
		if(!this.running){
			this.running = true;
			new Thread(this,"aliperClientModelThread").start();
		}
	}
// 每个Monitor对应一个线程
//	private class LineDrawer implements Runnable {
//		private Monitor monitor;
//		
//		public void setMonitor(Monitor monitor){
//			this.monitor=monitor;
//		}
//		@Override
//		public void run() {
//			long pos = monitor.getDataEndPosition();
//			Map<String, String> agent = agentMap.get(monitor.getPathName());
//			MonitorData monitors = remoteDataService.getMonitorData(agent, pos);
//			if (monitors != null) {
//				// System.out.println("fetch data:"+agent.get("name"));
//				monitor.setDataEndPosition(monitors.getDataEndPosition());
//				addValuesToTimeSeries(monitors, monitor);
//			}
//		}
//	}
	
	/**
	 * 为每总数大于10000的一组数据开启一个绘图线程
	 */
	private class DataListDrawer implements Runnable {
		private MonitorData monitors;
		private Monitor mr;
		public DataListDrawer(MonitorData monitors, Monitor mr){
			this.monitors=monitors;
			this.mr=mr;
		}
		@Override
		public void run(){
			addValuesToTimeSeries(monitors,mr);
		}
	}
	
	/**
	 * 设置当前工程
	 * @param project
	 */
	public void setProject(String project){
		this.project=project;
	}
	
	@Override
	public void run() {
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		while(this.running){
			try {
				Thread.sleep(this.interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// 指定间隔获取数据
			this.fetchChartData();
		}
	}
	
	private synchronized void initCategoryGui() {
		this.linespecMap = new HashMap<String, Monitor>();
		// 初始化Agent组
		agents= remoteDataService.getProjectAgents(project);
		if (agents == null) {
			return;
		}
		// 初始化agentMap
		for (String agent : agents.keySet()) {
			ArrayList<HashMap<String,String>> monitorAgent=agents.get(agent);
			for (Map<String,String> chart : monitorAgent) {
				String chartName=agent+"$$"+chart.get("name");
				agentMap.put(chartName, chart);
			}
		}
		// 初始化Gui
		for (String agent : agents.keySet()) {
			// 为每一个Agent生成服务器Gui
			GuiPackage guiPackage = GuiPackage.getInstance();
			JMeterTreeNode benchNode = guiPackage.getCurrentNode();
			JMeterTreeNode serverNode = addAgentToTree(benchNode, agent,
					"org.apache.jmeter.server.gui.ServerGui");
			for (int i = 0; i < MonitorGui.CATEGORY.length; i++) {
				String chartName=agent+"$$"+MonitorGui.CATEGORY[i];
				
				if (MonitorGui.CATEGORY[i].equals("net")){
					MonitorData monitors=null;
					monitors = remoteDataService.getStartMonitorData(agentMap.get(chartName));
					if (monitors==null) {
						return;
					}
				}
				
				// 为每一个Agent的监控分类生成类别Gui
				JMeterTreeNode dataNode = addAgentToTree(serverNode, MonitorGui.CATEGORY[i],
				"org.apache.jmeter.monitor.gui.MonitorGui");
				
				// 初始化每一个monitor的时间序列
				if (dataNode.getUserObject() instanceof Monitor) {
					Monitor mr = (Monitor) dataNode.getUserObject();
					mr.setPathName(chartName);
					mr.setHost(agent);
					mr.setCategory(MonitorGui.CATEGORY[i]);
					mr.setTitle(MonitorGui.CATEGORY[i]);
					mr.setNumberAxis(MonitorGui.CATEGORY[i]);
					mr.initSecondValueAxis(i);
					
					// 显示的指标
					String tmp = chartName + "$$";
					String[] fs=MonitorGui.ITEM[i];
					for (int j = 0; j < fs.length; j++) {
						String name = tmp + fs[j];
						System.out.println(fs[j]);
						TimeSeries ts = new TimeSeries(fs[j],
								org.jfree.data.time.Second.class);
						ts.setMaximumItemAge(periods);
						if (!fs[j].equals("time")) {
							mr.addTimeSeries(name, ts);
						}
					}
					mr.setLineColor();
					this.linespecMap.put(chartName, mr);
				}
			}
		}
	}
	private void addValuesToTimeSeries(MonitorData monitors, Monitor mr) {
		createDoubleDateForTimeSeries(mr, monitors);
//		String chart = mr.getCategory();
//		if (chart.equals("file")) {
//			// createLongDateForTimeSeries(fs,mr,monitors);
//		} else if (chart.equals("jsat")) {
//		} else if (chart.equals("memory")) {
//			// createLongDateForTimeSeries(fs,mr,monitors);
//		} else if (chart.equals("loadavg")) {
//			createDoubleDateForTimeSeries(mr, monitors);
//		} else if (chart.equals("pid_io")) {
//			createDoubleDateForTimeSeries(mr, monitors);
//		} else if (chart.equals("cpu")) {
//			createDoubleDateForTimeSeries(mr, monitors);
//		} else if (chart.equals("io")) {
//		} else if (chart.equals("pid_cpu")) {
//		} else if (chart.equals("net")) {
//
//		}
	}
	
	private void createDoubleDateForTimeSeries(Monitor ts,
			MonitorData monitors) {
		String[] fs=monitors.getFields();
		List<String[]> values = monitors.getValues();
		for (String[] strings : values) {
			// System.out.println(StringUtils.strip(strings[i]));
			ts.updateGui(ts.getCategory(),fs, strings);
		}
	}
	
	private JMeterTreeNode addAgentToTree(JMeterTreeNode parentNode,String agent,String type){
        GuiPackage guiPackage = GuiPackage.getInstance();
        try {
            TestElement testElement = guiPackage.createTestElement(type);
            testElement.setName(agent);
            return guiPackage.getTreeModel().addComponent(testElement, parentNode);
        }
        catch (IllegalUserActionException err) {
            String msg = err.getMessage();
            if (msg == null) {
                msg=err.toString();
            }
            JMeterUtils.reportErrorToUser(msg);
        }
        catch (Exception err) {
            String msg = err.getMessage();
            if (msg == null) {
                msg=err.toString();
            }
            JMeterUtils.reportErrorToUser(msg);
        }
        return null;
    }

	private synchronized void fetchChartData() {
		if (!this.running)
			return;
		for (String element : this.linespecMap.keySet()) {
			Monitor mor = linespecMap.get(element);
			long pos = mor.getDataEndPosition();
			Map<String, String> agent = agentMap.get(element);
			MonitorData monitors=null;
			if (mor.isFirstFetch()) {
				monitors = remoteDataService.getStartMonitorData(agent);
				mor.setDataEndPosition(monitors.getDataEndPosition());
				pos = mor.getDataEndPosition();
				mor.setFirstFetch(false);
			} else {
				monitors = remoteDataService.getMonitorData(agent, pos);
			}
			if (monitors == null) {
				continue;
			}
			// System.out.println("fetch data:"+agent.get("name"));
			mor.setDataEndPosition(monitors.getDataEndPosition());
			if (monitors.getDataEndPosition() - pos > 10000) {
				// 使用多线程
				new Thread(new DataListDrawer(monitors, mor)).start();
			} else {
				// 不使用多线程
				addValuesToTimeSeries(monitors, mor);
			}
//			System.out.println(mor.getCategory()+" Line:"+monitors.getValues().size()+":Overed!");
		}
	}
	
	public List<String> getProjects(String url) throws MalformedURLException {
		HessianProxyFactory factory = new HessianProxyFactory();
        RemoteDataService remoteDataService = (RemoteDataService) factory.create(
                RemoteDataService.class, url);
		return remoteDataService.getProjects();
	}
}
