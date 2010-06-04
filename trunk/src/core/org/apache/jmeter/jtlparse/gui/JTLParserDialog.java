package org.apache.jmeter.jtlparse.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.functions.Function;
import org.apache.jmeter.gui.action.ActionRouter;
import org.apache.jmeter.gui.action.Help;
import org.apache.jmeter.jtlparse.JTLParser;
import org.apache.jmeter.testelement.property.PropertyIterator;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.ComponentUtil;
import org.apache.jorphan.gui.JLabeledChoice;
import org.apache.jorphan.gui.JLabeledTextField;
import org.apache.jorphan.reflect.ClassFinder;

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
	private JList timeLst;
	private JList tpsLst;
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
