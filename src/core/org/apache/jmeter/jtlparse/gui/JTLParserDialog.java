package org.apache.jmeter.jtlparse.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.jmeter.jtlparse.JTLParser;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.ComponentUtil;
import org.apache.jorphan.gui.JLabeledTextField;

public class JTLParserDialog extends JDialog  implements ActionListener {
	/**
	 * 
	 */
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
	private JLabeledTextField path=new JLabeledTextField("路径：");
	private JButton start=new JButton("开始解析");
	private JButton cancel=new JButton("返回");

	public JTLParserDialog() {
		super((JFrame) null, JMeterUtils.getResString("jtl_parser_title"),false);
		init();
	}

	private void init() {
		this.getContentPane().setLayout(card);
		paneReq=new JPanel(new BorderLayout());
		paneReq.add(path,BorderLayout.CENTER);
		paneReq.add(start,BorderLayout.SOUTH);
		
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
		this.pack();
		ComponentUtil.centerComponentInWindow(this);
	}


	public void actionPerformed(ActionEvent e) {
		card.show(this.getContentPane(), "res");
		parseJTLFile();
	}

	private void parseJTLFile() {
		JTLParser jtlParser =new JTLParser();
		jtlParser.setJmeterLogFile("D:\\Tools\\jakarta-jmeter-2.3.4\\bin\\a.jtl");
		try {
			jtlParser.parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
