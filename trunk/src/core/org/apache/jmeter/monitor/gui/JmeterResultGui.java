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
	
	@Override
	public JPopupMenu createPopupMenu() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TestElement createTestElement() {
		JmeterResult monitor = new JmeterResult();
		modifyTestElement(monitor);
		return monitor;
	}

	@Override
	public String getLabelResource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection getMenuCategories() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void modifyTestElement(TestElement element) {
		super.configureTestElement(element);
	}
	
}
