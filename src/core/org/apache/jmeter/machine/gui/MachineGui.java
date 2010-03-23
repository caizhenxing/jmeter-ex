package org.apache.jmeter.machine.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.apache.jmeter.gui.AbstractJMeterGuiComponent;
import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.util.JMeterUtils;

import sun.tools.jconsole.JConsole;

/**
 * Machine panel
 * @author chenchao.yecc
 * @version jex001A
 */
public class MachineGui extends AbstractJMeterGuiComponent implements ItemListener {
	private static final long serialVersionUID = 1L;

    private JDesktopPane mainPanel;
    public static final String STATIC_LABEL="machine";

    public MachineGui() {
        super();
        init();
        initGui();
        Container cp=new Container();
        mainPanel.add(cp,BorderLayout.CENTER);
        mainPanel.add(new JLabel("Hello World"),BorderLayout.NORTH);
        mainPanel.setBackground(Color.YELLOW);
    }

    public Collection<String> getMenuCategories() {
        return null;
    }

    public TestElement createTestElement() {
        ThreadGroup tg = new ThreadGroup();
        modifyTestElement(tg);
        return tg;
    }

    /**
     * Modifies a given TestElement to mirror the data in the gui components.
     *
     * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
     */
    public void modifyTestElement(TestElement tg) {
        super.configureTestElement(tg);
    }

    public void configure(TestElement tg) {
        super.configure(tg);
        mainPanel.setVisible(true);
    }

    public void itemStateChanged(ItemEvent ie) {
    	
    }

    public JPopupMenu createPopupMenu() {
        JPopupMenu pop = new JPopupMenu();
//        pop.add(MenuFactory.makeMenus(new String[] {
//                MenuFactory.CONTROLLERS,
//                MenuFactory.CONFIG_ELEMENTS,
//                MenuFactory.TIMERS,
//                MenuFactory.PRE_PROCESSORS,
//                MenuFactory.SAMPLERS,
//                MenuFactory.POST_PROCESSORS,
//                MenuFactory.ASSERTIONS,
//                MenuFactory.LISTENERS,
//                },
//                JMeterUtils.getResString("add"), // $NON-NLS-1$
//                ActionNames.ADD));
//        MenuFactory.addEditMenu(pop, true);
//        MenuFactory.addFileMenu(pop);
        return pop;
    }

    public void clearGui(){
        super.clearGui();
        initGui();
    }

    // Initialise the gui field values
    private void initGui(){
    }

    private void init() {
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());

        Box box = Box.createVerticalBox();
        box.add(makeTitlePanel());
        add(box, BorderLayout.NORTH);

        mainPanel = new JDesktopPane();
        mainPanel.setLayout(new BorderLayout());
//        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
//                JMeterUtils.getResString("jvm_monitor"))); // $NON-NLS-1$
        mainPanel.setVisible(true);
        this.add(mainPanel, BorderLayout.CENTER);
        
    }

    public void setNode(JMeterTreeNode node) {
        getNamePanel().setNode(node);
    }

    public Dimension getPreferredSize() {
        return getMinimumSize();
    }

	@Override
	public String getLabelResource() {
		return "machine";
	}
}