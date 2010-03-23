package org.apache.jmeter.control.gui;

import java.awt.BorderLayout;
import java.util.Collection;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import org.apache.jmeter.gui.AbstractJMeterGuiComponent;
import org.apache.jmeter.gui.action.ActionNames;
import org.apache.jmeter.gui.util.MenuFactory;
import org.apache.jmeter.machine.gui.MachineGui;
import org.apache.jmeter.testelement.MonitorBench;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;

public class MonitorGui extends AbstractJMeterGuiComponent {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Create a new WorkbenchGui.
     */
    public MonitorGui() {
        super();
        init();
    }

    /**
     * This is the list of menu categories this gui component will be available
     * under. This implementation returns null, since the WorkBench appears at
     * the top level of the tree and cannot be added elsewhere.
     *
     * @return a Collection of Strings, where each element is one of the
     *         constants defined in MenuFactory
     */
    public Collection<String> getMenuCategories() {
        return null;
    }

    /* Implements JMeterGUIComponent.createTestElement() */
    public TestElement createTestElement() {
    	MonitorBench wb = new MonitorBench();
        modifyTestElement(wb);
        return wb;
    }

    /* Implements JMeterGUIComponent.modifyTestElement(TestElement) */
    public void modifyTestElement(TestElement wb) {
        super.configureTestElement(wb);
    }

    /**
     * When a user right-clicks on the component in the test tree, or selects
     * the edit menu when the component is selected, the component will be asked
     * to return a JPopupMenu that provides all the options available to the
     * user from this component.
     * <p>
     * The WorkBench returns a popup menu allowing you to add anything.
     *
     * @return a JPopupMenu appropriate for the component.
     */
    public JPopupMenu createPopupMenu() {
        JPopupMenu menu = new JPopupMenu();
        JMenu addMenu = new JMenu(JMeterUtils.getResString("add"));
        addMenu.add(MenuFactory.makeMenuItem(MachineGui.STATIC_LABEL, MachineGui.class.getName(),
                ActionNames.ADD));
        menu.add(addMenu);
//        MenuFactory.addPasteResetMenu(menu);
//        MenuFactory.addFileMenu(menu);
        return menu;
    }

    public String getLabelResource() {
        return "monitorbench_title";
    }

    /**
     * Initialize the components and layout of this component.
     */
    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());

        add(makeTitlePanel(), BorderLayout.NORTH);
    }
}
