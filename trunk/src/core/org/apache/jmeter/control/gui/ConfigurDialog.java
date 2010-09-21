package org.apache.jmeter.control.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.apache.jmeter.control.AgentServer;
import org.apache.jmeter.gui.util.YccCustomTextField;
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
	private JRadioButton jbcustom = new JRadioButton();
	private JRadioButton jbfiftin = new JRadioButton();
	private JRadioButton jbthirty = new JRadioButton();
	private JRadioButton jbfortyfive = new JRadioButton();
	private JRadioButton jbhour = new JRadioButton();
	private JRadioButton jbday = new JRadioButton();
	private JRadioButton jbthreeday = new JRadioButton();
	private JRadioButton jbfiveday = new JRadioButton();
	private JRadioButton jbsevenday = new JRadioButton();
	private ButtonGroup bg =new ButtonGroup();

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
		ActionListener actionListener＿Esc = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				ConfigurDialog.this.setVisible(false);
			}
		};
		this.getRootPane().registerKeyboardAction(actionListener＿Esc, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),JComponent.WHEN_IN_FOCUSED_WINDOW);
		this.setModal(true);
		this.setResizable(false);
		this.setTitle(JMeterUtils.getResString("agent_configure_dialog"));
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
		interTF = new YccCustomTextField(2, 0, 99);
		interTF.setColumns(10);
		tmpPanel.add(interTF);
		tmpPanel.add(new JLabel(JMeterUtils.getResString("second")));
		tmpPanel.add(Box.createHorizontalStrut(12));
		jbcustom.addItemListener(this);
		bg.add(jbcustom);
		tmpPanel.add(jbcustom);
		tmpPanel.add(new JLabel("自定义"));
		tmpPanel.add(Box.createHorizontalStrut(3));
		jbfiftin.addItemListener(this);
		bg.add(jbfiftin);
		tmpPanel.add(jbfiftin);
		tmpPanel.add(new JLabel("15分钟"));
		tmpPanel.add(Box.createHorizontalStrut(3));
		jbthirty.addItemListener(this);
		tmpPanel.add(jbthirty);
		bg.add(jbthirty);
		tmpPanel.add(new JLabel("30分钟"));
		tmpPanel.add(Box.createHorizontalStrut(3));
		jbfortyfive.addItemListener(this);
		tmpPanel.add(jbfortyfive);
		bg.add(jbfortyfive);
		tmpPanel.add(new JLabel("45分钟"));
		tmpPanel.add(Box.createHorizontalStrut(3));
		cfPanel.add(tmpPanel);
		
		// time
		tmpPanel = Box.createHorizontalBox();
		tmpPanel.add(new JLabel(JMeterUtils.getResString("monitor_count")));
		timeTF = new YccCustomTextField(7, 0, 1296000);
		timeTF.setColumns(10);
		tmpPanel.add(timeTF);
		tmpPanel.add(new JLabel(JMeterUtils.getResString("count")));
		tmpPanel.add(Box.createHorizontalStrut(12));
		jbhour.addItemListener(this);
		tmpPanel.add(jbhour);
		bg.add(jbhour);
		tmpPanel.add(new JLabel("1小时"));
		tmpPanel.add(Box.createHorizontalStrut(3));
		jbday.addItemListener(this);
		tmpPanel.add(jbday);
		bg.add(jbday);
		tmpPanel.add(new JLabel("1天"));
		tmpPanel.add(Box.createHorizontalStrut(3));
		jbthreeday.addItemListener(this);
		tmpPanel.add(jbthreeday);
		bg.add(jbthreeday);
		tmpPanel.add(new JLabel("3天"));
		tmpPanel.add(Box.createHorizontalStrut(3));
		jbfiveday.addItemListener(this);
		tmpPanel.add(jbfiveday);
		bg.add(jbfiveday);
		tmpPanel.add(new JLabel("5天"));
		tmpPanel.add(Box.createHorizontalStrut(3));
		jbsevenday.addItemListener(this);
		tmpPanel.add(jbsevenday);
		bg.add(jbsevenday);
		tmpPanel.add(new JLabel("7天"));
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
				JTextField tmpTx=new YccCustomTextField(10);
				tmpTx.setColumns(10);
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
		portTF.setEditable(false);
		ipTF.setText(as.getAddress());
		ipTF.setEditable(false);
		proTF.setText(as.getProject());
		proTF.setEditable(editable);
		pwdTF.setText(as.getPassword());
		pwdTF.setEditable(false);
		interTF.setText(String.valueOf(as.getInterval()));
		interTF.setEditable(editable);
		timeTF.setText(String.valueOf(as.getTimes()));
		timeTF.setEditable(editable);
		jbcustom.setEnabled(editable);
		jbfiftin.setEnabled(editable);
		jbthirty.setEnabled(editable);
		jbfortyfive.setEnabled(editable);
		jbhour.setEnabled(editable);
		jbday.setEnabled(editable);
		jbthreeday.setEnabled(editable);
		jbfiveday.setEnabled(editable);
		jbsevenday.setEnabled(editable);
		// 配置JRadioButton的选择状态
		int time = (int) (as.getInterval()*(as.getTimes()));
		switch (time) {
		// 15min
		case 60 * 15:
			break;
		// 30min
		case 60 * 30:
			break;
		// 45min
		case 60 * 45:
			break;
		// 60min
		case 60 * 60:
			break;
		// 1day
		case 60 * 60 * 24:
			break;
		// 3day
		case 60 * 60 * 24 * 3:
			break;
		// 5day
		case 60 * 60 * 24 * 5:
			break;
		// 7day
		case 60 * 60 * 24 * 7:
			break;
		default:
			jbcustom.setSelected(true);
		}
		if (as.getRbItem()!=null) {
			bg.setSelected(as.getRbItem().getModel(), true);
		}
		List<String> lst = Arrays.asList(as.getItems().split(","));
		for (int j = 0; j < AGENTS.length; j++) {
			cbMap.get(AGENTS[j]).setEnabled(editable);
			if (lst.contains(AGENTS[j])) {				
				cbMap.get(AGENTS[j]).setSelected(true);
				if (as.getPid()!=null) {
					if (prossTxMap.get(AGENTS[j])!=null) {
						prossTxMap.get(AGENTS[j]).setText(as.getPid());
					}
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
		if (e.getSource() == jbfiftin) {
			setInterAndTime("3","300");
		} else if (e.getSource() == jbthirty) {
			setInterAndTime("3","600");
		} else if (e.getSource() == jbfortyfive) {
			setInterAndTime("3","900");
		} else if (e.getSource() == jbhour) {
			setInterAndTime("3","1200");
		} else if (e.getSource() == jbday) {
			setInterAndTime("3","28800");
		} else if (e.getSource() == jbthreeday) {
			setInterAndTime("3","86400");
		} else if (e.getSource() == jbfiveday) {
			setInterAndTime("3","144000");
		} else if (e.getSource() == jbsevenday) {
			setInterAndTime("3","144000");
		} else if (e.getSource() == jbcustom) {
			timeTF.setText("");
			interTF.setText("");
		} else {
			
		}
	}

	private void setInterAndTime(String ineter,String time){
		timeTF.setText(time);
		interTF.setText(ineter);
	}
	public void actionPerformed(ActionEvent e) {
		
	}
	
	public boolean checkInput(){
		int interval =JMeterUtils.StringToInt(interTF.getText());
		long times=JMeterUtils.StringToInt(timeTF.getText());
		
		// 无项目名
		if (proTF.getText().equals("")) {
			JOptionPane.showMessageDialog(null,JMeterUtils.getResString("check_project_error"), JMeterUtils.getResString("server_bench_error"),
					JOptionPane.ERROR_MESSAGE);
			proTF.requestFocus();
			return false;
		}
		
		// 间隔时间小于0或没有
		if (interval<=0) {
			JOptionPane.showMessageDialog(null,JMeterUtils.getResString("check_interval_error"), JMeterUtils.getResString("server_bench_error"),
					JOptionPane.ERROR_MESSAGE);
			interTF.selectAll();
			interTF.requestFocus();
			return false;
		}
		
		// 取样次数小于30次
		if (times<30L) {
			JOptionPane.showMessageDialog(null,JMeterUtils.getResString("check_times_error"), JMeterUtils.getResString("server_bench_error"),
					JOptionPane.ERROR_MESSAGE);
			timeTF.selectAll();
			timeTF.requestFocus();
			return false;
		}
		
		// 取样时间超过15天
		if (interval*times>15*3600) {
			JOptionPane.showMessageDialog(null,JMeterUtils.getResString("check_times_total_error"), JMeterUtils.getResString("server_bench_error"),
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
		
		// 选择了PID但未输入PID进程号
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
