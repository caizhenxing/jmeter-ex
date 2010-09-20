package org.apache.jmeter.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.lang.reflect.UndeclaredThrowableException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang.StringUtils;
import org.apache.jmeter.control.MonitorClientModel;
import org.apache.jmeter.control.gui.ServerBenchGui;
import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.monitor.MonitorDataStat;
import org.apache.jmeter.monitor.MonitorLine;
import org.apache.jmeter.monitor.MonitorModel;
import org.apache.jmeter.monitor.MonitorModelFactory;
import org.apache.jmeter.monitor.gui.MonitorGui;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.jfree.chart.ChartPanel;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;

import com.alibaba.b2b.qa.monitor.MonitorAgent;
import com.alibaba.b2b.qa.monitor.MonitorData;
import com.alibaba.b2b.qa.monitor.MonitorProject;

public class ResultViewFrame extends JFrame implements ActionListener,ItemListener,TreeSelectionListener{
	
	private static final Logger log = LoggingManager.getLoggerForClass();
	private static double persent = 0D;
	private static final long serialVersionUID = 1L;
	private static int GAP = 8;
	private static int VGAP = 3;
	// 工程列表
	private DefaultComboBoxModel projectModel = new DefaultComboBoxModel();
	private JComboBox projects=new JComboBox(projectModel);
	// 工程名对应的Report时间
	private Map<String,DefaultComboBoxModel> timeMap = new HashMap<String,DefaultComboBoxModel>();
	// 时间段列表
	private JComboBox times=new JComboBox();
	// 开始时间
	private JTextField fromTf = new JTextField(12);
	// 结束时间
	private JTextField toTf = new JTextField(12);
	// 服务器URL
	private JTextField url = new JTextField(30);
	// 树
	private JTree tree;
	private ViewTreeModel treeModel = null;
	
	// 更新按钮
	private JButton update = new JButton("更新工程");
	// 查看按钮
	private JButton view = new JButton("开始查看");
	
	private static SimpleDateFormat format= new  SimpleDateFormat("mm-dd hh:MM");
	private JButton savegraph=new JButton("保存当前图片");
	private JButton saveall = new JButton("保存所有图片");
	private long beginTime=0;
	private long endTime=0;
    private JScrollPane mainPanel=null;
    private JScrollPane treePanel = null;
	private YccTab tab = new YccTab();
	// 工程名对应的Report工程信息
	private Map<String,MonitorProject> projectMap = new HashMap<String,MonitorProject>();
	
	// 用于查看历史按钮活性设置的回调
	private ServerBenchGui benchgui=null;
	private MonitorClientModel model = null;
	
	static {
		String value=JMeterUtils.getProperty("monitor.persent");
		try{
			persent=Double.parseDouble(value);
		}catch (NumberFormatException e){
			log.warn("the value of monitor.persent is invalid!use default value: 10");
			persent=10D;
		}
	}
	public void setServerBenchGui(ServerBenchGui benchgui){
		this.benchgui=benchgui;
	}
	
	public void setMonitorClientModel(MonitorClientModel model){
		this.model=model;
	}
	
	public boolean showFrame() {
		// 设置服务器URL
		url.setText(benchgui.getCurrentServerUrl());
		this.pack();
		// 初始化控件
		this.clearAll();
		JMeterUtils.centerWindow(this);
		return true;
	}
	
	private void clearAll() {
		// 清空日期
		fromTf.setText("");
		toTf.setText("");
		projectModel.removeAllElements();
		timeMap.clear();
		projects.removeAllItems();
		times.removeAllItems();
	}
	
	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			benchgui.setHistoryButtonEnable(true);
		}
		super.processWindowEvent(e);
	}

	public ResultViewFrame() {
		update.addActionListener(this);
		view.addActionListener(this);
		savegraph.addActionListener(this);
		saveall.addActionListener(this);
		projects.addItemListener(this);
		this.setTitle("历史数据查看器");
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		initGui();
	}
	
	private void initGui(){
		
		// 控制区域
		 JPanel upPanel=new JPanel(new BorderLayout());
		
		 // 上层面板
		 // 左边面板
		 VerticalPanel leftPanel = new VerticalPanel();
		 leftPanel.add(Box.createVerticalStrut(VGAP));
		 JPanel tmp = new JPanel(new BorderLayout());
		 tmp = new JPanel(new BorderLayout());
		 tmp.add(new JLabel("服务器URL："),BorderLayout.WEST);
		 tmp.add(url,BorderLayout.CENTER);
		 url.setEditable(false);
		 tmp.add(Box.createHorizontalStrut(GAP),BorderLayout.EAST);
		 leftPanel.add(tmp);
		 tmp = new JPanel(new BorderLayout());
		 tmp.add(new JLabel("工程名字："),BorderLayout.WEST);
		 tmp.add(projects,BorderLayout.CENTER);
		 JPanel bp = new HorizontalPanel();
		 bp.add(Box.createHorizontalStrut(GAP));
		 bp.add(update);
		 bp.add(Box.createHorizontalStrut(GAP));
		 tmp.add(bp,BorderLayout.EAST);
//		 tmp.add(Box.createHorizontalStrut(GAP),BorderLayout.EAST);
		 leftPanel.add(tmp);
		 tmp = new JPanel(new BorderLayout());
		 tmp.add(new JLabel("工程时段："),BorderLayout.WEST);
		 tmp.add(times,BorderLayout.CENTER);
		 tmp.add(Box.createHorizontalStrut(GAP),BorderLayout.EAST);
		 leftPanel.add(tmp);
		 leftPanel.add(Box.createVerticalStrut(VGAP));
		 upPanel.add(leftPanel,BorderLayout.WEST);
		 
		 // 中间面板
		 VerticalPanel midPanel = new VerticalPanel();
		 midPanel.add(Box.createVerticalStrut(GAP));
		 tmp = new JPanel(new BorderLayout());
		 tmp.add(new JLabel("开始时间："),BorderLayout.WEST);
		 tmp.add(fromTf,BorderLayout.CENTER);
		 tmp.add(Box.createHorizontalStrut(GAP),BorderLayout.EAST);
		 midPanel.add(tmp);
		 tmp = new JPanel(new BorderLayout());
		 tmp.add(new JLabel("结束时间："),BorderLayout.WEST);
		 tmp.add(toTf,BorderLayout.CENTER);
		 tmp.add(Box.createHorizontalStrut(GAP),BorderLayout.EAST);
		 midPanel.add(tmp);
		 bp = new HorizontalPanel();
		 bp.add(Box.createHorizontalStrut(GAP));
		 bp.add(savegraph);
		 bp.add(Box.createHorizontalStrut(GAP));
		 bp.add(saveall);
		 bp.add(Box.createHorizontalStrut(GAP));
		 bp.add(view);
		 bp.add(Box.createHorizontalStrut(GAP));
		 midPanel.add(bp);
		 
		 upPanel.add(midPanel,BorderLayout.CENTER);
		 
		// 图形区域
		JSplitPane treeAndMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		mainPanel=new JScrollPane();
		
		// 初始化树
		ViewTreeNode rootNode = new  ViewTreeNode("Servers");
		treeModel = new ViewTreeModel(rootNode);
		rootNode.setAllowsChildren(true);
		tree=new JTree(treeModel);
		tree.addTreeSelectionListener(this);
		tree.setShowsRootHandles(true);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION );
		treePanel = new JScrollPane(tree);
		treePanel.setMinimumSize(new Dimension(100, 0));
		
		treeAndMain.setLeftComponent(treePanel);
		treeAndMain.setRightComponent(mainPanel);
		
		this.getContentPane().add(upPanel, BorderLayout.NORTH);
		this.getContentPane().add(treeAndMain, BorderLayout.CENTER);
	}
	
	class YccTab extends JTabbedPane{
		private static final long serialVersionUID = 1L;
		JTabbedPane subTab = new JTabbedPane();
		public void addSubTabPanel(String title,ChartPanel chart){
			super.addTab(title, chart);
		}
		
		public void removeAllSubTabPanel(){
			super.removeAll();
		}
	}
	
	public static void main(String[] args) {
		long i=System.currentTimeMillis();
		ResultViewFrame r=new ResultViewFrame();
		r.pack();
		r.setTitle("YccTry");
		r.setVisible(true);
		System.out.println(System.currentTimeMillis()-i);
	}

	private boolean checkDate() {
		String from = fromTf.getText();
		String to = toTf.getText();
		// null验证
		if (StringUtils.isEmpty(from)) {
			// 开始日期为空
			return false;
		}
		if (StringUtils.isEmpty(to)) {
			// 结束日期为空
			return false;
		}

		try {
			Date fromtime = format.parse(from);
			beginTime = fromtime.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
		try {
			Date totime = format.parse(to);
			endTime = totime.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==update){
			// 取最新的工程列表
			projectModel.removeAllElements();
			timeMap.clear();
			try {
				List<MonitorProject> monitors=model.getAllMonitorProject();
				for (Iterator<MonitorProject> iterator = monitors.iterator(); iterator
						.hasNext();) {
					MonitorProject monitorProject = iterator.next();
					projectMap.put(monitorProject.getProjectName(), monitorProject);
					projectModel.addElement(monitorProject.getProjectName());
				}
			} catch (UndeclaredThrowableException e1) {
				JOptionPane.showMessageDialog(GuiPackage.getInstance()
						.getMainFrame(), JMeterUtils
						.getResString("server_bench_failed")
						+ model.getServiceUrl(), JMeterUtils
						.getResString("server_bench_error"),
						JOptionPane.ERROR_MESSAGE);
			}
		} else if(e.getSource()==view){
//			if (checkDate()) {
//			long t = System.currentTimeMillis();
			if (true) {
				// 清空树和显示区域
				int count=treeModel.getRootNode().getChildCount();
				for (int i = 0; i < count; i++) {
					treeModel.removeNodeFromParent((MutableTreeNode) treeModel.getChild(treeModel.getRootNode(),i));
				}
				mainPanel.setViewportView(null);
				String item =(String)projects.getSelectedItem();
				ReportTimeItem rt = (ReportTimeItem)times.getSelectedItem();
				// 设定结束时间
				MonitorProject pro = projectMap.get(item);
				List<String> tmplst = Arrays.asList(pro.getReportTimeList());
				int index = tmplst.indexOf(rt.getTime());
				if (index+1 != tmplst.size()) {
					rt.setEndTime(tmplst.get(index+1));
				} else {
					rt.setEndTime("");
				}
				
				// 取得所有的监控项
				Map<String, ArrayList<MonitorAgent>>  agentMap = model.getProjectMonitorAgentsWithReportTime(item, rt.getTime());
				for (Iterator<String> iterator = agentMap.keySet().iterator(); iterator
						.hasNext();) {
					String ip = iterator.next();
					// 添加Server树节点
					ViewTreeNode serverNode = new  ViewTreeNode(ip);
					ViewTabbedPane tab = new ViewTabbedPane();
					serverNode.setTabbedPanel(tab);
					treeModel.insertNodeInto(serverNode, treeModel.getRootNode(), treeModel.getRootNode().getChildCount());
					ArrayList<MonitorAgent> aglst= agentMap.get(ip);
					for (Iterator<MonitorAgent> iterator2 = aglst.iterator(); iterator2
							.hasNext();) {
						MonitorAgent monitorAgent = iterator2.next();
						MonitorModel monitorModel = MonitorModelFactory
								.getMonitorModel(monitorAgent.getName());
						if (monitorModel==null) {
							log.warn("Unkonw moniotr:"+ monitorAgent.getName());
							continue;
						}
						monitorModel.setPathName(ip + monitorAgent.getName());
						monitorModel.setHost(ip);
						monitorModel.setCategory(monitorAgent.getName());
						monitorModel.setTitle(monitorAgent.getName());
						monitorModel.setNumberAxis(monitorAgent.getName());
						monitorModel.initSecondValueAxis(monitorAgent.getName());

						// 显示的指标
						String tmp = ip +"$$"+ monitorAgent.getName() + "$$";
						Map<String, MonitorLine> lines = MonitorGui.MONITOR_CONFIGURE
								.get(monitorAgent.getName()).getLines();
						// 取得所有数据
						MonitorData md = model.getMonitorDataByDuration(
								monitorAgent, rt.getTimeValue(), rt
								.getEndTimeValue());
						for (Iterator<String> iterator3 = lines.keySet()
								.iterator(); iterator3.hasNext();) {
							String line = iterator3.next();
							
							new Thread(new ChartPanelCreater(tmp, line,
									monitorAgent, md,
									monitorModel)).start();
						}
						
						// 初始化Tab面板
						JPanel tp = new JPanel(new BorderLayout());
						tp.add(monitorModel.getCheckBoxPanel(),BorderLayout.NORTH);
						tp.add(monitorModel.getChartPanel(),BorderLayout.CENTER);
						tp.add(monitorModel.getTablePanel(),BorderLayout.SOUTH);
						tab.add(monitorAgent.getName(), tp);
					}
				}

				// 展开所有树节点
				for (int i = 0; i < tree.getRowCount(); i++) {
					tree.expandRow(i);
				}
//				System.out.println(System.currentTimeMillis()-t);
//			    tree.setRootVisible(false);
//				Map<String,ChartPanel> chartMap = model.getChartPanel((String)this.projects.getSelectedItem(),beginTime,endTime);
			}
//			model.getAllDataForProject(agent, startTime, stopTime);
//			startField.getText();
//			endField.getText();
//			model.view();
		} else if(e.getSource()==savegraph){
			System.out.println("savegraph");
		} else if(e.getSource()==saveall){
			System.out.println("saveall");
		}
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource()==projects) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				// 更新Report时间
				String item  = (String)projects.getSelectedItem();
				if (timeMap.keySet().contains(item)) {
					times.setModel(timeMap.get(item));
				} else {
					DefaultComboBoxModel tmodel = new DefaultComboBoxModel();
					timeMap.put(item, tmodel);
					String[] str=projectMap.get(item).getReportTimeList();
					for (int i = 0; i < str.length; i++) {
						ReportTimeItem rt = new ReportTimeItem();
						rt.setTime(str[i]);
						tmodel.addElement(rt);
					}
					times.setModel(timeMap.get(item));
				}
			}
			
		}
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		// TODO Auto-generated method stub
		ViewTreeNode selectedNode = (ViewTreeNode)tree.getLastSelectedPathComponent();
		if (selectedNode==null) {
			return;
		}
		if (selectedNode.getUserObject().equals("Servers")) {
			return;
		}
		mainPanel.setViewportView(selectedNode.getTabbedPanel());
	}
	
	class ChartPanelCreater implements Runnable {
		private MonitorAgent monitorAgent = null;
		private MonitorData md = null;
		private MonitorModel monitorModel = null;
		private String ip = null;
		private String line = null;
		private List<String> lst = null;
		private DataMergeService service = null;

		public ChartPanelCreater(String ip, String line,
				MonitorAgent monitorAgent, MonitorData md,
				MonitorModel monitorModel) {
			this.ip = ip;
			this.line = line;
			this.monitorAgent = monitorAgent;
			this.md = md;
			this.monitorModel = monitorModel;
			
			// 设置可以容忍的百分比
			service = new DataMergeService(ResultViewFrame.persent);
			lst = Arrays.asList(md.getFields());
		}

		public void run() {
			String name = ip + line;
			if (!MonitorGui.MONITOR_CONFIGURE.get(monitorAgent.getName())
					.getShowType(line).equals("-")) {
				TimeSeries ts = new TimeSeries(line,
						org.jfree.data.time.Second.class);
				ts.setMaximumItemAge(50000);
				MonitorDataStat mds=new MonitorDataStat();
				List<String[]> results = md.getValues();
				int nmb = lst.indexOf(line);
//				System.out.println(name+"'s count is: "+results.size());
				int count=0;
				for (Iterator<String[]> iterator4 = results.iterator(); iterator4
						.hasNext();) {
					String[] values = iterator4.next();
					String strings = values[nmb];
					Date time = null;
					try {
						time = new Date(Long.parseLong(values[0]));
					} catch (NumberFormatException ne) {
						System.out.println("Error date value:");
						log.error("Error date value:"+values[0]);
					}
					String type = MonitorGui.MONITOR_CONFIGURE.get(
							monitorAgent.getName()).getDataType(line);
					if (type.equals(MonitorModel.TYPE_LONG)) {
						Long v = Long.parseLong(StringUtils.strip(strings));
						mds.addData(v);
						if (service.isInsertData(time,v,iterator4.hasNext())) {
							ts.addOrUpdate(new Second(time), v);
							count++;
						}
					} else if (type.equals(MonitorModel.TYPE_DOUBLE)) {
						Double v = null;
						if (monitorAgent.getName().equals("net")) {
							v = Double.parseDouble(StringUtils
									.strip(values[nmb + 9]));
						} else {
							v = Double.parseDouble(StringUtils
									.strip(values[nmb]));
						}
						mds.addData(v);
						if (service.isInsertData(time,v,iterator4.hasNext())) {
							ts.addOrUpdate(new Second(time), v);
							count++;
						}
					}
				}
//				System.out.println(name+" 's draw count is: "+count);
				monitorModel.addLazyTimeSeries(name, ts);
				monitorModel.addRowToTable(name, mds);
			}
		}
	}
	
	class DataMergeService {
	    private double          acceptAngle;                               //可以接受的误差度数  

	    private DataPoint       firstPoint    = null;
	    private DataPoint       secondPoint   = null;
	    private double          prevSlope;
	    private double          preLineValue;

	    public DataMergeService(double angle){
	    	this.acceptAngle=angle;
	    }
	    
	    public void setAcceptAngle(double acceptAngle) {
	        this.acceptAngle = acceptAngle;
	    }

	    public double getAcceptAngle() {
	        return acceptAngle;
	    }

	    /**
	     * 增加数据点
	     * 
	     * @param time
	     * @param value
	     */
	    public boolean isInsertData(Date time, double value, boolean last) {
	        DataPoint point = new DataPoint();
	        point.setTime(time.getTime());
	        point.setValue(value);
	        return addData(point,last);
	    }

	    /**
	     * 增加数据点
	     * 
	     * @param point
	     */
	    private boolean addData(DataPoint point,boolean last) {
	        if (null != firstPoint && null != secondPoint) {
	            //计算按照原先斜率得到的第三个点坐标
	            DataPoint point3 = new DataPoint();
	            point3.setTime(point.getTime());
	            double value = preLineValue + prevSlope * point.getTime();
	            point3.setValue(value);

//	            double slope2 = getSlope(secondPoint, point);

//	            double angle = Math.toDegrees(angle(prevSlope, slope2));
	            double angle = Math.abs(((point.getValue()-value)/value)*100);

	            if (angle <= acceptAngle) { //如果误差在可接受范围内
	                secondPoint = point3;
	                if (last) {
	                	return false;
					} else {
						return true;
					}
	            } else {
//	                System.out.println(angle);
//	                mergeDataList.add(copyPoint(secondPoint));
	                firstPoint = secondPoint;
	                secondPoint = point;
	                parseValue();
	                return true;
	            }
	        } else {
	            if (null == firstPoint) {
	                firstPoint = point;
//	                mergeDataList.add(point);
	                return true;
	            } else if (null == secondPoint) {
	                secondPoint = point;
	                parseValue();
	                return true;
	            } else {
	            	return false;
	            }
	        }
	    }

	    /**
	     * 根据2个直线的斜率算出夹角
	     * 
	     * @param slope1
	     * @param slope2
	     * @return
	     */
	    private double angle(double slope1, double slope2) {
	        double tanAngle = Math.abs((slope2 - slope1) / (1 + slope1 * slope2));
	        return Math.atan(tanAngle);
	    }

	    private void parseValue() {
	        prevSlope = getSlope(firstPoint, secondPoint);
	        preLineValue = getLineValue(firstPoint, secondPoint);
	    }

	    /**
	     * 得到2个点之间的斜率
	     * 
	     * @param point1
	     * @param point2
	     * @return
	     */
	    private double getSlope(DataPoint point1, DataPoint point2) {
	        return (point2.getValue() - point1.getValue()) / (point2.getTime() - point1.getTime());
	    }

	    /**
	     * 得到2个点表达式的常量
	     * 
	     * @param point1
	     * @param point2
	     * @return
	     */
	    private double getLineValue(DataPoint point1, DataPoint point2) {
	        double y1 = point1.getValue();
	        double y2 = point2.getValue();
	        long x1 = point1.getTime();
	        long x2 = point2.getTime();
	        return (x1 * y2 - x2 * y1) / (x1 - x2);
	    }
	}

	private static class ReportTimeItem{
		private static SimpleDateFormat reportFormat= new  SimpleDateFormat("yyyyMMddHHmmss");
		String time="";
		String endTime="";
		String formatTime="";
		
		public long getTimeValue(){
			long value = -1L;
			try {
				Date date = reportFormat.parse(time);
				value = date.getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return value;
		}
		
		public long getEndTimeValue(){
			long value = -1L;
			if (endTime.equals("")) {
				value = System.currentTimeMillis();
			} else {
				try {
					Date date = reportFormat.parse(endTime);
					value = date.getTime();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			return value;
		}
		
		public String getEndTime(){
			return endTime;
		}
		
		public String toString(){
			return formatTime;
		}
		
		public void setTime(String time){
			this.time=time;
			formatTime = getConvertDate(time);
		}
		
		public void setEndTime(String end){
			endTime=end;
		}
		
		public String getTime(){
			return time;
		}
		
		private static String getConvertDate(String str){
			StringBuilder sb=new StringBuilder();
			sb.append(str.subSequence(0, 4)).append("-").append(str.subSequence(4, 6)).append("-").append(str.subSequence(6, 8));
			sb.append(" ").append(str.subSequence(8, 10)).append(":").append(str.subSequence(10, 12)).append(":").append(str.subSequence(12, 14));
			return sb.toString();
		}
	}
}
