package org.apache.jmeter.control.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.jmeter.control.AgentServer;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.layout.VerticalLayout;

import com.alibaba.b2b.qa.monitor.AgentCommand;


public class ConfigurDialog extends JDialog implements ItemListener,ActionListener{
	private static final long serialVersionUID = 1L;
	private TextField portTF=null;
	private TextField ipTF=null;
	private TextField proTF=null;
	private TextField pwdTF=null;
	private JPanel mainPanel=null;
	private JPanel controlPanel=null;
	private JButton prossBT = null;
	private Map<String,JCheckBox> cbMap=new HashMap<String,JCheckBox>(); 

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
	
	public ConfigurDialog(){
		init();
		this.setModal(true);
		this.setTitle("Title Dialog");
		System.out.println(SwingUtilities.getRoot(this).getWidth());
		System.out.println(SwingUtilities.getRoot(this).getHeight());
		this.setSize(500, 400);
		this.setLocation(400, 200);
	}
	
	public void init(){

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
		tmpPanel.add(new JLabel("端　口："));
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
		tmpPanel.add(new JLabel("密　码："));
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
				projectPanel.add(Box.createHorizontalStrut(10));
				prossBT=new JButton("获取进程ID");
				prossBT.addActionListener(this);
				projectPanel.add(prossBT);
				cbPanel.add(projectPanel);
			} else {
				cbPanel.add(jb);
			}
		}
		
		cfPanel.add(cbPanel);
		mainPanel.add(controlPanel, BorderLayout.NORTH);
		mainPanel.add(cfPanel, BorderLayout.CENTER);
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
	}
	
	private JCheckBox createChooseCheckBox(String labelResourceName, Color color) {
		JCheckBox checkBox = new JCheckBox(labelResourceName);
		checkBox.setSelected(false);
		checkBox.addItemListener(this);
		checkBox.setForeground(color);
//		checkBox.setToolTipText(JMeterUtils.getResString(PRE_TK+category+"_"+labelResourceName));
		return checkBox;
	}
	
	public void showModifyValueDialog(AgentServer as){
		setAgentValue(as);
		this.setVisible(true);
	}

	private void setAgentValue(AgentServer as){
		portTF.setText(as.getPort());
		ipTF.setText(as.getAddress());
		proTF.setText(as.getProject());
		pwdTF.setText(as.getPassword());
	}
	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
