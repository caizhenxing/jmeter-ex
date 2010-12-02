package org.apache.jmeter.monitor.gui;

import java.util.Collection;

import javax.swing.JPopupMenu;

import org.apache.jmeter.gui.AbstractJMeterGuiComponent;
import org.apache.jmeter.monitor.JmeterResult;
import org.apache.jmeter.testelement.TestElement;

public class JmeterResultGui extends AbstractJMeterGuiComponent{

	private static final long serialVersionUID = 1L;

	/**
	 * construct method
	 * 
	 */
	public JmeterResultGui() {
		super();
	}
	
	public JPopupMenu createPopupMenu() {
		// TODO Auto-generated method stub
		return null;
	}

	public TestElement createTestElement() {
		JmeterResult monitor = new JmeterResult();
		modifyTestElement(monitor);
		return monitor;
	}

	public String getLabelResource() {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection getMenuCategories() {
		// TODO Auto-generated method stub
		return null;
	}

	public void modifyTestElement(TestElement element) {
		super.configureTestElement(element);
	}
	
}
