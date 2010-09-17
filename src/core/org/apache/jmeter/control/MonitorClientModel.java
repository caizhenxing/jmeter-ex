package org.apache.jmeter.control;

import java.awt.BasicStroke;
import java.awt.Font;
import java.lang.reflect.UndeclaredThrowableException;
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
import org.apache.jmeter.monitor.MonitorLine;
import org.apache.jmeter.monitor.MonitorModel;
import org.apache.jmeter.monitor.MonitorModelFactory;
import org.apache.jmeter.monitor.gui.MonitorGui;
import org.apache.jmeter.server.Server;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import com.alibaba.b2b.qa.monitor.MonitorAgent;
import com.alibaba.b2b.qa.monitor.MonitorData;
import com.alibaba.b2b.qa.monitor.MonitorProject;
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
	private boolean modified = false;
	private long interval = 2000;
	private RemoteDataService remoteDataService = null;
	private RemoteControllerService remoteControllerService = null;
	private Map<String, ArrayList<MonitorAgent>> agents = null;
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

	public void modifiedServerURL(){
		modified=true;
	}
	
	public RemoteDataService getRemoteDataService(){
		if (remoteDataService == null||modified) {
			initDataService();
			modified=false;
		}
		return remoteDataService;
	}
	
	private RemoteControllerService getRemoteControllerService(){
		if (remoteControllerService == null||modified) {
			initControlService();
			modified=false;
		}
		return remoteControllerService;
	}
	
	// 缓存agent，取数据时使用
	private Map<String, MonitorAgent> agentMap = new HashMap<String, MonitorAgent>();

	public MonitorClientModel() {
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

	public String getServiceUrl(){
		return serviceUrl;
	}
	
	public List<MonitorProject> getAllMonitorProject() {
		List<MonitorProject> monitors = getRemoteDataService()
				.getAllMonitorProject();
		return monitors == null ? new ArrayList<MonitorProject>() : monitors;
	}
	
	public Map<String, ArrayList<MonitorAgent>> getProjectMonitorAgentsWithReportTime(String projectName, String reportTime){
		return getRemoteDataService().getProjectMonitorAgentsWithReportTime(projectName,reportTime);
	}
	
	public MonitorData getMonitorDataByDuration(MonitorAgent ma, long start, long end){
		return getRemoteDataService().getMonitorDataByDuration(ma,start,end);
	}
	
	public void stopProject(RemoteAgent agent) {
		try {
			getRemoteControllerService().stopProject(agent, agent.getRunProject());
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
			getRemoteControllerService().stopAgents(agent, items);
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
			System.out.println(getRemoteControllerService().startProject(agent, agent.getRunProject()));
			Thread.sleep(1000);
			// 启动Agent
			for (Iterator<String> iterator = items.iterator(); iterator
					.hasNext();) {
				try {
					System.out.println(getRemoteControllerService().startAgent(agent, iterator.next(),
							agent.getInterval(), agent.getCount(), param));
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
			throws MalformedURLException,UndeclaredThrowableException {
		remoteAgentMap.clear();
		List<AgentServer> resList = new ArrayList<AgentServer>();
		// 获得所有的服务器
		agentList = getRemoteControllerService().getAllAgents();
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
			lst = getRemoteControllerService().getProcessList(remoteAgentMap
					.get(tmpAgent));
		} catch (AgentConnectionError e) {
			e.printStackTrace();
		}
		return lst;
	}
	
	private void initControlService(){
		try {
			remoteControllerService  = (RemoteControllerService) factory
			.create(RemoteControllerService.class, HTTP_HEADER + serviceUrl
					+ CONTROL_SERVER);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	private void initDataService(){
		try {
			remoteDataService = (RemoteDataService) factory
			.create(RemoteDataService.class, HTTP_HEADER + serviceUrl
					+ DATA_SERVER);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized boolean connect() throws MalformedURLException {
		if (getRemoteDataService()==null) {
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
				System.out.println("Can't get data from remote machine!");
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
			m.getTablePanel().removeAll();
		}
		guiList.clear();

		// 初始化Agent组
		agents = getRemoteDataService().getProjectMonitorAgents(project);
		if (agents == null || agents.isEmpty()) {
			return false;
		}
		
		// 初始化agentMap
		for (String agent : agents.keySet()) {
			ArrayList<MonitorAgent> monitorAgent = agents.get(agent);
			for (MonitorAgent chart : monitorAgent) {
				String chartName = agent + "$$" + chart.getName();
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

			// 获取所有的Agent所在服务器的信息
			agentList = getRemoteControllerService().getAllAgents();
			
			// 为ServerNode指定Server的硬件信息
			if (serverNode.getUserObject() instanceof Server) {
				Server server = (Server) serverNode.getUserObject();
				// 取得当前Agent对应的RemoteAgent
				RemoteAgent ra = null;
				for (Iterator<RemoteAgent> iterator = agentList.iterator(); iterator
						.hasNext();) {
					ra = iterator.next();
					if (ra.getAddress().equals(agent)) {
						break;
					}
				}
				// 取得硬件信息
				String info="";
				try {
					info = getRemoteControllerService().getAgentMachineInfo(ra);
				} catch (AgentConnectionError e) {
					e.printStackTrace();
				}
				// 将信息设定给Server
				server.setInfo(info);
			}
			for (Iterator<String> iterator = MonitorGui.MONITOR_CONFIGURE.keySet().iterator(); iterator.hasNext();) {
				String category = iterator.next();
				String chartName = agent + "$$" + category;
				if (!agentMap.keySet().contains(chartName)) {
					continue;
				}
				// 为每一个Agent的监控分类生成类别Gui
				JMeterTreeNode dataNode = addAgentToTree(serverNode,
						JMeterUtils.getResString(MonitorModel.PRE_TITLE
								+ category),
						"org.apache.jmeter.monitor.gui.MonitorGui");

				// 初始化每一个monitor的时间序列
				if (dataNode.getUserObject() instanceof Monitor) {
					MonitorModel model = MonitorModelFactory
							.getMonitorModel(category);
					Monitor mr = (Monitor) dataNode.getUserObject();
					mr.setMonitorModel(model);
					model.setPathName(chartName);
					model.setHost(agent);
					model.setCategory(category);
					model.setTitle(category);
					model.setNumberAxis(category);
					model.initSecondValueAxis(category);

					// 显示的指标
					String tmp = chartName + "$$";
					Map<String, MonitorLine> lines=MonitorGui.MONITOR_CONFIGURE.get(category).getLines();
					for (Iterator<String> iterator2 = lines.keySet().iterator(); iterator2
							.hasNext();) {
						String line=iterator2.next();
						String name = tmp + line;
						// System.out.println(fs[j]);
						if (!MonitorGui.MONITOR_CONFIGURE.get(category).getShowType(line).equals("-")) {
							TimeSeries ts = new TimeSeries(line,
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
					com.getTablePanel().add(mr.toString(),
							model.getTablePanel());

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
			MonitorAgent agent = agentMap.get(element);
			MonitorData monitors = null;
			if (mor.getMonitorModel().isFirstFetch()) {
				monitors = getRemoteDataService().getStartMonitorData(agent);
				if (monitors == null) {
					continue;
				}
				mor.getMonitorModel().setDataEndPosition(
						monitors.getDataEndPosition());
				pos = mor.getMonitorModel().getDataEndPosition();
				mor.getMonitorModel().setFirstFetch(false);
			} else {
				monitors = getRemoteDataService().getMonitorData(agent, pos);
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
		return getRemoteDataService().getProjects();
	}
	
	public Map<String,ChartPanel> getChartPanel(String project,long start,long end){
		Map<String,ChartPanel> map=new HashMap<String,ChartPanel>();
		Map<String, ArrayList<MonitorAgent>> servers = getRemoteDataService().getProjectMonitorAgents(project);
		if (servers == null) {
			return map;
		}
		for (String agentIp : servers.keySet()) {
			ArrayList<MonitorAgent> lst=servers.get(agentIp);
			for (Iterator<MonitorAgent> iterator = lst.iterator(); iterator.hasNext();) {
				MonitorAgent agent = iterator.next();
				MonitorData data=this.getRemoteDataService().getStartMonitorData(agent);
				System.out.println(agentIp+":name="+agent.getName());
				data = getRemoteDataService().getMonitorDataByDuration(agent, start, end);
				System.out.println(agentIp + data.getValues().size());
//				long num=data.getDataEndPosition();
//				int el=(int)num%10000;
//				int time=(int)num/10000;
//				for (int i = 0; i< time; i++) {
//					System.out.println("get date from "+(1+i*10000)+" to "+((i+1)*10000));
//				}
//				System.out.println("get date from "+(1+time*10000)+" to "+(time*10000+el));
			}
		}
		
		DateAxis localDateAxis = new DateAxis("TT");

		// 设置左侧主轴
		NumberAxis localNumberAxisL = new NumberAxis("");
		localNumberAxisL.setTickLabelFont(new Font("SansSerif", 0, 12));
		localNumberAxisL.setLabelFont(new Font("SansSerif", 0, 14));

		localDateAxis.setTickLabelFont(new Font("SansSerif", 0, 12));
		localDateAxis.setLabelFont(new Font("SansSerif", 0, 14));
		localDateAxis.setDateFormatOverride(new SimpleDateFormat("mm:ss:SSS"));

		XYLineAndShapeRenderer localXYLineAndShapeRendererL = new XYLineAndShapeRenderer(true, false);
		localXYLineAndShapeRendererL.setSeriesStroke(0, new BasicStroke(1.0F,
				0, 2));
		// XYPlot localXYPlot = new XYPlot(localTimeSeriesCollectionL,
		// localDateAxis, localNumberAxisL, localXYLineAndShapeRendererL);
		XYPlot localXYPlot = new XYPlot();
		TimeSeriesCollection localTimeSeriesCollectionL = new TimeSeriesCollection();

		localXYPlot.setDataset(0, localTimeSeriesCollectionL);
		localXYPlot.setRenderer(0, localXYLineAndShapeRendererL);
		localXYPlot.setDomainAxis(localDateAxis);
		localXYPlot.setRangeAxis(0, localNumberAxisL);
		localDateAxis.setAutoRange(true);
		localDateAxis.setLowerMargin(0.0D);
		localDateAxis.setUpperMargin(0.0D);
		localDateAxis.setTickLabelsVisible(true);
		localNumberAxisL.setStandardTickUnits(NumberAxis
				.createIntegerTickUnits());
		JFreeChart localJFreeChart = new JFreeChart("", new Font("SansSerif", 1, 24),
				localXYPlot, true);
		ChartPanel chartPanel = new ChartPanel(localJFreeChart, true);
	
//		addTimeSeries(localTimeSeriesCollectionL);
		return map;
	}
	

	public synchronized boolean view() {
		if (getRemoteDataService()==null) {
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
		Map<String, ArrayList<HashMap<String, String>>> allAgent = getRemoteDataService().getProjectAgents("R");
		for (String agent : allAgent.keySet()) {
			ArrayList<HashMap<String, String>> monitorAgent = allAgent.get(agent);
			for (Map<String, String> chart : monitorAgent) {
				MonitorData monitors = null;
//				monitors = getRemoteDataService().getMonitorDataByDuration(chart, st,en);
				System.out.println(monitors);
			}
		}
		return true;
	}
	
	public synchronized void disConnect() {
		running = false;
	}
	public static void main(String[] args) {
		long num=33289;
		int el=(int)num%10000;
		int time=(int)num/10000;
		for (int i = 0; i< time; i++) {
			System.out.println("get date from "+(1+i*10000)+" to "+((i+1)*10000));
		}
		System.out.println("get date from "+(1+time*10000)+" to "+(time*10000+el));
	}
}
