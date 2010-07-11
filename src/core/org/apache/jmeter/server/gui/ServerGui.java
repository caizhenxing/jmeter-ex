package org.apache.jmeter.server.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.apache.jmeter.gui.AbstractJMeterGuiComponent;
import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.server.Server;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.layout.VerticalLayout;

import com.alibaba.b2b.qa.monitor.AgentCommand;
import com.alibaba.b2b.qa.monitor.RemoteAgent;

/**
 * Server Gui
 * 
 * @since jex002A
 * @author chenchao.yecc
 */
public class ServerGui  extends AbstractJMeterGuiComponent implements ItemListener,ActionListener{
	private static final long serialVersionUID = 1L;

	public static final String STATIC_LABEL = "server";
	
	public static final String[] AGENTS={
		AgentCommand.AGENT_FILE,
		AgentCommand.AGENT_JSTAT,
		AgentCommand.AGENT_MEMORY,
		AgentCommand.AGENT_LOAD,
		AgentCommand.AGENT_PIDIO,
		AgentCommand.AGENT_CPU,
		AgentCommand.AGENT_IO,
		AgentCommand.AGENT_PIDCPU,
		AgentCommand.AGENT_NET,
	};
	
	private TextField portTF=null;
	private TextField ipTF=null;
	private TextField proTF=null;
	private TextField pwdTF=null;
	private JPanel mainPanel=null;
	private JPanel controlPanel=null;
	private Map<String,JCheckBox> cbMap=new HashMap<String,JCheckBox>(); 

	private Server server =null;
	/**
	 * construct method
	 * 
	 */
	public ServerGui() {
		super();
		init();
		initGui();
	}

	/**
	 * 
	 * @see org.apache.jmeter.gui.JMeterGUIComponent#getMenuCategories()
	 */
	public Collection<String> getMenuCategories() {
		return null;
	}

	/**
	 * 
	 * @see org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()
	 */
	public TestElement createTestElement() {
		Server server = new Server();
		modifyTestElement(server);
		return server;
	}

	/**
	 * Modifies a given TestElement to mirror the data in the gui components.
	 * 
	 * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
	 */
	public void modifyTestElement(TestElement tg) {
		super.configureTestElement(tg);
	}

	/**
	 * Modifies a given TestElement to mirror the data in the gui components.
	 * 
	 * @see org.apache.jmeter.gui.JMeterGUIComponent#configure(TestElement tg)
	 */
	public void configure(TestElement tg) {
		super.configure(tg);
		if (tg instanceof Server) {
			server = (Server) tg;
			RemoteAgent ra=server.getRemoteAgent();
			if (ra != null) {
				proTF.setText(ra.getRunProject());
				portTF.setText(String.valueOf(ra.getPort()));
				ipTF.setText(ra.getAddress());
				pwdTF.setText(ra.getPassword());
				List<String> lst = ra.getRunAgents();
				for (Iterator<String> iterator = lst.iterator(); iterator
						.hasNext();) {
					String name = (String) iterator.next();
					if (cbMap.containsKey(name)) {
						cbMap.get(name).setSelected(true);
					}

				}
			}
		}
	}

	/**
	 * @see java.awt.event.ItemListener#itemStateChanged(ItemEvent)
	 * 
	 */
	public void itemStateChanged(ItemEvent ie) {

	}

	/**
	 * @see org.apache.jmeter.gui.JMeterGUIComponent#createPopupMenu()
	 * 
	 */
	public JPopupMenu createPopupMenu() {
        return null;
    }

	/**
	 * @see org.apache.jmeter.gui.JMeterGUIComponent#clearGui()
	 * 
	 */
	public void clearGui() {
		super.clearGui();
		initGui();
	}

	/**
	 * rebuild gui
	 * 
	 */
	private void initGui() {
	}

	/**
	 * init gui
	 * 
	 */
	private void init() {
		setLayout(new BorderLayout(0, 5));
		setBorder(makeBorder());

		Box box = Box.createVerticalBox();
		box.add(makeTitlePanel());
		add(box, BorderLayout.NORTH);

		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		// 提示信息 生效按钮
		controlPanel = new JPanel();
		controlPanel.setLayout(new BorderLayout());
		JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
		left.add(new JLabel("服务器配置信息："));
		JPanel right = new JPanel();
		JButton active=new JButton("生效 ");
		active.addActionListener(this);
		right.add(active);
		controlPanel.add(right, BorderLayout.EAST);
		controlPanel.add(left, BorderLayout.CENTER);
		
		// 监控选项配置
		JPanel cfPanel = new JPanel();
		cfPanel.setLayout(new VerticalLayout(5, VerticalLayout.LEFT));
		cfPanel.setBorder(BorderFactory.createTitledBorder("服务器配置项目"));
		// port
		Box tmpPanel = Box.createHorizontalBox();
		tmpPanel.add(new JLabel("端口："));
		portTF=new TextField(10);
		portTF.setEditable(false);
		tmpPanel.add(portTF);
		cfPanel.add(tmpPanel);
		
		// IP
		tmpPanel = Box.createHorizontalBox();
		tmpPanel.add(new JLabel("IP地址："));
		ipTF=new TextField(10);
		ipTF.setEditable(false);
		tmpPanel.add(ipTF);
		cfPanel.add(tmpPanel);
		
		// Project
		tmpPanel = Box.createHorizontalBox();
		tmpPanel.add(new JLabel("工程名："));
		proTF=new TextField(10);
		proTF.setEditable(false);
		tmpPanel.add(proTF);
		cfPanel.add(tmpPanel);
		
		// Password
		tmpPanel = Box.createHorizontalBox();
		tmpPanel.add(new JLabel("密码："));
		pwdTF=new TextField(10);
//		pwdTF.setEditable(false);
		tmpPanel.add(pwdTF);
		cfPanel.add(tmpPanel);
		
		
		// 其他选项
		JPanel cbPanel = new JPanel();
		cbPanel.setLayout(new VerticalLayout(5, VerticalLayout.LEFT));
		cfPanel.add(new JLabel("以下选项用来配置服务器监控项目"));
		for (int i = 0; i < AGENTS.length; i++) {
			JCheckBox jb = createChooseCheckBox(JMeterUtils.getResString("server_"+AGENTS[i]), Color.BLACK);
			cbMap.put(AGENTS[i], jb);
			if (AGENTS[i].equals(AgentCommand.AGENT_PIDIO)||(AGENTS[i].equals(AgentCommand.AGENT_PIDCPU)||AGENTS[i].equals(AgentCommand.AGENT_JSTAT))) {
				Box projectPanel = Box.createHorizontalBox();
				projectPanel.add(jb);
				projectPanel.add(Box.createHorizontalStrut(10));
				projectPanel.add(new JLabel("进程ID: "));
				projectPanel.add(new TextField(10));
				cbPanel.add(projectPanel);
			} else {
				cbPanel.add(jb);
			}
		}
		
		cfPanel.add(cbPanel);
		mainPanel.add(controlPanel, BorderLayout.NORTH);
//		mainPanel.add(cfPanel, BorderLayout.SOUTH);
		mainPanel.add(cfPanel, BorderLayout.CENTER);
		this.add(mainPanel, BorderLayout.CENTER);
	}
	
private JCheckBox createChooseCheckBox(String labelResourceName, Color color) {
	JCheckBox checkBox = new JCheckBox(labelResourceName);
	checkBox.setSelected(false);
	checkBox.addItemListener(this);
	checkBox.setForeground(color);
//	checkBox.setToolTipText(JMeterUtils.getResString(PRE_TK+category+"_"+labelResourceName));
	return checkBox;
}
	/**
	 * @see org.apache.jmeter.gui.JMeterGUIComponent#setNode(javax.swing.tree.TreeNode)
	 * 
	 */
	public void setNode(JMeterTreeNode node) {
		getNamePanel().setNode(node);
	}

	/**
	 * @see javax.swing.JComponent#getPreferredSize()
	 * 
	 */
	@Override
	public Dimension getPreferredSize() {
		return getMinimumSize();
	}

	/**
	 * @see org.apache.jmeter.gui.JMeterGUIComponent#getLabelResource()
	 * 
	 */
	public String getLabelResource() {
		return "m_server";
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
