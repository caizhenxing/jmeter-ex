package org.apache.jmeter.control.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
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
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;

import org.apache.jmeter.control.AgentServer;
import org.apache.jmeter.control.MonitorClientModel;
import org.apache.jmeter.control.UserProcess;
import org.apache.jmeter.gui.AbstractJMeterGuiComponent;
import org.apache.jmeter.gui.GuiPackage;
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
public class ServerBenchGui extends AbstractJMeterGuiComponent implements ActionListener{
	private static final long serialVersionUID = 1L;

	private JComboBox com = new JComboBox();
	private JTextField rangeField = new JTextField(58);
	private JButton update = new JButton(JMeterUtils.getResString("server_bench_update"));
	private JButton connect = new JButton(JMeterUtils.getResString("server_bench_connect"));
	private JButton disConnect = new JButton(JMeterUtils.getResString("server_bench_disconnect"));
	private JButton configure = new JButton("configure");
	private JButton startBT = new JButton("启动监控");
	private ConfigurDialog confDialog = new ConfigurDialog();
	private ProcessListDialog proDialog=new ProcessListDialog();
	private MonitorClientModel model = new MonitorClientModel();
	private Map<Integer,AgentServer> agentSeverContainer = new HashMap<Integer,AgentServer>();
	private transient ObjectTableModel omodel;
	private JTable agentTable = null;
	private Map<AgentServer,Integer> editAgent = new HashMap<AgentServer,Integer>();;
	private AgentServer tmpAgent = null;
	private static final String[] COLUMNS = { "con_state", "con_ip", "con_port",
		"con_project", "con_interal", "con_times", "con_monitor_item"};
	private static final TableCellRenderer[] RENDERERS = new TableCellRenderer[] {
		null, // state
		null, // ip
		null, // port
		null, // project
		null, // interal
		null, // times
		null, // monitor item
	};
	
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

		add(makeTitlePanel());

		// URL
		VerticalPanel mainPanel = new VerticalPanel();

		mainPanel.setLayout(new VerticalLayout(5, VerticalLayout.LEFT));
		mainPanel.setBorder(BorderFactory.createTitledBorder(JMeterUtils.getResString("server_bench_login")));

		// project
		Box urlsPanel = Box.createHorizontalBox();
		JLabel urls=new JLabel(JMeterUtils.getResString("server_bench_url"));
		urls.setPreferredSize(new Dimension(80,20));
		urlsPanel.add(urls);

//		rangeField.setText("http://10.20.136.18:8080/aliper-server/AliperServlet");
		rangeField.setText("http://10.249.129.159:8080/monitor/remote/remoteDataService");
		urlsPanel.add(rangeField);

		mainPanel.add(urlsPanel);

		Box projectPanel = Box.createHorizontalBox();
		JLabel pro=new JLabel(JMeterUtils.getResString("server_bench_projects"));
		pro.setPreferredSize(new Dimension(80,20));
		projectPanel.add(pro);

		String s[]={JMeterUtils.getResString("server_bench_getprojects")};
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
		buttonPanel.add(configure);
		connect.addActionListener(this);
		disConnect.addActionListener(this);
		configure.addActionListener(this);
		mainPanel.add(buttonPanel);
		
		// 配置Dialog
		omodel = new ObjectTableModel(COLUMNS, AgentServer.class, new Functor[] {
			new Functor("getState"), new Functor("getAddress"), new Functor("getPort"),
			new Functor("getProject"), new Functor("getInterval"),new Functor("getTimes"),
			new Functor("getItems"), }, new Functor[] { null, null, null, null,
			null, null, null }, new Class[] { String.class, String.class, String.class,
			String.class, Integer.class, Long.class , String.class});
		agentTable = new YccCustomTable(omodel);
		agentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		agentTable.getTableHeader().setDefaultRenderer(
				new HeaderAsPropertyRenderer());
		agentTable.setPreferredScrollableViewportSize(new Dimension(500, 250));
		RendererUtils.applyRenderers(agentTable, RENDERERS);
		JScrollPane myScrollPane = new JScrollPane(agentTable);
		agentTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                    if (evt.getClickCount() == 2) {
                    	int rowI  = agentTable.rowAtPoint(evt.getPoint());
                    	AgentServer as=agentSeverContainer.get(rowI);
                    	tmpAgent=as;
                    	confDialog.showModifyValueDialog(as);
                    }
            }
    });
		proDialog.setListener(this);
		
		add(mainPanel);
		add(new JLabel("Agent configuration:"));
		add(myScrollPane);
		
		Box actPanel = Box.createHorizontalBox();
		startBT.setPreferredSize(new Dimension(80,30));
		startBT.addActionListener(this);
		actPanel.add(startBT);
		add(actPanel);
		confDialog.addLiseners(this);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == update) {
			List<String> lst = null;
			try {
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
		} else if (e.getSource() == connect) {
			if (JMeterUtils.getResString("server_bench_getprojects").equals(com.getSelectedItem())) {
				JOptionPane.showMessageDialog(GuiPackage.getInstance().getMainFrame(),JMeterUtils.getResString("server_bench_error_projects")
						, JMeterUtils.getResString("server_bench_error"),
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			this.model.setServiceUrl(rangeField.getText());
        	try {
        		model.setProject((String)com.getSelectedItem());
        		if (model.connect()) {
        			connect.setEnabled(false);
        			configure.setEnabled(false);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (e.getSource() == disConnect) {
			model.disConnect();
			connect.setEnabled(true);
			configure.setEnabled(true);
		} else if (e.getSource() == configure){
			updateAgentList();
		} else if (confDialog.getProcessButton().contains(e.getSource())){
			List<String> lst=model.getAllProcess(tmpAgent);
			int ind=lst.get(0).indexOf("CMD");
			ObjectTableModel pidModel=proDialog.getTableModel();
			for (int i = 1; i < lst.size(); i++) {
				String line = lst.get(i);
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
		} else if (e.getSource()==proDialog.getEnter()){
			JTable jt=proDialog.getTable();
			if (jt.getSelectedRow()!=-1) {
				String pid=(String)jt.getValueAt(jt.getSelectedRow(),1);
				List<JTextField> lst=confDialog.getProcessTextField();
				for (Iterator<JTextField> iterator = lst.iterator(); iterator.hasNext();) {
					iterator.next().setText(pid);
				}
			}
			proDialog.setVisible(false);
		} else if (e.getSource()==confDialog.getActiveButton()){
			String pid=confDialog.getPid();
			String inter=confDialog.getInterval();
			String time=confDialog.getTimes();
			String project=confDialog.getProject();
			List<String> lst=confDialog.getCheckBoxValue();
			StringBuilder sb = new StringBuilder();
			for (Iterator<String> iterator = lst.iterator(); iterator.hasNext();) {
				sb.append(iterator.next());
				if (iterator.hasNext()) {
					sb.append(",");
				}
			}
			tmpAgent.setItems(sb.toString());
			tmpAgent.setInterval(Integer.parseInt(inter));
			tmpAgent.setTimes(Long.parseLong(time));
			tmpAgent.setProject(project);
			tmpAgent.setPid(pid);
			List editlst = new ArrayList(agentSeverContainer.values());
			int num=editlst.indexOf(tmpAgent);
			omodel.insertRow(tmpAgent, num);
		} else if(e.getSource()==startBT){
//			RemoteAgent ra=model.getRemoteAgentMap().get(tmpAgent);
//			ra.setRunProject(project);
////			ra.setRunAgents(lst);
//			
//			for (Iterator<String> iterator = lst.iterator(); iterator.hasNext();) {
//				model.startAgent(ra, iterator.next(), Long.parseLong(inter), Long.parseLong(time), pid);
//			}
			updateAgentList();
			confDialog.setVisible(false);
			
		}
	}
	
	private void updateAgentList(){
		List<AgentServer> aglst=null;
		agentSeverContainer.clear();
		omodel.clearData();
		try {
			aglst=model.configure();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		if (aglst!=null) {
			for (Iterator<AgentServer> iterator = aglst.iterator(); iterator.hasNext();) {
				AgentServer as =iterator.next();
				agentSeverContainer.put(omodel.getRowCount(), as);
				omodel.insertRow(as, omodel.getRowCount());
			}
		}
	}
}
