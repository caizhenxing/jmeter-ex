package org.apache.jmeter.jtlparse.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.jmeter.jtlparse.JTLParser;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.ComponentUtil;
import org.apache.jorphan.gui.JLabeledTextField;

/**
 * the dialog of parsing jtl file
 * 
 * @author chenchao.yecc
 * @since jex001A
 */
public class JTLParserDialog extends JDialog  implements ActionListener {
	private static final long serialVersionUID = 1L;

	private JPanel paneReq;
	private JPanel paneRes;
	private JPanel panelUp;
	private JPanel panelDown;
	private CardLayout card= new CardLayout(10, 10);
	private JLabel time=new JLabel(JMeterUtils.getResString("jtl_parser_time"));
	private JLabel tps=new JLabel(JMeterUtils.getResString("jtl_parser_tps"));
//	private JList timeLst;
//	private JList tpsLst;
//	private JLabeledTextField host;
	private JLabeledTextField openPath=new JLabeledTextField("JTL文件路径：");
	private JLabeledTextField savePath=new JLabeledTextField("保存路径：");
	private JButton start=new JButton("开始");
	private JButton cancel=new JButton("返回");

	public JTLParserDialog() {
		super((JFrame) null, JMeterUtils.getResString("jtl_parser_title"),false);
		init();
	}

	private void init() {
		this.getContentPane().setLayout(card);
		this.setSize(650,150);
		paneReq=new JPanel();
		paneReq.setLayout(null);
		paneReq.add(openPath);
		openPath.setBounds(10, 5, 600, 20);
		paneReq.add(savePath);
		savePath.setBounds(10, 35, 600, 20);
		paneReq.add(start);
		start.setBounds(this.getWidth()/2-40, 70, 80, 25);
		
		paneRes=new JPanel(new BorderLayout());
		panelUp=new JPanel(new BorderLayout());
		panelDown=new JPanel(new BorderLayout());
		panelUp.add(time,BorderLayout.NORTH);
		panelDown.add(tps,BorderLayout.NORTH);
		paneRes.add(panelUp,BorderLayout.NORTH);
		paneRes.add(panelDown,BorderLayout.CENTER);
		paneRes.add(cancel,BorderLayout.SOUTH);
		start.addActionListener(this);
		this.getContentPane().add(paneReq, "req");
		this.getContentPane().add(paneRes, "res");
		this.setResizable(false);
//		this.pack();
		ComponentUtil.centerComponentInWindow(this);
	}


	public void actionPerformed(ActionEvent e) {
//		card.show(this.getContentPane(), "res");
		parseJTLFile(openPath.getText(),savePath.getText());
	}

	private void parseJTLFile(String openPath,String savePath) {
		if (openPath.equals("")) {
			JOptionPane.showMessageDialog(null, "请填写JTL文件路径", "错误", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (savePath.equals("")) {
			JOptionPane.showMessageDialog(null, "请填写结果文件路径", "错误", JOptionPane.ERROR_MESSAGE);
			return;
		}
		File open=new File(openPath);
		if (!open.exists()) {
			JOptionPane.showMessageDialog(null, "JTL文件不存在", "错误", JOptionPane.ERROR_MESSAGE);
			return;
		}
		JTLParser jtlParser =new JTLParser();
		jtlParser.setJmeterLogFile(openPath);
		jtlParser.setSaveFile(savePath);
		try {
			jtlParser.parse();
			JOptionPane.showMessageDialog(null, "解析成功", "解析成功", JOptionPane.CLOSED_OPTION);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
