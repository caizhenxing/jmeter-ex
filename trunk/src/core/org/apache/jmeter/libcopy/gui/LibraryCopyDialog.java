package org.apache.jmeter.libcopy.gui;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.jmeter.libcopy.LibraryCopy;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.ComponentUtil;
import org.apache.jorphan.gui.JLabeledTextField;

/**
 * the dialog of copy jar file
 * 
 * @author chenchao.yecc
 * @since jex001A
 */
public class LibraryCopyDialog extends JDialog  implements ActionListener {
	private static final long serialVersionUID = 1L;

	private JPanel paneReq;
	private CardLayout card= new CardLayout(10, 10);
	private JLabeledTextField openPath=new JLabeledTextField(JMeterUtils.getResString("lib_copy_project_path"));
	private JLabeledTextField savePath=new JLabeledTextField(JMeterUtils.getResString("lib_save_path"));
	private JButton start=new JButton(JMeterUtils.getResString("jtl_parser_start"));

	public LibraryCopyDialog() {
		super((JFrame) null, JMeterUtils.getResString("lib_copy_title"),false);
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
			JOptionPane.showMessageDialog(null, "请填写Eclipse工程路径", "错误", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (savePath.equals("")) {
			JOptionPane.showMessageDialog(null, "请填写依赖保存路径", "错误", JOptionPane.ERROR_MESSAGE);
			return;
		}
		File open=new File(openPath);
		if (!open.exists()) {
			JOptionPane.showMessageDialog(null, "Eclipse工程不存在", "错误", JOptionPane.ERROR_MESSAGE);
			return;
		}
		LibraryCopy copyer =new LibraryCopy();
		copyer.setSrcPath(openPath);
		copyer.setDesPath(savePath);
		try {
			copyer.copyLibrary();
			JOptionPane.showMessageDialog(null, "解析成功", "解析成功", JOptionPane.CLOSED_OPTION);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
