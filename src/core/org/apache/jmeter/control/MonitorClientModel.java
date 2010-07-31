package org.apache.jmeter.control;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.apache.jmeter.exceptions.IllegalUserActionException;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.monitor.Monitor;
import org.apache.jmeter.monitor.MonitorModel;
import org.apache.jmeter.monitor.MonitorModelFactory;
import org.apache.jmeter.monitor.gui.MonitorGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.jfree.data.time.TimeSeries;

import com.alibaba.b2b.qa.monitor.MonitorData;
import com.alibaba.b2b.qa.monitor.RemoteAgent;
import com.alibaba.b2b.qa.monitor.remote.RemoteControllerService;
import com.alibaba.b2b.qa.monitor.remote.RemoteDataService;
import com.alibaba.b2b.qa.monitor.remote.exception.AgentConnectionError;
import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.io.HessianProtocolException;

/**
 * Monitor Client Model
 * 
 * @author chenchao.yecc
 * @version jex002A
 * 
 */
public class MonitorClientModel implements Runnable {

	public static final String HTTP_HEADER = "http://";
	public static final String DATA_SERVER = "/monitor/remote/remoteDataService";
	public static final String CONTROL_SERVER = "/monitor/remote/remoteControllerService";
	private String serviceUrl;
	private String project;
	private long interval = 2000;
	private RemoteDataService remoteDataService = null;
	private RemoteControllerService remoteControllerService = null;
	private Map<String, ArrayList<HashMap<String, String>>> agents = null;
	private boolean running;
	private HashMap<String, Monitor> linespecMap = new HashMap<String, Monitor>();
	// 用于AgentServer和RemoteAgent得对应
	private Map<AgentServer, RemoteAgent> remoteAgentMap = new HashMap<AgentServer, RemoteAgent>();
	private int periods = 30000;
	private Thread dataFetcher = null;
	private List<RemoteAgent> agentList = null;
	private List<MonitorGui> guiList = new ArrayList<MonitorGui>();
	private HessianProxyFactory factory = new HessianProxyFactory();
	private String pid = "";

	// 缓存agent，取数据时使用
	private Map<String, Map<String, String>> agentMap = new HashMap<String, Map<String, String>>();

	public MonitorClientModel() {
		// TODO 从配置文件中读取monitor.interval的配置
		String value=JMeterUtils.getProperty("monitor.interval");
		int interval=JMeterUtils.StringToInt(value);
		if (interval != 0 && interval >= 2) {
			this.interval = interval * 1000;
		}
	}

	public Map<AgentServer, RemoteAgent> getRemoteAgentMap() {
		return remoteAgentMap;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getPid() {
		return pid;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public MonitorData getAllDataForProject(Map<String,String> agent,long startTime,long stopTime){
		MonitorData monitors = remoteDataService.getMonitorDataByDuration(agent, startTime, stopTime);
		return monitors;
	}
	
	public void stopProject(RemoteAgent agent) {
		try {
			remoteControllerService.stopProject(agent, agent.getRunProject());
			Thread.sleep(3000);
		} catch (AgentConnectionError e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void stopAgent(RemoteAgent agent) {
		try {
			String[] items = new String[agent.getRunAgents().size()];
			for (int i = 0; i < agent.getRunAgents().size(); i++) {
				items[i] = agent.getRunAgents().get(i);
			}
			remoteControllerService.stopAgents(agent, items);
			Thread.sleep(3000);
		} catch (AgentConnectionError e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void startAgent(RemoteAgent agent, List<String> items, String param) {
		// 启动工程
		try {
			remoteControllerService.startProject(agent, agent.getRunProject());
			Thread.sleep(1000);
			// 启动Agent
			for (Iterator<String> iterator = items.iterator(); iterator
					.hasNext();) {
				try {
					remoteControllerService.startAgent(agent, iterator.next(),
							agent.getInterval(), agent.getCount(), param);
				} catch (AgentConnectionError e) {
					e.printStackTrace();
				}
			}
			Thread.sleep(3000);
		} catch (AgentConnectionError e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public synchronized List<AgentServer> configure()
			throws MalformedURLException {
		remoteAgentMap.clear();
		List<AgentServer> resList = new ArrayList<AgentServer>();
		remoteControllerService = (RemoteControllerService) factory.create(
				RemoteControllerService.class, HTTP_HEADER + serviceUrl
						+ CONTROL_SERVER);
		// 获得所有的服务器
		agentList = remoteControllerService.getAllAgents();
		if (agentList != null) {
			for (Iterator<RemoteAgent> iterator = agentList.iterator(); iterator
					.hasNext();) {
				RemoteAgent agentServer = iterator.next();
				AgentServer s = new AgentServer();
				s.setAddress(agentServer.getAddress());
				s.setPort(String.valueOf(agentServer.getPort()));
				s.setProject(agentServer.getRunProject());
				s.setPid(agentServer.getPid());
				// 判断Agent的运行状态
				if (agentServer.getRunProject() == null
						|| agentServer.getRunProject().equals("")) {
					s.setState(AgentServer.STOP);
				} else {
					s.setState(AgentServer.RUN);
				}
				s.setPassword(agentServer.getPassword());
				s.setInterval(agentServer.getInterval());
				s.setTimes(agentServer.getCount());
				s.setStartTime(agentServer.getProjectStartTime());
				s.setEndTime(agentServer.getProjectStartTime());
				if (agentServer.getRunAgents().size() != 0) {
					StringBuilder sb = new StringBuilder();
					for (Iterator<String> itr = agentServer.getRunAgents()
							.iterator(); itr.hasNext();) {
						sb.append(itr.next());
						if (itr.hasNext()) {
							sb.append(",");
						}
					}
					s.setItems(sb.toString());
				}
				resList.add(s);
				remoteAgentMap.put(s, agentServer);
			}
		}
		return resList;
	}

	public List<String> getAllProcess(AgentServer tmpAgent) {
		List<String> lst = new ArrayList<String>();
		try {
			lst = remoteControllerService.getProcessList(remoteAgentMap
					.get(tmpAgent));
		} catch (AgentConnectionError e) {
			e.printStackTrace();
		}
		return lst;
	}

	public synchronized boolean view() {
		if (!initDataService()) {
			JOptionPane.showMessageDialog(GuiPackage.getInstance()
					.getMainFrame(), JMeterUtils
					.getResString("server_bench_connect_error"), JMeterUtils
					.getResString("server_bench_error"),
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 
		Date start=null;
		Date end=null;
		try {
			start= sdf.parse("2010-07-13 01:44:00");
			end= sdf.parse("2010-07-13 01:47:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long st=start.getTime();
		long en=end.getTime();
		Map<String, ArrayList<HashMap<String, String>>> allAgent = remoteDataService.getProjectAgents("R");
		for (String agent : allAgent.keySet()) {
			ArrayList<HashMap<String, String>> monitorAgent = allAgent.get(agent);
			for (Map<String, String> chart : monitorAgent) {
				MonitorData monitors = null;
				monitors = remoteDataService.getMonitorDataByDuration(chart, st,en);
				System.out.println(monitors);
			}
		}
		return true;
	}
	
	private boolean initDataService(){
		boolean res=true;
		try {
			remoteDataService = (RemoteDataService) factory
			.create(RemoteDataService.class, HTTP_HEADER + serviceUrl
					+ DATA_SERVER);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			res=false;
		}
		return res;
	}
	
	public synchronized boolean connect() throws MalformedURLException {
		if (!initDataService()) {
			JOptionPane.showMessageDialog(GuiPackage.getInstance()
					.getMainFrame(), JMeterUtils
					.getResString("server_bench_connect_error"), JMeterUtils
					.getResString("server_bench_error"),
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		if (!initCategoryGui()) {
			JOptionPane.showMessageDialog(GuiPackage.getInstance()
					.getMainFrame(), JMeterUtils
					.getResString("server_bench_error_projects"), JMeterUtils
					.getResString("server_bench_error"),
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if (!this.running) {
			this.running = true;
			dataFetcher = new Thread(this, "MonitorThread");
			dataFetcher.start();
		}
		// 等待数据加载
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}

	 // 每个Monitor对应一个线程
//	private class LineDrawer implements Runnable {
//
//		public void run() {
//			
//		}
//	}

	/**
	 * 为每总数大于10000的一组数据开启一个绘图线程
	 */
	private class DataListDrawer implements Runnable {
		private MonitorData monitors;
		private Monitor mr;

		public DataListDrawer(MonitorData monitors, Monitor mr) {
			this.monitors = monitors;
			this.mr = mr;
		}

		public void run() {
			addValuesToTimeSeries(monitors, mr);
		}
	}

	/**
	 * 设置当前工程
	 * 
	 * @param project
	 */
	public void setProject(String project) {
		this.project = project;
	}

	public void run() {

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		while (this.running) {
			try {
				Thread.sleep(this.interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// 指定间隔获取数据
			try {
				this.fetchChartData();
			} catch (Exception e) {
				System.out.println("Over");
			}
		}
	}

	private synchronized boolean initCategoryGui() {
		// 清空原数据
		agentMap.clear();
		linespecMap.clear();
		for (Iterator<MonitorGui> iterator = guiList.iterator(); iterator
				.hasNext();) {
			MonitorGui m = iterator.next();
			m.getMainPanel().removeAll();
			m.getCheckBoxPanel().removeAll();
		}
		guiList.clear();

		// 初始化Agent组
		agents = remoteDataService.getProjectAgents(project);
		if (agents == null) {
			return false;
		}
		// 初始化agentMap
		for (String agent : agents.keySet()) {
			ArrayList<HashMap<String, String>> monitorAgent = agents.get(agent);
			for (Map<String, String> chart : monitorAgent) {
				String chartName = agent + "$$" + chart.get("name");
				agentMap.put(chartName, chart);
			}
		}

		// 为每一个Agent生成ServerGui
		GuiPackage guiPackage = GuiPackage.getInstance();
		JMeterTreeNode benchNode = guiPackage.getCurrentNode();

		// 清空当前结点下的所有子节点
		int count = benchNode.getChildCount();
		for (int i = 0; i < count; i++) {
			JMeterTreeNode tmpNode = (JMeterTreeNode) benchNode.getChildAt(i);
			TestElement testElement = tmpNode.getTestElement();
			guiPackage.getTreeModel().removeNodeFromParent(tmpNode);
			guiPackage.removeNode(testElement);
		}
		for (String agent : agents.keySet()) {
			// 初始化Gui
			// 新建ServerGui
			JMeterTreeNode serverNode = addAgentToTree(benchNode, agent,
					"org.apache.jmeter.server.gui.ServerGui");
			for (int i = 0; i < MonitorGui.CATEGORY.length; i++) {
				String chartName = agent + "$$" + MonitorGui.CATEGORY[i];
				if (!agentMap.keySet().contains(chartName)) {
					continue;
				}
				// 为每一个Agent的监控分类生成类别Gui
				JMeterTreeNode dataNode = addAgentToTree(serverNode,
						JMeterUtils.getResString(MonitorModel.PRE_TITLE
								+ MonitorGui.CATEGORY[i]),
						"org.apache.jmeter.monitor.gui.MonitorGui");

				// 初始化每一个monitor的时间序列
				if (dataNode.getUserObject() instanceof Monitor) {
					MonitorModel model = MonitorModelFactory
							.getMonitorModel(MonitorGui.CATEGORY[i]);
					Monitor mr = (Monitor) dataNode.getUserObject();
					mr.setMonitorModel(model);
					model.setPathName(chartName);
					model.setHost(agent);
					model.setCategory(MonitorGui.CATEGORY[i]);
					model.setTitle(MonitorGui.CATEGORY[i]);
					model.setNumberAxis(MonitorGui.CATEGORY[i]);
					model.initSecondValueAxis(i);

					// 显示的指标
					String tmp = chartName + "$$";
					String[] fs = MonitorGui.ITEM[i];
					for (int j = 1; j < fs.length; j++) {
						String name = tmp + fs[j];
						// System.out.println(fs[j]);
						if (!MonitorGui.LINE_COLLECTION[i][j].equals("-")) {
							TimeSeries ts = new TimeSeries(fs[j],
									org.jfree.data.time.Second.class);
							ts.setMaximumItemAge(periods);
							model.addTimeSeries(name, ts);
						}
					}
					model.setLineColor();

					// 将model的组件追加到Gui上
					MonitorGui com = (MonitorGui) GuiPackage.getInstance()
							.getGui(dataNode.getTestElement());
					com.getMainPanel()
							.add(mr.toString(), model.getChartPanel());
					com.getCheckBoxPanel().add(mr.toString(),
							model.getCheckBoxPanel());

					// 缓存Monitor与MonitorGui
					this.linespecMap.put(chartName, mr);
					guiList.add(com);
				}
			}
		}
		return true;
	}

	private void addValuesToTimeSeries(MonitorData monitors, Monitor mr) {
		String[] fs = monitors.getFields();
		List<String[]> values = monitors.getValues();
		for (String[] strings : values) {
			// System.out.println(StringUtils.strip(strings[i]));
			mr.getMonitorModel().updateGui(mr.getMonitorModel().getCategory(),
					fs, strings);
		}
	}

	private JMeterTreeNode addAgentToTree(JMeterTreeNode parentNode,
			String agent, String type) {
		GuiPackage guiPackage = GuiPackage.getInstance();
		try {
			TestElement testElement = guiPackage.createTestElement(type);
			testElement.setName(agent);
			return guiPackage.getTreeModel().addComponent(testElement,
					parentNode);
		} catch (IllegalUserActionException err) {
			String msg = err.getMessage();
			if (msg == null) {
				msg = err.toString();
			}
			JMeterUtils.reportErrorToUser(msg);
		} catch (Exception err) {
			String msg = err.getMessage();
			if (msg == null) {
				msg = err.toString();
			}
			JMeterUtils.reportErrorToUser(msg);
		}
		return null;
	}

	private synchronized void fetchChartData() throws HessianProtocolException {
		if (!this.running) {
			return;
		}
		for (String element : this.linespecMap.keySet()) {
			Monitor mor = linespecMap.get(element);
			long pos = mor.getMonitorModel().getDataEndPosition();
			Map<String, String> agent = agentMap.get(element);
			MonitorData monitors = null;
			if (mor.getMonitorModel().isFirstFetch()) {
				monitors = remoteDataService.getStartMonitorData(agent);
				if (monitors == null) {
					continue;
				}
				mor.getMonitorModel().setDataEndPosition(
						monitors.getDataEndPosition());
				pos = mor.getMonitorModel().getDataEndPosition();
				mor.getMonitorModel().setFirstFetch(false);
			} else {
				monitors = remoteDataService.getMonitorData(agent, pos);
				if (monitors == null) {
					continue;
				}
			}
			mor.getMonitorModel().setDataEndPosition(
					monitors.getDataEndPosition());
			if (monitors.getDataEndPosition() - pos > 10000) {
				// 使用多线程
				new Thread(new DataListDrawer(monitors, mor)).start();
			} else {
				// 不使用多线程
				addValuesToTimeSeries(monitors, mor);
			}
		}
	}

	public List<String> getProjects(String url) throws MalformedURLException {
		remoteDataService = (RemoteDataService) factory
				.create(RemoteDataService.class, HTTP_HEADER + serviceUrl
						+ DATA_SERVER);
		return remoteDataService.getProjects();
	}

	public synchronized void disConnect() {
		running = false;
	}
}
