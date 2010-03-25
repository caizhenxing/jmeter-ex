package org.apache.jmeter.machine.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.apache.jmeter.gui.AbstractJMeterGuiComponent;
import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.machine.Machine;
import org.apache.jmeter.testelement.TestElement;

import sun.tools.jconsole.JConsole;
import sun.tools.jconsole.VMPanel;

/**
 * Machine panel
 * @author chenchao.yecc
 * @version jex001A
 */
public class MachineGui extends AbstractJMeterGuiComponent implements ItemListener,ActionListener {
	private static final long serialVersionUID = 1L;

	// the panel of jvm
    private JDesktopPane jvmPanel;
    //
    private JPanel mainPanel;
    private JPanel controlPanel;
    private JButton start_btn = new JButton("start");
    private JButton close_btn = new JButton("close");
    private JLabel info_lbl = new JLabel();
    public static final String STATIC_LABEL="machine";

    public MachineGui() {
        super();
        init();
        initGui();
    }

    public Collection<String> getMenuCategories() {
        return null;
    }

    public TestElement createTestElement() {
        Machine machine = new Machine();
        modifyTestElement(machine);
        return machine;
    }

    public void setJvmState(String info){
    	info_lbl.setText(info);
    }
    
    public void removeJvmPanel(){
    	setStartButtonEnable(true);
    }
    
    public void setStartButtonEnable(boolean enable){
    	start_btn.setEnabled(enable);
    	close_btn.setEnabled(!enable);
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
        jvmPanel.setVisible(true);
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

    public JDesktopPane getMainPanel(){
    	return jvmPanel;
    }
    
    private void init() {
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());

        Box box = Box.createVerticalBox();
        box.add(makeTitlePanel());
        add(box, BorderLayout.NORTH);

        mainPanel=new JPanel();
        mainPanel.setLayout(new BorderLayout());
        controlPanel=new JPanel();
        controlPanel.setLayout(new BorderLayout());
        jvmPanel = new JDesktopPane();
        jvmPanel.setLayout(new BorderLayout());
        jvmPanel.setBackground(Color.LIGHT_GRAY);
//        mainPanel.setLayout(new BorderLayout());
//        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
//                JMeterUtils.getResString("jvm_monitor"))); // $NON-NLS-1$
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel right = new JPanel();
        info_lbl.setText("Click the right button to start jvm monitor");
        left.add(info_lbl);
        right.add(start_btn);
        right.add(close_btn);
        controlPanel.add(right,BorderLayout.EAST);
        controlPanel.add(left,BorderLayout.CENTER);
        controlPanel.setBackground(Color.RED);
        start_btn.addActionListener(this);
        close_btn.addActionListener(this);
        mainPanel.add(controlPanel,BorderLayout.NORTH);
        mainPanel.add(jvmPanel,BorderLayout.CENTER);
//        controlPanel.setVisible(true);
//        jvmPanel.setVisible(true);
//        mainPanel.setVisible(true);
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

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==start_btn){
			JConsole.getInstance().showConnectDialog("", "", 0, null, null, null);
		} else if (e.getSource()==close_btn){
			JConsole.getInstance().vmPanelClosing((VMPanel)jvmPanel.getComponent(0));
			jvmPanel.removeAll();
			info_lbl.setText("Click the right button to start jvm monitor");
			setStartButtonEnable(true);
		}
	}
}