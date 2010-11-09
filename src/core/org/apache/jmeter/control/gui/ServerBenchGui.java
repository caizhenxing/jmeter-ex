package org.apache.jmeter.control.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.apache.jmeter.control.AgentServer;
import org.apache.jmeter.control.MonitorClientModel;
import org.apache.jmeter.control.UserProcess;
import org.apache.jmeter.gui.AbstractJMeterGuiComponent;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.ResultViewFrame;
import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.gui.util.YccCustomTable;
import org.apache.jmeter.testelement.ServerBench;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.ObjectTableModel;
import org.apache.jorphan.gui.RendererUtils;
import org.apache.jorphan.gui.layout.VerticalLayout;
import org.apache.jorphan.reflect.Functor;

import com.alibaba.b2b.qa.monitor.RemoteAgent;

/**
 * Server Bench
 * 
 * @since jex002A
 * @author chenchao.yecc
 */
public class ServerBenchGui extends AbstractJMeterGuiComponent implements ActionListener,KeyListener{
	
	private static final long serialVersionUID = 1L;
	private JComboBox com = new JComboBox();
	private JTextField rangeField = new JTextField(58);
//	private JTextField startField = new JTextField(1);
//	private JTextField endField = new JTextField(1);
	private JTextField ipchoice=new JTextField(10);
	private ResultViewFrame resultFrame=null;
	private JButton update = new JButton(JMeterUtils.getResString("server_bench_update"));
	private JButton connect = new JButton(JMeterUtils.getResString("server_bench_connect"));
	private JButton disConnect = new JButton(JMeterUtils.getResString("server_bench_disconnect"));
	private JButton view = new JButton(JMeterUtils.getResString("view_history"));
	private JButton configure = new JButton(JMeterUtils.getResString("server_bench_configure"));
	private JButton show = new JButton(JMeterUtils.getResString("server_bench_watch"));
	private JButton edit = new JButton(JMeterUtils.getResString("server_bench_edit"));
	private JButton stop = new JButton(JMeterUtils.getResString("server_bench_stop"));
	private JButton stopProject = new JButton(JMeterUtils.getResString("server_bench_stop_pro"));
	private JButton startBT = new JButton(JMeterUtils.getResString("server_bench_start"));
	private JButton choice=new JButton(JMeterUtils.getResString("find"));
	private ConfigurDialog confDialog = new ConfigurDialog();
	private ProcessListDialog proDialog=new ProcessListDialog();
	private MonitorClientModel model = new MonitorClientModel();
	private Map<Integer,AgentServer> agentSeverContainer = new HashMap<Integer,AgentServer>();
	private transient ObjectTableModel tableModel;
	private JTable agentTable = null;
	private AgentServer tmpAgent = null;
	private static final String[] COLUMNS = { "con_state", "con_ip", "con_port",
		"con_project", "con_interal", "con_times", "con_start","con_end","con_monitor_item"};
	static TableCellRenderer render = new ColorTableCellRenderer();
	private static Color RUN_COLOR=new Color(102,255,102);
	private static Color READY_COLOR=new Color(255,255,153);
	private static Color STOP_COLOR=new Color(204,204,221);
	private static final TableCellRenderer[] RENDERERS = new TableCellRenderer[] {
		render, // state
		null, // ip
		null, // port
		null, // project
		null, // interal
		null, // count
		null, // start time
		null, // end time
		null, // monitor item
	};
//	
//	JProgressBar dpb = new JProgressBar(0, 500);
//	JDialog dlg = null;
//	Thread t = new Thread(new Runnable() {
//		public void run() {
//			dlg.setVisible(true);
//		}
//	});
	
	public String getCurrentServerUrl(){
		return rangeField.getText();
	}
	public void setHistoryButtonEnable(boolean enable){
		this.view.setEnabled(enable);
	}
	/**
	 * Create a new JVMbenchGui.
	 */
	public ServerBenchGui() {
		super();
		init();
	}

	/**
	 * @see org.apache.jmeter.gui.JMeterGUIComponent#getMenuCategories()
	 */
	public Collection<String> getMenuCategories() {
		return null;
	}

	/**
	 * @see org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()
	 */
	public TestElement createTestElement() {
		ServerBench jb = new ServerBench();
		modifyTestElement(jb);
		return jb;
	}

	/**
	 * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
	 */
	public void modifyTestElement(TestElement jb) {
		super.configureTestElement(jb);
	}

	/**
	 * @see org.apache.jmeter.gui.JMeterGUIComponent#createPopupMenu()
	 */
	public JPopupMenu createPopupMenu() {
		JPopupMenu menu = new JPopupMenu();
		return menu;
	}

	public String getLabelResource() {
		return "serverbench_title";
	}

	/**
	 * Initialize the components and layout of this component.
	 */
	private void init() {
		setLayout(new VerticalLayout(5, VerticalLayout.BOTH));
		setBorder(makeBorder());

//		add(makeTitlePanel());	// jex003D 删除了Title面板

		// URL
		VerticalPanel mainPanel = new VerticalPanel();

		mainPanel.setLayout(new VerticalLayout(5, VerticalLayout.LEFT));
		mainPanel.setBorder(BorderFactory.createTitledBorder(JMeterUtils
				.getResString("server_bench_login")));

		// project
		Box urlsPanel = Box.createHorizontalBox();
		JLabel urls = new JLabel(JMeterUtils.getResString("server_bench_url"));
		urls.setPreferredSize(new Dimension(80, 20));
		urlsPanel.add(urls);
		// 用户修改URL后重新取得连接
		rangeField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent e) {
				model.modifiedServerURL();
				model.setServiceUrl(rangeField.getText());
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				model.modifiedServerURL();
				model.setServiceUrl(rangeField.getText());
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				model.modifiedServerURL();
				model.setServiceUrl(rangeField.getText());
			}
		});
		
		// rangeField.setText("10.249.129.159:8080");
//		rangeField.setText("10.249.128.13:8080");
		rangeField.setText("10.20.136.1:8080");
		urlsPanel.add(rangeField);

		mainPanel.add(urlsPanel);

		Box projectPanel = Box.createHorizontalBox();
		JLabel pro = new JLabel(JMeterUtils
				.getResString("server_bench_projects"));
		pro.setPreferredSize(new Dimension(80, 20));
		projectPanel.add(pro);

		String s[] = { JMeterUtils.getResString("server_bench_getprojects") };
		com.setModel(new DefaultComboBoxModel(s));
		com.setPreferredSize(new Dimension(300, 20));
		projectPanel.add(com);
		projectPanel.add(Box.createHorizontalStrut(5));
		projectPanel.add(update);
		update.addActionListener(this);
		mainPanel.add(projectPanel);

		Box buttonPanel = Box.createHorizontalBox();
		buttonPanel.add(Box.createHorizontalStrut(20));
		buttonPanel.add(connect);
		buttonPanel.add(Box.createHorizontalStrut(20));
		buttonPanel.add(disConnect);
		buttonPanel.add(Box.createHorizontalStrut(20));
		buttonPanel.add(view);
		connect.addActionListener(this);
		view.addActionListener(this);
		disConnect.addActionListener(this);
		mainPanel.add(buttonPanel);

		// 配置Dialog
		tableModel = new ObjectTableModel(COLUMNS, AgentServer.class,
				new Functor[] { new Functor("getState"),
						new Functor("getAddress"), new Functor("getPort"),
						new Functor("getProject"), new Functor("getInterval"),
						new Functor("getTimes"), new Functor("getStartTime"),
						new Functor("getEndTime"), new Functor("getItems"), },
				new Functor[] { null, null, null, null, null, null, null, null,
						null }, new Class[] { String.class, String.class,
						String.class, String.class, Integer.class, Long.class,
						String.class, String.class, String.class });
		agentTable = new YccCustomTable(tableModel);
		agentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		agentTable.getTableHeader().setDefaultRenderer(
				new HeaderAsPropertyRenderer());
		agentTable.setPreferredScrollableViewportSize(new Dimension(500, 390));
		RendererUtils.applyRenderers(agentTable, RENDERERS);
		JScrollPane myScrollPane = new JScrollPane(agentTable);
		agentTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					openModifyDialog(false);
				}
			}
		});
		agentTable.addKeyListener(this);
		proDialog.setListener(this);

		add(mainPanel);
		JLabel lb = new JLabel(JMeterUtils.getResString("agent_configuration"));
		Font curFont = lb.getFont();
		lb.setFont(curFont.deriveFont((float) curFont.getSize() + 1));
		add(lb);

		JPanel jp = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel lbchoice = new JLabel(JMeterUtils.getResString("find_info"));
		jp.add(lbchoice);
		jp.add(ipchoice);
		ipchoice.addKeyListener(this);
		choice.addActionListener(this);
		jp.add(choice);
		add(jp);
		add(myScrollPane);

		Box actPanel = Box.createHorizontalBox();
		startBT.setPreferredSize(new Dimension(80, 30));
		startBT.addActionListener(this);
		actPanel.add(configure);
		actPanel.add(Box.createHorizontalStrut(20));
		actPanel.add(show);
		actPanel.add(Box.createHorizontalStrut(20));
		actPanel.add(edit);
		actPanel.add(Box.createHorizontalStrut(20));
		actPanel.add(startBT);
		// 不再使用停止Agent
//		actPanel.add(Box.createHorizontalStrut(20));    // jex003D
//		actPanel.add(stop);                             // jex003D
		actPanel.add(Box.createHorizontalStrut(20));
		actPanel.add(stopProject);
		add(actPanel);

		configure.addActionListener(this);
		show.addActionListener(this);
		edit.addActionListener(this);
		stop.addActionListener(this);
		stopProject.addActionListener(this);
		confDialog.addLiseners(this);
	}

//	private void showProcess(){
//		dlg.setLocationRelativeTo(GuiPackage.getInstance().getMainFrame());
//		t.start();
//	}
//	
//	private void hideProcess(){
//		dlg.setVisible(false);
//	}
	public void actionPerformed(ActionEvent e) {
		
		// 更新
		if (e.getSource() == update) {
			List<String> lst = null;
			try {
				model.setServiceUrl(rangeField.getText());
				lst = this.model.getProjects(rangeField.getText());
				com.setModel(new DefaultComboBoxModel(lst.toArray()));
			} catch (MalformedURLException e1) {
				JOptionPane.showMessageDialog(GuiPackage.getInstance().getMainFrame(),JMeterUtils.getResString("server_bench_failed")
						+ rangeField.getText(), JMeterUtils.getResString("server_bench_error"),
						JOptionPane.ERROR_MESSAGE);
				return;
			} catch (UndeclaredThrowableException e1){
				JOptionPane.showMessageDialog(GuiPackage.getInstance().getMainFrame(),JMeterUtils.getResString("server_bench_failed")
						+ rangeField.getText(), JMeterUtils.getResString("server_bench_error"),
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		// 连接
		} else if (e.getSource() == connect) {
			if (JMeterUtils.getResString("server_bench_getprojects").equals(com.getSelectedItem())) {
				JOptionPane.showMessageDialog(GuiPackage.getInstance().getMainFrame(),JMeterUtils.getResString("server_bench_error_projects")
						, JMeterUtils.getResString("server_bench_error"),
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			model.setServiceUrl(rangeField.getText());
        	try {
        		model.setProject((String)com.getSelectedItem());
        		if (model.connect()) {
        			connect.setEnabled(false);
//        			configure.setEnabled(false);
        			edit.setEnabled(false);
        			stop.setEnabled(false);
        			stopProject.setEnabled(false);
        			startBT.setEnabled(false);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		// 断开连接
		} else if (e.getSource() == disConnect) {
			if (connect.isEnabled()) {
				return;
			}
			if (!(JOptionPane.showConfirmDialog(null, JMeterUtils.getResString("confirm_info_disconnect"),JMeterUtils.getResString("confirm_title_disconnect"),JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)) {
				return;
			}
			model.disConnect();
			connect.setEnabled(true);
//			configure.setEnabled(true);
			edit.setEnabled(true);
			startBT.setEnabled(true);
			stop.setEnabled(true);
			stopProject.setEnabled(true);
		// 获取Agent
		} else if (e.getSource() == configure){
			model.setServiceUrl(rangeField.getText());
			updateAgentList();
		// 获取进程
		} else if (confDialog.getProcessButton().contains(e.getSource())){
			List<String> lst=model.getAllProcess(tmpAgent);
			int ind=lst.get(0).indexOf("CMD");
			ObjectTableModel pidModel=proDialog.getTableModel();
			pidModel.clearData();
			for (int i = 1; i < lst.size(); i++) {
				String line = lst.get(i);
				if (!checkProcess(line)) {
					continue;
				}
				StringTokenizer tokens = new StringTokenizer(line);
				UserProcess up=new UserProcess();
				up.setUid(tokens.nextToken());
				up.setPid(tokens.nextToken());
				up.setPpid(tokens.nextToken());
				up.setRunTime(tokens.nextToken());
				up.setStartTime(tokens.nextToken());
				up.setCmd(line.substring(ind,line.length()));
				pidModel.insertRow(up, pidModel.getRowCount());
			}
			proDialog.setVisible(true);
		// 选中线程
		} else if (e.getSource()==proDialog.getEnter()){
			JTable jt=proDialog.getTable();
			if (jt.getSelectedRow()!=-1) {
				String pid=(String)jt.getValueAt(jt.getSelectedRow(),1);
				Collection<JTextField> lst=confDialog.getProcessTextField();
				for (Iterator<JTextField> iterator = lst.iterator(); iterator.hasNext();) {
					iterator.next().setText(pid);
				}
			}
			proDialog.setVisible(false);
		// 配置Agent
		} else if (e.getSource()==confDialog.getActiveButton()){
			if(!confDialog.checkInput()){
				return;
			}
			String pid=confDialog.getPid();
			String inter=confDialog.getInterval();
			String time=confDialog.getTimes();
			String project=confDialog.getProject();
			String jmeterPath=confDialog.getJmeterPath();
			int interval =JMeterUtils.StringToInt(inter);
			long times=JMeterUtils.StringToInt(time);
			List<String> lst=confDialog.getCheckBoxValue();
			StringBuilder sb = new StringBuilder();
			for (Iterator<String> iterator = lst.iterator(); iterator.hasNext();) {
				sb.append(iterator.next());
				if (iterator.hasNext()) {
					sb.append(",");
				}
			}
			tmpAgent.setItems(sb.toString());
			tmpAgent.setInterval(interval);
			tmpAgent.setTimes(times);
			tmpAgent.setProject(project);
			tmpAgent.setPid(pid);
			tmpAgent.setJmeterPath(jmeterPath);
			tmpAgent.setState(AgentServer.READY);
			agentTable.repaint();
			confDialog.setVisible(false);
			
		// 应用配置后的Agent
		} else if(e.getSource()==startBT){
			boolean res=false;
			for (Iterator<Integer> iterator = agentSeverContainer.keySet().iterator(); iterator.hasNext();) {
				AgentServer as = agentSeverContainer.get(iterator.next());
				if (as.getState().equals(AgentServer.READY)) {
					res=true;
					break;
				}
			}
			if (!res) {
				JOptionPane.showMessageDialog(null,JMeterUtils.getResString("check_agent_error"), JMeterUtils.getResString("server_bench_error"),
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (!(JOptionPane.showConfirmDialog(null, JMeterUtils.getResString("confirm_start_monitor"),JMeterUtils.getResString("confirm_title_start"),JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)) {
				return;
			}
			boolean done = false;
			for (Iterator<Integer> iterator = agentSeverContainer.keySet().iterator(); iterator.hasNext();) {
				AgentServer as = agentSeverContainer.get(iterator.next());
				if (as.getState().equals(AgentServer.READY)) {
					RemoteAgent ra =model.getRemoteAgentMap().get(as);
					ra.setRunProject(as.getProject());
					ra.setCount(as.getTimes());
					ra.setInterval(as.getInterval());
					ra.setRunAgents(as.getItemAsList());
					List<String> params=new ArrayList<String>();
					params.add(as.getPid());
					params.add(as.getJmeterPath());
					done=model.startAgent(ra, as.getItemAsList(),params);
				} else {
					continue;
				}
			}
			if (done) {
				updateAgentList();
				JOptionPane.showMessageDialog(null, JMeterUtils.getResString("start_agent"),JMeterUtils.getResString("info_success"), JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(null, JMeterUtils.getResString("start_agent_warn"),JMeterUtils.getResString("info_success"), JOptionPane.WARNING_MESSAGE);
			}
		// 编辑Agent
		} else if (e.getSource() == edit) {
			int rowI = agentTable.getSelectedRow();
			if (rowI != -1) {
				AgentServer as = agentSeverContainer.get(rowI);
				if (as.getState().equals(AgentServer.RUN)) {
					JOptionPane.showMessageDialog(null, JMeterUtils
							.getResString("error_running_agent"), JMeterUtils
							.getResString("server_bench_error"),
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				openModifyDialog(true);
			}
		} else if (e.getSource() == show){
			openModifyDialog(false);
		} else if (e.getSource() == stop){
			int rowI = agentTable.getSelectedRow();
			if (rowI != -1) {
				AgentServer as = agentSeverContainer.get(rowI);
				if (!as.getState().equals(AgentServer.RUN)) {
					JOptionPane.showMessageDialog(null, JMeterUtils
							.getResString("error_stop_agent"), JMeterUtils
							.getResString("server_bench_error"),
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (!(JOptionPane.showConfirmDialog(null, JMeterUtils.getResString("confirm_stop_monitor"),JMeterUtils.getResString("confirm_title_stop"),JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)) {
					return;
				}
				RemoteAgent ra =model.getRemoteAgentMap().get(as);
				model.stopAgent(ra);
				updateAgentList();
				JOptionPane.showMessageDialog(null, JMeterUtils.getResString("stop_agent"),JMeterUtils.getResString("info_success"), JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (e.getSource() == stopProject){
			int rowI = agentTable.getSelectedRow();
			if (rowI != -1) {
				if (!(JOptionPane.showConfirmDialog(null, JMeterUtils.getResString("confirm_stop_project"),JMeterUtils.getResString("confirm_title_stop"),JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)) {
					return;
				}
				AgentServer as = agentSeverContainer.get(rowI);
				if (!as.getState().equals(AgentServer.RUN)||(as.getProject()==null||as.getProject().equals(""))) {
					JOptionPane.showMessageDialog(null, JMeterUtils
							.getResString("error_no_project"), JMeterUtils
							.getResString("server_bench_error"),
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				RemoteAgent ra =model.getRemoteAgentMap().get(as);
				model.stopProject(ra);
				updateAgentList();
				JOptionPane.showMessageDialog(null, JMeterUtils.getResString("stop_project"),JMeterUtils.getResString("info_success"), JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (e.getSource() == view){
			setHistoryButtonEnable(false);
			// resultFrame的lazy初始化
			if (resultFrame == null) {
				resultFrame = new ResultViewFrame();
				resultFrame.setServerBenchGui(this);
			}
			resultFrame.setMonitorClientModel(model);
			resultFrame.showFrame();
		} else if (e.getSource() == choice){
			findItemFromTable(ipchoice.getText());
		}
	}
	
	private void findItemFromTable(String ip){
		for (Iterator<Integer> iterator = agentSeverContainer.keySet().iterator(); iterator.hasNext();) {
			int index=iterator.next();
			AgentServer as = agentSeverContainer.get(index);
			if (as.getAddress().equals(ip)) {
				agentTable.changeSelection(index, 1, false, false);
				agentTable.requestFocus();
				return;
			}
		}
		// 所查IP不在列表中
	}
	
	private boolean checkProcess(String line){
		if (line.contains("java")) {
			return true;
		}
		return false;
	}
	
	private void openModifyDialog(boolean editable){
    	int rowI  = agentTable.getSelectedRow();
    	if (rowI!=-1) {
    		AgentServer as=agentSeverContainer.get(rowI);
    		tmpAgent=as;
    		confDialog.showModifyValueDialog(as,editable);
		}
	}
	
	private boolean updateAgentList(){
		boolean res=false;
		List<AgentServer> aglst=null;
		agentSeverContainer.clear();
		tableModel.clearData();
		try {
			aglst=model.configure();
		} catch (MalformedURLException e1) {
			return res;
		} catch (UndeclaredThrowableException e1){
			return res;
		}
		if (aglst!=null) {
			Collections.sort(aglst);
			for (Iterator<AgentServer> iterator = aglst.iterator(); iterator.hasNext();) {
				AgentServer as =iterator.next();
				agentSeverContainer.put(tableModel.getRowCount(), as);
				tableModel.insertRow(as, tableModel.getRowCount());
			}
		}
		return true;
	}

	/**
	 * 设置Table的颜色
	 * @author chenchao.yecc
	 *
	 */
	private static class ColorTableCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			Component cell = super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, column);
			if (value.equals(AgentServer.RUN)) {
				cell.setBackground(RUN_COLOR);
			}else if (value.equals(AgentServer.READY)){
				cell.setBackground(READY_COLOR);
			}else if (value.equals(AgentServer.STOP)){
				cell.setBackground(STOP_COLOR);
			}
			return cell;
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}
	
	@Override
	// 键盘事件
	public void keyReleased(KeyEvent e) {
		if (e.getSource()==ipchoice) {
			if (e.getKeyCode()==KeyEvent.VK_ENTER) {
				findItemFromTable(ipchoice.getText());
			}
		}else if(e.getSource()==agentTable){
			if (e.getKeyCode()==KeyEvent.VK_SPACE) {
				openModifyDialog(false);
			}
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
	}
}
