package org.apache.jmeter.control;

import java.awt.Frame;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

import org.apache.jmeter.control.gui.ProgressSample;
import org.apache.jmeter.exceptions.IllegalUserActionException;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.monitor.Monitor;
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
	private RemoteControllerService remoteControllerService = null;
	private Map<String, ArrayList<HashMap<String,String>>> agents=null;
	private boolean running;
	private HashMap<String,Monitor> linespecMap = new HashMap<String, Monitor>();
	// 用于AgentServer和RemoteAgent得对应
	private Map<AgentServer,RemoteAgent> remoteAgentMap = new HashMap<AgentServer,RemoteAgent>();
	private int periods=30000;
	private Thread dataFetcher=null;
	private List<RemoteAgent> agentList = null;
	HessianProxyFactory factory=new HessianProxyFactory();

	// 缓存agent，取数据时使用
	private Map<String,Map<String,String>> agentMap=new HashMap<String,Map<String,String>>();
	
	public Map<AgentServer,RemoteAgent> getRemoteAgentMap(){
		return remoteAgentMap;
	}
	
	public void stopAgent(RemoteAgent agent){
		try {
			String[] items=new String[agent.getRunAgents().size()];
			for (int i = 0; i < agent.getRunAgents().size(); i++) {
				items[i]=agent.getRunAgents().get(i);
			}
			remoteControllerService.stopAgents(agent, items);
			Thread.sleep(3000);
		} catch (AgentConnectionError e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void startAgent(RemoteAgent agent, List<String> items,String param) {
		// 启动工程
		Thread thread = new Thread() {
			public void run() {
				int index = 0;

				while (index < 5) {
					try {
						sleep(1000);
						System.out.println(++index);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};

		ProgressSample.show((Frame) null, thread, "Agent启动中", "成功", "启动成功");
		try {
			remoteControllerService.startProject(agent, agent.getRunProject());
			Thread.sleep(1000);
			// 启动Agent
			for (Iterator<String> iterator = items.iterator(); iterator
			.hasNext();) {
				try {
					remoteControllerService.startAgent(agent, iterator.next(), agent.getInterval(),
							agent.getCount(), param);
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
	
	public synchronized List<AgentServer> configure() throws MalformedURLException{
		remoteAgentMap.clear();
		List<AgentServer> resList=new ArrayList<AgentServer>();
		remoteControllerService = (RemoteControllerService) factory.create(
				RemoteControllerService.class, "http://10.249.129.159:8080/monitor/remote/remoteControllerService");
		// 获得所有的服务器
		agentList=remoteControllerService.getAllAgents();
		if (agentList!=null) {
			for (Iterator<RemoteAgent> iterator = agentList.iterator(); iterator.hasNext();) {
				RemoteAgent agentServer = iterator.next();
				AgentServer s=new AgentServer();
				s.setAddress(agentServer.getAddress());
				s.setPort(String.valueOf(agentServer.getPort()));
				s.setProject(agentServer.getRunProject());
				// 判断Agent的运行状态
				if (agentServer.getRunProject()==null||agentServer.getRunProject().equals("")) {
					s.setState(AgentServer.STOP);
				} else {
					s.setState(AgentServer.RUN);
				}
				s.setPassword(agentServer.getPassword());
				s.setInterval(agentServer.getInterval());
				s.setTimes(agentServer.getCount());
				s.setStartTime(agentServer.getProjectStartTime());
				s.setEndTime(agentServer.getProjectStartTime());
				if (agentServer.getRunAgents().size()!=0) {
					StringBuilder sb = new StringBuilder();
					for (Iterator<String> itr = agentServer.getRunAgents().iterator(); itr.hasNext();) {
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
	
	public List<String> getAllProcess(AgentServer tmpAgent){
		List<String> lst=new ArrayList<String>();
		try {
			lst=remoteControllerService.getProcessList(remoteAgentMap.get(tmpAgent));
		} catch (AgentConnectionError e) {
			e.printStackTrace();
		}
		return lst; 
	}
	
	public synchronized boolean connect() throws MalformedURLException {
		remoteDataService = (RemoteDataService) factory.create(
                RemoteDataService.class, this.serviceUrl);
		if (this.remoteDataService == null) {
			JOptionPane.showMessageDialog(GuiPackage.getInstance().getMainFrame(),JMeterUtils.getResString("server_bench_connect_error"), JMeterUtils.getResString("server_bench_error"),
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		if(!initCategoryGui()){
			JOptionPane.showMessageDialog(GuiPackage.getInstance().getMainFrame(), JMeterUtils.getResString("server_bench_error_projects"), JMeterUtils.getResString("server_bench_error"),
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if(!this.running){
			this.running = true;
			dataFetcher=new Thread(this,"aliperClientModelThread");
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
	
	private synchronized boolean initCategoryGui() {
		// 清空原数据
		agentMap.clear();
		linespecMap.clear();

		// 初始化Agent组
		agents= remoteDataService.getProjectAgents(project);
		if (agents == null) {
			return false;
		}
		// 初始化agentMap
		for (String agent : agents.keySet()) {
			ArrayList<HashMap<String,String>> monitorAgent=agents.get(agent);
			for (Map<String,String> chart : monitorAgent) {
				String chartName=agent+"$$"+chart.get("name");
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

//				if (MonitorGui.CATEGORY[i].equals("net")) {
//					MonitorData monitors = null;
//					monitors = remoteDataService.getStartMonitorData(agentMap
//							.get(chartName));
//					if (monitors == null) {
//						continue;
//					}
//				}

				if(!agentMap.keySet().contains(chartName)){
					continue;
				}
				// 为每一个Agent的监控分类生成类别Gui
				JMeterTreeNode dataNode = addAgentToTree(serverNode,
						JMeterUtils.getResString(Monitor.PRE_TITLE + MonitorGui.CATEGORY[i]),
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
					String[] fs = MonitorGui.ITEM[i];
					for (int j = 1; j < fs.length; j++) {
						String name = tmp + fs[j];
						// System.out.println(fs[j]);
						if(!MonitorGui.LINE_COLLECTION[i][j].equals("-")){
							TimeSeries ts = new TimeSeries(fs[j],
									org.jfree.data.time.Second.class);
							ts.setMaximumItemAge(periods);
							mr.addTimeSeries(name, ts);
						}
					}
					mr.setLineColor();
					this.linespecMap.put(chartName, mr);
				}
			}
		}
		return true;
	}
	private void addValuesToTimeSeries(MonitorData monitors, Monitor mr) {
		String[] fs=monitors.getFields();
		List<String[]> values = monitors.getValues();
		for (String[] strings : values) {
			// System.out.println(StringUtils.strip(strings[i]));
			mr.updateGui(mr.getCategory(),fs, strings);
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
				if (monitors == null) {
					continue;
				}
				mor.setDataEndPosition(monitors.getDataEndPosition());
				pos = mor.getDataEndPosition();
				mor.setFirstFetch(false);
			} else {
				monitors = remoteDataService.getMonitorData(agent, pos);
				if (monitors == null) {
					continue;
				}
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
	
	public synchronized void disConnect(){
		running=false;
	}
}
