package org.apache.jmeter.control.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.apache.jmeter.control.UserProcess;
import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
import org.apache.jmeter.gui.util.YccCustomTable;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.ObjectTableModel;
import org.apache.jorphan.gui.RendererUtils;
import org.apache.jorphan.reflect.Functor;

public class ProcessListDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private transient ObjectTableModel pidmodel;
	private JTable pidTable = null;
	private JButton enter = new JButton(JMeterUtils.getResString("confirm"));
	public ProcessListDialog(){
			init();
			this.setResizable(false);
			this.setModal(true);
			this.setTitle("Title Dialog");
			this.setSize(800,500);
			this.setLocation(400, 200);
	}

	private static final TableCellRenderer[] PID_RENDERERS = new TableCellRenderer[] {
		null, // uid
		null, // pid
		null, // comd
	};
	
	private static final String[] PID_COLUMNS = { "uid", "pid",	"cmd"};
	
	public ObjectTableModel getTableModel(){
		return pidmodel;
	}
	
	public JTable getTable(){
		return pidTable;
	}
	
	public JButton getEnter(){
		return enter;
	}
	
	private void init() {
		// 所有进程Dialog
		JPanel tp=new JPanel(new BorderLayout());
		pidmodel = new ObjectTableModel(PID_COLUMNS, UserProcess.class,
				new Functor[] { new Functor("getUid"), new Functor("getPid"),
			new Functor("getCmd")}, new Functor[] { null, null, null, }, new Class[] { String.class, String.class,
			String.class,});
		pidTable = new YccCustomTable(pidmodel);
		pidTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pidTable.getTableHeader().setDefaultRenderer(
				new HeaderAsPropertyRenderer());
		pidTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
//		pidTable.setPreferredSize(new Dimension(500, 2400));
//		pidTable.setPreferredScrollableViewportSize(new Dimension(900,600));
		RendererUtils.applyRenderers(pidTable, PID_RENDERERS);
		JScrollPane pidScrollPane = new JScrollPane(pidTable);
		DefaultTableCellRenderer render =(DefaultTableCellRenderer)pidTable.getTableHeader().getDefaultRenderer();
		render.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
//		pidScrollPane.setPreferredSize(new Dimension(600,500));
		tp.add(pidScrollPane,BorderLayout.CENTER);
		Box actPanel = Box.createHorizontalBox();
		actPanel.add(Box.createHorizontalStrut(360));
		actPanel.add(enter);
		tp.add(actPanel,BorderLayout.SOUTH);
		
		getContentPane().add(tp);
	}
	
	public void setListener(ActionListener al){
		enter.addActionListener(al);
	}
}
