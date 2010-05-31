package org.apache.jmeter.control.gui;

import java.awt.BorderLayout;
import java.util.Collection;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import org.apache.jmeter.gui.AbstractJMeterGuiComponent;
import org.apache.jmeter.gui.action.ActionNames;
import org.apache.jmeter.gui.util.MenuFactory;
import org.apache.jmeter.machine.gui.MachineGui;
import org.apache.jmeter.testelement.JVMBench;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;

/**
 * JVMBench
 * 
 * @since jex001A
 * @author chenchao.yecc
 */
public class JVMBenchGui extends AbstractJMeterGuiComponent {
	private static final long serialVersionUID = 1L;

	/**
     * Create a new JVMbenchGui.
     */
    public JVMBenchGui() {
        super();
        init();
    }

    /**
     * @see org.apache.jmeter.gui.JMeterGUIComponent#getMenuCategories()
     */
    public Collection<String> getMenuCategories() {
        return null;
    }

    /**
     * @see org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()
     */
    public TestElement createTestElement() {
    	JVMBench jb = new JVMBench();
        modifyTestElement(jb);
        return jb;
    }

    /**
     * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
     */
    public void modifyTestElement(TestElement jb) {
        super.configureTestElement(jb);
    }

    /**
     * @see org.apache.jmeter.gui.JMeterGUIComponent#createPopupMenu()
     */
    public JPopupMenu createPopupMenu() {
        JPopupMenu menu = new JPopupMenu();
        JMenu addMenu = new JMenu(JMeterUtils.getResString("add"));
        addMenu.add(MenuFactory.makeMenuItem(JMeterUtils.getResString("machine"), MachineGui.class.getName(),
                ActionNames.ADD));
        menu.add(addMenu);
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
