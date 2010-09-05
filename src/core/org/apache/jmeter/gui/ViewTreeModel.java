package org.apache.jmeter.gui;

import javax.swing.JPanel;
import javax.swing.tree.DefaultTreeModel;

public class ViewTreeModel extends DefaultTreeModel{

	private static final long serialVersionUID = 1L;
	JPanel mainPanel= null;
	
	public void setMainPanel(JPanel mainPanel){
		this.mainPanel = mainPanel;
	}
	
	public ViewTreeModel(ViewTreeNode node1) {
		super(node1);
//		ViewTreeNode node2  =   new  ViewTreeNode("10.20.36.19");
//		ViewTreeNode node3  =   new  ViewTreeNode("10.20.36.20");
//		ViewTreeNode node4  =   new  ViewTreeNode("10.20.36.21");
//		node1.add(node2);
//		node1.add(node3);
//		node1.add(node4);
		//测试的节点end
	}

	public ViewTreeNode getRootNode(){
		return (ViewTreeNode)super.getRoot();
	}
}
