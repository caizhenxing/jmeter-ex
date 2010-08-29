package org.apache.jmeter.gui;

import javax.swing.tree.DefaultMutableTreeNode;

public class ViewTreeNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 1767003599403346967L;
	private ViewTabbedPane tabPanel = null;
	
	public void setTabbedPanel(ViewTabbedPane tabPanel){
		this.tabPanel = tabPanel;
	}
	
	public ViewTabbedPane getTabbedPanel(){
		return tabPanel;
	}
	
	public ViewTreeNode(Object userObject){
		super(userObject);
	}
}
