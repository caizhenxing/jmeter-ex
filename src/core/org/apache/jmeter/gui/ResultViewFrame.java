package org.apache.jmeter.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang.StringUtils;
import org.apache.jmeter.control.MonitorClientModel;
import org.apache.jmeter.control.gui.CustomItemListDialog;
import org.apache.jmeter.control.gui.ServerBenchGui;
import org.apache.jmeter.gui.tree.JMeterCellRenderer;
import org.apache.jmeter.gui.util.FileDialoger;
import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.monitor.JmeterMonitorDataStat;
import org.apache.jmeter.monitor.JmeterMonitorModel;
import org.apache.jmeter.monitor.MonitorDataStat;
import org.apache.jmeter.monitor.MonitorLine;
import org.apache.jmeter.monitor.MonitorModel;
import org.apache.jmeter.monitor.MonitorModelFactory;
import org.apache.jmeter.monitor.gui.MonitorGui;
import org.apache.jmeter.server.gui.ServerGui;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;

import com.alibaba.b2b.qa.monitor.MonitorAgent;
import com.alibaba.b2b.qa.monitor.MonitorData;
import com.alibaba.b2b.qa.monitor.MonitorProject;

/**
 * 结果查看Frame
 * @author chenchao.yecc
 * @since jex003A
 *
 */
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
	private JTextField url = new JTextField(15);
	// 树
	private JTree tree;
	private ViewTreeModel treeModel = null;
	
	// 更新按钮
	private JButton update = new JButton(JMeterUtils.getResString("server_bench_update"));
	// 查看按钮
	private JButton view = new JButton(JMeterUtils.getResString("view_start"));
	private JButton filter = new JButton(JMeterUtils.getResString("project_filter"));
	private CustomItemListDialog customizedProjectDialog = new CustomItemListDialog();
	
	private static SimpleDateFormat format= new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private JButton savegraph=new JButton(JMeterUtils.getResString("save_cur_pic"));
	private JButton saveall = new JButton(JMeterUtils.getResString("save_all_pic"));
	private long beginTime = Long.MIN_VALUE;
	private long endTime = Long.MAX_VALUE;
    private JScrollPane mainPanel=null;
    private JScrollPane treePanel = null;
	// 工程名对应的Report工程信息
	private Map<String,MonitorProject> projectMap = new HashMap<String,MonitorProject>();
	
	// 用于查看历史按钮活性设置的回调
	private ServerBenchGui benchgui=null;
	private MonitorClientModel model = null;
	
	static {
		String value=JMeterUtils.getProperty("monitor.persent");
		try{
			if (value == null) {
				throw new NumberFormatException();
			}
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
		this.setTitle(JMeterUtils.getResString("view_history_title"));
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
		 tmp.add(new JLabel(JMeterUtils.getResString("server_bench_url")),BorderLayout.WEST);
		 JPanel filterPanel = new JPanel(new FlowLayout());
		 filterPanel.add(url);
		 filterPanel.add(filter);
		 filter.addActionListener(this);
//		 filterPanel.add(Box.createHorizontalStrut(GAP),BorderLayout.EAST);
		 tmp.add(filterPanel,BorderLayout.CENTER);
		 customizedProjectDialog.setInfo(JMeterUtils.getResString("project_for_each_row"));
		 customizedProjectDialog.setTitle(JMeterUtils.getResString("add_customized_projec"));
		 url.setEditable(false);
		 tmp.add(Box.createHorizontalStrut(GAP),BorderLayout.EAST);
		 leftPanel.add(tmp);
		 tmp = new JPanel(new BorderLayout());
		 tmp.add(new JLabel(JMeterUtils.getResString("server_bench_projects")),BorderLayout.WEST);
		 tmp.add(projects,BorderLayout.CENTER);
		 JPanel bp = new HorizontalPanel();
		 bp.add(Box.createHorizontalStrut(GAP));
		 bp.add(update);
		 bp.add(Box.createHorizontalStrut(GAP));
		 tmp.add(bp,BorderLayout.EAST);
//		 tmp.add(Box.createHorizontalStrut(GAP),BorderLayout.EAST);
		 leftPanel.add(tmp);
		 tmp = new JPanel(new BorderLayout());
		 tmp.add(new JLabel(JMeterUtils.getResString("server_bench_time_parts")),BorderLayout.WEST);
		 tmp.add(times,BorderLayout.CENTER);
		 tmp.add(Box.createHorizontalStrut(GAP),BorderLayout.EAST);
		 leftPanel.add(tmp);
		 leftPanel.add(Box.createVerticalStrut(VGAP));
		 upPanel.add(leftPanel,BorderLayout.WEST);

		 // 中间面板
		 VerticalPanel midPanel = new VerticalPanel();
		 midPanel.add(Box.createVerticalStrut(GAP));
		 tmp = new JPanel(new BorderLayout());
		 tmp.add(new JLabel(JMeterUtils.getResString("start_time")),BorderLayout.WEST);
		 tmp.add(fromTf,BorderLayout.CENTER);
		 fromTf.setToolTipText(JMeterUtils.getResString("date_format")+"yyyy-MM-dd hh:mm:ss");
		 tmp.add(Box.createHorizontalStrut(GAP),BorderLayout.EAST);
		 midPanel.add(tmp);
		 tmp = new JPanel(new BorderLayout());
		 tmp.add(new JLabel(JMeterUtils.getResString("end_time")),BorderLayout.WEST);
		 toTf.setToolTipText(JMeterUtils.getResString("date_format")+"yyyy-MM-dd hh:mm:ss");
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
		DefaultTreeCellRenderer rend = new JMeterCellRenderer();
		rend.setFont(new Font("Dialog", Font.PLAIN, 11));
		tree.setCellRenderer(new DefaultTreeCellRenderer() {
			private static final long serialVersionUID = 1L;
			public Component getTreeCellRendererComponent(JTree tree,
					Object value, boolean sel, boolean expanded, boolean leaf,
					int row, boolean hasFocus) {
				super.getTreeCellRendererComponent(tree, value, sel, expanded,
						leaf, row, hasFocus);
				if (row !=0 && leaf) {
					ImageIcon icon = GUIFactory.getIcon(ServerGui.class);
					setIcon(icon);
				} else {
					ImageIcon icon = GUIFactory.getIcon(ResultViewFrame.class);
					setIcon(icon);
				}
				return this;
			}
		});
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION );
		treePanel = new JScrollPane(tree);
		treePanel.setMinimumSize(new Dimension(100, 0));
		
		treeAndMain.setLeftComponent(treePanel);
		treeAndMain.setRightComponent(mainPanel);
		
		this.getContentPane().add(upPanel, BorderLayout.NORTH);
		this.getContentPane().add(treeAndMain, BorderLayout.CENTER);
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

		if (StringUtils.isBlank(from)&& StringUtils.isBlank(to)) {
			beginTime = Long.MIN_VALUE;
			endTime = Long.MAX_VALUE;
			return true;
		} else if (StringUtils.isBlank(from) && StringUtils.isNotBlank(to)){
			return false;
		} else if (StringUtils.isNotBlank(from) && StringUtils.isBlank(to)){
			return false;
		} 
		
		try {
			Date fromtime = format.parse(from);
			beginTime = fromtime.getTime();
		} catch (ParseException e) {
			beginTime = Long.MIN_VALUE;
			return false;
		}
		try {
			Date totime = format.parse(to);
			endTime = totime.getTime();
		} catch (ParseException e) {
			endTime = Long.MAX_VALUE;
			return false;
		}
		return true;
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==update){
			// 取最新的工程列表
			projectModel.removeAllElements();
			timeMap.clear();
			List<String> nameList = this.customizedProjectDialog.getCustomizedAgentList();
			try {
				List<MonitorProject> monitors=model.getAllMonitorProject();
				for (Iterator<MonitorProject> iterator = monitors.iterator(); iterator
						.hasNext();) {
					MonitorProject monitorProject = iterator.next();
					if (!nameList.isEmpty()) {
						if (!nameList.contains(monitorProject.getProjectName())) {
							continue;
						}
					}
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
			if (this.projects.getSelectedItem()==null || times.getSelectedItem()==null) {
				return;
			}
			if (!(JOptionPane.showConfirmDialog(null, JMeterUtils.getResString("confirm_info_view"),JMeterUtils.getResString("confirm_title_view"),JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)) {
				return;
			}
			if (!checkDate()) {
				JOptionPane.showMessageDialog(GuiPackage.getInstance()
						.getMainFrame(), JMeterUtils
						.getResString("view_date_erros"), JMeterUtils
						.getResString("server_bench_error"),
						JOptionPane.ERROR_MESSAGE);
			}
//			long t = System.currentTimeMillis();
			if (true) {
				// 清空树和显示区域
				int count=treeModel.getRootNode().getChildCount();
				for (int i = 0; i < count; i++) {
					treeModel.removeNodeFromParent((MutableTreeNode) treeModel.getChild(treeModel.getRootNode(),i));
				}
				mainPanel.setViewportView(null);
				String item =(String)projects.getSelectedItem();
				if (item==null||item.equals("")) {
					return;
				}
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
					if (ip.equals("jmeter")) {
						// 单独处理Jmeter监控
						ViewTreeNode serverNode = new  ViewTreeNode(ip);
						ViewTabbedPane tab = new ViewTabbedPane();
						serverNode.setTabbedPanel(tab);
						treeModel.insertNodeInto(serverNode, treeModel.getRootNode(), treeModel.getRootNode().getChildCount());
						ArrayList<MonitorAgent> aglst= agentMap.get(ip);
						for (Iterator<MonitorAgent> iterator2 = aglst.iterator(); iterator2
						.hasNext();) {
							MonitorAgent monitorAgent = iterator2.next();
							MonitorModel monitorModel = MonitorModelFactory
							.getMonitorModel(monitorAgent.getMachineIp());
							if (monitorModel==null) {
								log.warn("Unkonw moniotr:"+ monitorAgent.getName());
								continue;
							}
							JmeterMonitorModel jmeterMonitorModel=(JmeterMonitorModel)monitorModel;
							jmeterMonitorModel.setPathName(ip + monitorAgent.getName());
							jmeterMonitorModel.setHost(monitorAgent.getName());
							jmeterMonitorModel.setCategory(ip);
							jmeterMonitorModel.setTitle("jmeter");
							jmeterMonitorModel.setNumberAxis("jmeter");
							jmeterMonitorModel.initSecondValueAxis("jmeter");
							// 显示的指标
							String tmp = ip +"$$"+ monitorAgent.getName() + "$$";
							Map<String, MonitorLine> lines = MonitorGui.MONITOR_CONFIGURE
									.get(ip).getLines();
							// 取得所有数据
							MonitorData md = model.getMonitorDataByDuration(
									monitorAgent, rt.getTimeValue(), rt
									.getEndTimeValue());
							// 取得直线
							for (Iterator<String> iterator3 = lines.keySet()
									.iterator(); iterator3.hasNext();) {
								String line = iterator3.next();
								new Thread(new JmeterChartPanelCreater(tmp, line,
										monitorAgent, md,
										jmeterMonitorModel,beginTime,endTime)).start();
							}
							
							// 初始化Tab面板
							JPanel tp = new JPanel(new BorderLayout());
							tp.add(jmeterMonitorModel.getCheckBoxPanel(),BorderLayout.NORTH);
							tp.add(jmeterMonitorModel.getChartPanel(),BorderLayout.CENTER);
							tp.add(jmeterMonitorModel.getTablePanel(),BorderLayout.SOUTH);
							tab.add(monitorAgent.getName(), tp);
						}
						continue;
					}
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
									monitorModel,beginTime,endTime)).start();
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
			}
		} else if (e.getSource() == savegraph) {
			ViewTreeNode node = (ViewTreeNode) tree
					.getLastSelectedPathComponent();
			if (node == null) {
				return;
			}
			ViewTabbedPane tab = node.getTabbedPanel();
			if (tab == null) {
				return;
			}
			JPanel p = (JPanel) tab.getSelectedComponent();
			String name = tab.getTitleAt(tab.getSelectedIndex());
			JFileChooser chooser = FileDialoger.promptToSaveFile(name + ".jpg");
			if (chooser == null) {
				return;
			}
			File out = new File(chooser.getSelectedFile().getAbsolutePath());
			ResultViewFrame.saveGraphForJPanel(p,out);
			JOptionPane.showMessageDialog(null, JMeterUtils
					.getResString("save_success"), JMeterUtils
					.getResString("table_visualizer_success"),
					JOptionPane.CLOSED_OPTION);
		} else if (e.getSource() == saveall) {
			ViewTreeNode node = (ViewTreeNode) tree
					.getLastSelectedPathComponent();
			if (node == null) {
				return;
			}
			ViewTabbedPane tab = node.getTabbedPanel();
			if (tab == null) {
				return;
			}
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.showDialog(this,"OK");
			if (chooser == null) {
				return;
			}
			int count = node.getTabbedPanel().getComponentCount();
			for (int i = 0; i < count; i++) {
				JPanel p = (JPanel) node.getTabbedPanel().getComponent(i);
				String name = node.getTabbedPanel().getTitleAt(i);
				File out = new File(chooser.getSelectedFile().getAbsolutePath()+File.separator+name+".jpg");
				ResultViewFrame.saveGraphForJPanel(p,out);
			}
			JOptionPane.showMessageDialog(null, JMeterUtils
					.getResString("save_success"), JMeterUtils
					.getResString("table_visualizer_success"),
					JOptionPane.CLOSED_OPTION);
		} else if (e.getSource() == filter) {
			customizedProjectDialog.setVisible(true);
		}
	}
	
	public static void saveGraphForJPanel(JPanel p,File out){
		BufferedImage bi = (BufferedImage) p.createImage(p.getWidth(),
				p.getHeight());
		p.paint(bi.getGraphics());
		try {
			javax.imageio.ImageIO.write(bi, "jpg", out);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
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
	
	public void valueChanged(TreeSelectionEvent e) {
		ViewTreeNode selectedNode = (ViewTreeNode)tree.getLastSelectedPathComponent();
		if (selectedNode==null) {
			return;
		}
		if (selectedNode.getUserObject().equals("Servers")) {
			return;
		}
		mainPanel.setViewportView(selectedNode.getTabbedPanel());
	}
	
	class JmeterChartPanelCreater implements Runnable {
		private MonitorAgent monitorAgent = null;
		private MonitorData md = null;
		private JmeterMonitorModel monitorModel = null;
		private String ip = null;
		private String line = null;
		private boolean goon = true;
		private boolean isLast = false;
		private List<String> lst = null;
		private DataMergeService service = null;
		private long beginTime = Long.MIN_VALUE;
		private long endTime = Long.MAX_VALUE;

		public JmeterChartPanelCreater(String ip, String line,
				MonitorAgent monitorAgent, MonitorData md,
				JmeterMonitorModel monitorModel,long beginTime,long endTime) {
			this.ip = ip;
			this.line = line;
			this.monitorAgent = monitorAgent;
			this.md = md;
			this.monitorModel = monitorModel;
			this.endTime = endTime;
			this.beginTime = beginTime;
			
			// 设置可以容忍的百分比
			service = new DataMergeService(ResultViewFrame.persent);
			lst = Arrays.asList(md.getFields());
		}

		public void run() {
			String name = ip + line;
			if (!MonitorGui.MONITOR_CONFIGURE.get("jmeter").getShowType(line).equals(
					"-")) {
				TimeSeries ts = new TimeSeries(line,
						org.jfree.data.time.Second.class);
				ts.setMaximumItemAge(50000);
				JmeterMonitorDataStat mds = new JmeterMonitorDataStat();
				List<String[]> results = md.getValues();
				int nmb = lst.indexOf(line);
				// System.out.println(name+"'s count is: "+results.size());
				int count = 0;
				for (Iterator<String[]> iterator4 = results.iterator(); iterator4
						.hasNext();) {
					String[] values = iterator4.next();
					String strings = values[nmb];
					Date time = null;
					try {
						time = new Date(Long.parseLong(values[0]));
						// 判断time是否在指定的区间
						if (time.getTime() < beginTime) {
							continue;
						}
						if (time.getTime() >= endTime) {
						    if (!goon) {
						        isLast = true;
                            }
							goon = false;
						}
						if (isLast) {
                            break;
                        }
					} catch (NumberFormatException ne) {
						System.out.println("Error date value:");
						log.error("Error date value:" + values[0]);
					}
					if (line.equals("avgTime")) {
					    Long v = Long.parseLong(StringUtils.strip(strings));
					    mds.addData(values);
					    if (service.isInsertData(time, v, iterator4.hasNext()
					            && goon)) {
					        ts.addOrUpdate(new Second(time), v);
					        count++;
					    }
                    } else if (line.equals("avgTps")){
                        Double v = Double.parseDouble(StringUtils.strip(strings));
                        mds.addData(values);
                        if (service.isInsertData(time, v, iterator4.hasNext()
                                && goon)) {
                            ts.addOrUpdate(new Second(time), v);
                            count++;
                        }
                    }
				}
				monitorModel.addLazyTimeSeries(name, ts);
				// 只保留一个总结果
				synchronized (this) {
				        monitorModel.addRowToTable(name, mds);
                }
			}
		}
	}
	class ChartPanelCreater implements Runnable {
		private MonitorAgent monitorAgent = null;
		private MonitorData md = null;
		private MonitorModel monitorModel = null;
		private String ip = null;
		private String line = null;
		private boolean goon = true;
		private boolean isLast = false;
		private List<String> lst = null;
		private DataMergeService service = null;
		private long beginTime = Long.MIN_VALUE;
		private long endTime = Long.MAX_VALUE;

		public ChartPanelCreater(String ip, String line,
				MonitorAgent monitorAgent, MonitorData md,
				MonitorModel monitorModel,long beginTime,long endTime) {
			this.ip = ip;
			this.line = line;
			this.monitorAgent = monitorAgent;
			this.md = md;
			this.monitorModel = monitorModel;
			this.endTime = endTime;
			this.beginTime = beginTime;
			this.goon = true;
			
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
				ts.setMaximumItemAge(50000*30);
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
						// 判断time是否在指定的区间
						if (time.getTime() < beginTime){
							continue;
						}
						if (time.getTime() >= endTime) {
                            if (!goon) {
                                isLast = true;
                            }
                            goon = false;
                        }
                        if (isLast) {
                            break;
                        }
					} catch (NumberFormatException ne) {
						System.out.println("Error date value:");
						log.error("Error date value:"+values[0]);
					}
					String type = MonitorGui.MONITOR_CONFIGURE.get(
							monitorAgent.getName()).getDataType(line);
					if (type.equals(MonitorModel.TYPE_LONG)) {
						Long v = Long.parseLong(StringUtils.strip(strings));
						mds.addData(v);
						if (service.isInsertData(time, v, iterator4.hasNext() && goon)) {
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
						if (service.isInsertData(time,v,iterator4.hasNext() && goon)) {
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
