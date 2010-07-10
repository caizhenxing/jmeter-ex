package org.apache.jmeter.control.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.apache.jmeter.control.AgentServer;
import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
import org.apache.jmeter.gui.util.YccCustomTable;
import org.apache.jorphan.gui.ObjectTableModel;
import org.apache.jorphan.gui.RendererUtils;
import org.apache.jorphan.reflect.Functor;

public class ConfigurDialog extends JDialog{
	private static final long serialVersionUID = 1L;
	private JPanel mainPanel =new JPanel(new BorderLayout());
	private transient ObjectTableModel model;
	private JTable myJTable = null;
	private static final String[] COLUMNS = { "ip", "port",
		"project", "interal", "times", "monitor item"};
	private static final TableCellRenderer[] RENDERERS = new TableCellRenderer[] {
		null, // ip
		null, // port
		null, // project
		null, // interal
		null, // times
		null, // monitor item
	};
	
	public ConfigurDialog(){
		model = new ObjectTableModel(COLUMNS, AgentServer.class, new Functor[] {
			new Functor("getAddress"), new Functor("getPort"),
			new Functor("getProject"), new Functor("getInterval"),new Functor("getTimes"),
			new Functor("getItems"), }, new Functor[] { null, null, null,
			null, null, null }, new Class[] { String.class, String.class,
			String.class, Integer.class, Integer.class , String.class});
		myJTable = new YccCustomTable(model);
		myJTable.getTableHeader().setDefaultRenderer(
				new HeaderAsPropertyRenderer());
		myJTable.setPreferredScrollableViewportSize(new Dimension(500, 50));
		RendererUtils.applyRenderers(myJTable, RENDERERS);
		JScrollPane myScrollPane = new JScrollPane(myJTable);
		
		mainPanel.add(myScrollPane,BorderLayout.CENTER);
		this.getContentPane().add(mainPanel);
		this.setModal(true);
		this.setTitle("Title Dialog");
		this.setSize(500, 400);
		this.setLocation(400, 200);
	}
	
	public void addAgentToList(AgentServer server){
		model.insertRow(server, model.getRowCount() - 1);
	}
}
