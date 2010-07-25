package org.apache.jmeter.jtlparse.gui;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
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
	private CardLayout card= new CardLayout(10, 10);
	private JLabeledTextField openPath=new JLabeledTextField(JMeterUtils.getResString("jtl_parser_file_path"));
	private JLabeledTextField savePath=new JLabeledTextField(JMeterUtils.getResString("jtl_parser_save_path"));
	private JButton start=new JButton(JMeterUtils.getResString("jtl_parser_start"));

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
		
		start.addActionListener(this);
		this.getContentPane().add(paneReq, "req");
		this.setResizable(false);
		ComponentUtil.centerComponentInWindow(this);
	}


	public void actionPerformed(ActionEvent e) {
		parseJTLFile(openPath.getText(),savePath.getText());
	}

	private void parseJTLFile(String openPath,String savePath) {
		if (openPath.equals("")) {
			JOptionPane.showMessageDialog(null, JMeterUtils.getResString("jtlparser_path"),JMeterUtils.getResString("Error"), JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (savePath.equals("")) {
			JOptionPane.showMessageDialog(null, JMeterUtils.getResString("jtlparser_res_path"), JMeterUtils.getResString("Error"), JOptionPane.ERROR_MESSAGE);
			return;
		}
		File open=new File(openPath);
		if (!open.exists()) {
			JOptionPane.showMessageDialog(null, JMeterUtils.getResString("jtlparser_no_jtl"), JMeterUtils.getResString("Error"), JOptionPane.ERROR_MESSAGE);
			return;
		}
		JTLParser jtlParser =new JTLParser();
		jtlParser.setJmeterLogFile(openPath);
		jtlParser.setSaveFile(savePath);
		try {
			jtlParser.parse();
			JOptionPane.showMessageDialog(null, JMeterUtils.getResString("jtlparser_succed"), JMeterUtils.getResString("table_visualizer_success"), JOptionPane.CLOSED_OPTION);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
