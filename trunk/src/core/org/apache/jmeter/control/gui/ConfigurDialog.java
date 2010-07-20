package org.apache.jmeter.control.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.jmeter.control.AgentServer;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.layout.VerticalLayout;

import com.alibaba.b2b.qa.monitor.AgentCommand;


public class ConfigurDialog extends JDialog implements ItemListener,ActionListener{
	private static final long serialVersionUID = 1L;
	private JTextField portTF=null;
	private JTextField ipTF=null;
	private JTextField proTF=null;
	private JTextField pwdTF=null;
	private JTextField interTF=null;
	private JTextField timeTF=null;
	private JPanel mainPanel=null;
	private JPanel controlPanel=null;
	private List<JButton> prossBT = new ArrayList<JButton>();
//	private List<JTextField> prossTx = new ArrayList<JTextField>();
	private Map<String,JTextField> prossTxMap = new HashMap<String,JTextField>();
	private JButton active= null;
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
		this.setResizable(false);
		this.setTitle("Title Dialog");
		this.setSize(500, 550);
		this.setLocation(400, 200);
	}
	
	public void init(){

		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		// 提示信息 生效按钮
		controlPanel = new JPanel();
		controlPanel.setLayout(new BorderLayout());
		JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel jl=new JLabel(JMeterUtils.getResString("agent_info"));
	    Font curFont = jl.getFont();
	    jl.setFont(curFont.deriveFont((float) curFont.getSize() + 1));
		left.add(jl);
		
		JPanel right = new JPanel();
		active=new JButton(JMeterUtils.getResString("confirm"));
		right.add(active);
		controlPanel.add(right, BorderLayout.EAST);
		controlPanel.add(left, BorderLayout.CENTER);
		
		// 监控选项配置
		JPanel cfPanel = new JPanel();
		cfPanel.setLayout(new VerticalLayout(5, VerticalLayout.LEFT));
		cfPanel.setBorder(BorderFactory.createTitledBorder(JMeterUtils.getResString("configure_item")));
		// port
		Box tmpPanel = Box.createHorizontalBox();
		tmpPanel.add(new JLabel(JMeterUtils.getResString("item_port")));
		portTF=new JTextField(10);
		portTF.setEditable(false);
		tmpPanel.add(portTF);
		cfPanel.add(tmpPanel);
		
		// IP
		tmpPanel = Box.createHorizontalBox();
		tmpPanel.add(new JLabel(JMeterUtils.getResString("item_ip")));
		ipTF=new JTextField(10);
		ipTF.setEditable(false);
		tmpPanel.add(ipTF);
		cfPanel.add(tmpPanel);
		
		// Project
		tmpPanel = Box.createHorizontalBox();
		tmpPanel.add(new JLabel(JMeterUtils.getResString("project_name")));
		proTF=new JTextField(10);
		tmpPanel.add(proTF);
		cfPanel.add(tmpPanel);
		
		// Password
		tmpPanel = Box.createHorizontalBox();
		tmpPanel.add(new JLabel(JMeterUtils.getResString("password_item")));
		pwdTF=new JTextField(10);
		tmpPanel.add(pwdTF);
		cfPanel.add(tmpPanel);
		
		// interval
		tmpPanel = Box.createHorizontalBox();
		tmpPanel.add(new JLabel(JMeterUtils.getResString("interval")));
		interTF=new JTextField(10);
		tmpPanel.add(interTF);
		tmpPanel.add(new JLabel(JMeterUtils.getResString("second")));
		cfPanel.add(tmpPanel);
		
		// time
		tmpPanel = Box.createHorizontalBox();
		tmpPanel.add(new JLabel(JMeterUtils.getResString("monitor_count")));
		timeTF=new JTextField(10);
		tmpPanel.add(timeTF);
		tmpPanel.add(new JLabel(JMeterUtils.getResString("count")));
		cfPanel.add(tmpPanel);
		
		
		// 其他选项
		JPanel cbPanel = new JPanel();
		cbPanel.setLayout(new VerticalLayout(5, VerticalLayout.LEFT));
		cfPanel.add(new JLabel(JMeterUtils.getResString("configure_monitor_item")));
		for (int i = 0; i < AGENTS.length; i++) {
			JCheckBox jb = createChooseCheckBox(JMeterUtils.getResString("server_"+AGENTS[i]), Color.BLACK);
			cbMap.put(AGENTS[i], jb);
			if (AGENTS[i].equals(AgentCommand.AGENT_PIDIO)||(AGENTS[i].equals(AgentCommand.AGENT_PIDCPU)||AGENTS[i].equals(AgentCommand.AGENT_JSTAT))) {
				Box projectPanel = Box.createHorizontalBox();
				projectPanel.add(jb);
				projectPanel.add(Box.createHorizontalStrut(10));
				projectPanel.add(new JLabel(JMeterUtils.getResString("process_id")));
				JTextField tmpTx=new JTextField(10);
				projectPanel.add(tmpTx);
				prossTxMap.put(AGENTS[i], tmpTx);
				projectPanel.add(Box.createHorizontalStrut(10));
				JButton tmpBT=new JButton(JMeterUtils.getResString("get_process_id"));
				prossBT.add(tmpBT);
				projectPanel.add(tmpBT);
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
	
	public void addLiseners(ActionListener al){
		for (Iterator<JButton> iterator = prossBT.iterator(); iterator.hasNext();) {
			iterator.next().addActionListener(al);
			
		}
		active.addActionListener(al);
	}
	
	public JButton getActiveButton(){
		return active;
	}
	
	public List<JButton> getProcessButton(){
		return prossBT;
	}
	
	public Collection<JTextField> getProcessTextField(){
		return prossTxMap.values();
	}
	
	public String getProject(){
		return proTF.getText();
	}
	
	public String getPid(){
		return new ArrayList<JTextField>(prossTxMap.values()).get(0).getText();
	}
	
	public String getInterval(){
		return interTF.getText();
	}
	
	public String getTimes(){
		return timeTF.getText();
	}
	
	public List<String> getCheckBoxValue(){
		List<String> lst =new ArrayList<String>();
		for (int j = 0; j < AGENTS.length; j++) {
			if (cbMap.get(AGENTS[j]).isSelected()) {
				lst.add(AGENTS[j]);
			}
		}
		return lst;
	}
	
	private JCheckBox createChooseCheckBox(String labelResourceName, Color color) {
		JCheckBox checkBox = new JCheckBox(labelResourceName);
		checkBox.setSelected(false);
		checkBox.addItemListener(this);
		checkBox.setForeground(color);
//		checkBox.setToolTipText(JMeterUtils.getResString(PRE_TK+category+"_"+labelResourceName));
		return checkBox;
	}
	
	public void showModifyValueDialog(AgentServer as,boolean editable){
		clearAgentValue();
		setAgentValue(as,editable);
		this.setVisible(true);
	}

	private void clearAgentValue(){
		portTF.setText("");
		ipTF.setText("");
		proTF.setText("");
		pwdTF.setText("");
		interTF.setText("");
		timeTF.setText("");
		for (int j = 0; j < AGENTS.length; j++) {
			cbMap.get(AGENTS[j]).setSelected(false);
		}
		for (Iterator<JTextField> iterator = prossTxMap.values().iterator(); iterator.hasNext();) {
			iterator.next().setText("");
		}
	}
	
	private void setAgentValue(AgentServer as,boolean editable){
		portTF.setText(as.getPort());
		portTF.setEditable(editable);
		ipTF.setText(as.getAddress());
		ipTF.setEditable(editable);
		proTF.setText(as.getProject());
		proTF.setEditable(editable);
		pwdTF.setText(as.getPassword());
		pwdTF.setEditable(editable);
		interTF.setText(String.valueOf(as.getInterval()));
		interTF.setEditable(editable);
		timeTF.setText(String.valueOf(as.getTimes()));
		timeTF.setEditable(editable);
		List<String> lst = Arrays.asList(as.getItems().split(","));
		for (int j = 0; j < AGENTS.length; j++) {
			cbMap.get(AGENTS[j]).setEnabled(editable);
			if (lst.contains(AGENTS[j])) {				
				cbMap.get(AGENTS[j]).setSelected(true);
				if (as.getPid()!=null) {
					prossTxMap.get(AGENTS[j]).setText(as.getPid());
				}
			}
		}
		for (Iterator<JButton> iterator = prossBT.iterator(); iterator.hasNext();) {
			iterator.next().setEnabled(editable);
		}
		for (Iterator<JTextField> iterator = prossTxMap.values().iterator(); iterator.hasNext();) {
			iterator.next().setEnabled(editable);
		}
		active.setVisible(editable);
	}
	
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void actionPerformed(ActionEvent e) {
		
	}
	
	public boolean checkInput(){
		int interval =JMeterUtils.StringToInt(interTF.getText());
		long times=JMeterUtils.StringToInt(timeTF.getText());
		if (proTF.getText().equals("")) {
			JOptionPane.showMessageDialog(null,JMeterUtils.getResString("check_project_error"), JMeterUtils.getResString("server_bench_error"),
					JOptionPane.ERROR_MESSAGE);
			proTF.requestFocus();
			return false;
		}
		if (interval==0) {
			JOptionPane.showMessageDialog(null,JMeterUtils.getResString("check_interval_error"), JMeterUtils.getResString("server_bench_error"),
					JOptionPane.ERROR_MESSAGE);
			interTF.selectAll();
			interTF.requestFocus();
			return false;
		}
		if (times<30L) {
			JOptionPane.showMessageDialog(null,JMeterUtils.getResString("check_times_error"), JMeterUtils.getResString("server_bench_error"),
					JOptionPane.ERROR_MESSAGE);
			timeTF.selectAll();
			timeTF.requestFocus();
			return false;
		}
		// 没有选择任何Agent
		boolean select = false;
		for (int j = 0; j < AGENTS.length; j++) {
			if (cbMap.get(AGENTS[j]).isSelected()) {
				select=true;
				break;
			}
		}
		if (!select) {
			JOptionPane.showMessageDialog(null,JMeterUtils.getResString("check_noitem_error"), JMeterUtils.getResString("server_bench_error"),
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		// 选择了PID但为输入PID进程号
		if (cbMap.get(AgentCommand.AGENT_PIDIO).isSelected()){
			if (JMeterUtils.StringToLong(prossTxMap.get(AgentCommand.AGENT_PIDIO).getText())==0) {
				JOptionPane.showMessageDialog(null,JMeterUtils.getResString("check_pid_error"), JMeterUtils.getResString("server_bench_error"),
						JOptionPane.ERROR_MESSAGE);
				prossTxMap.get(AgentCommand.AGENT_PIDIO).selectAll();
				prossTxMap.get(AgentCommand.AGENT_PIDIO).requestFocus();
				return false;
			}
		}
		
		if (cbMap.get(AgentCommand.AGENT_PIDCPU).isSelected()){
			if (JMeterUtils.StringToLong(prossTxMap.get(AgentCommand.AGENT_PIDCPU).getText())==0) {
				JOptionPane.showMessageDialog(null,JMeterUtils.getResString("check_pid_error"), JMeterUtils.getResString("server_bench_error"),
						JOptionPane.ERROR_MESSAGE);
				prossTxMap.get(AgentCommand.AGENT_PIDCPU).selectAll();
				prossTxMap.get(AgentCommand.AGENT_PIDCPU).requestFocus();
				return false;
			}
		}
		if (cbMap.get(AgentCommand.AGENT_JSTAT).isSelected()){
			if (JMeterUtils.StringToLong(prossTxMap.get(AgentCommand.AGENT_JSTAT).getText())==0) {
				JOptionPane.showMessageDialog(null,JMeterUtils.getResString("check_pid_error"), JMeterUtils.getResString("server_bench_error"),
						JOptionPane.ERROR_MESSAGE);
				prossTxMap.get(AgentCommand.AGENT_JSTAT).selectAll();
				prossTxMap.get(AgentCommand.AGENT_JSTAT).requestFocus();
				return false;
			}
		}
		return true;
	}
	
	public static void main(String[] args) {
		JFrame f=new JFrame();
		JPanel p=new JPanel();
		JPanel b=new JPanel();
		JButton bu=new JButton("Button");
		JPanel m=new JPanel(new BorderLayout());
		m.add(p,BorderLayout.NORTH);
		m.add(b,BorderLayout.CENTER);
		b.add(new JLabel("aeryeydf"));
		b.add(bu);
		p.add(new JLabel("adf"));
		p.add(bu);
		f.getContentPane().add(m);
		f.pack();
		f.setVisible(true);
	}
}
