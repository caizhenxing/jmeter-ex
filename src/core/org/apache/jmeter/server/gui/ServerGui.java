package org.apache.jmeter.server.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.util.Collection;

import javax.swing.Box;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.apache.jmeter.gui.AbstractJMeterGuiComponent;
import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.server.Server;
import org.apache.jmeter.testelement.TestElement;

/**
 * Server Gui
 * 
 * @since jex002A
 * @author chenchao.yecc
 */
public class ServerGui  extends AbstractJMeterGuiComponent {
	private static final long serialVersionUID = 1L;

	public static final String STATIC_LABEL = "server";
	// the panel of jvm
	private JDesktopPane jvmPanel;
	private JPanel mainPanel;
	private JPanel controlPanel;
	private JLabel info_lbl = new JLabel();

	/**
	 * construct method
	 * 
	 */
	public ServerGui() {
		super();
		init();
		initGui();
	}

	/**
	 * set jlebel information
	 * 
	 */
	public void setJvmState(String info) {
		info_lbl.setText(info);
	}

	/**
	 * 
	 * @see org.apache.jmeter.gui.JMeterGUIComponent#getMenuCategories()
	 */
	public Collection<String> getMenuCategories() {
		return null;
	}

	/**
	 * 
	 * @see org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()
	 */
	public TestElement createTestElement() {
		Server machine = new Server();
		modifyTestElement(machine);
		return machine;
	}

	/**
	 * Modifies a given TestElement to mirror the data in the gui components.
	 * 
	 * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
	 */
	public void modifyTestElement(TestElement tg) {
		super.configureTestElement(tg);
	}

	/**
	 * Modifies a given TestElement to mirror the data in the gui components.
	 * 
	 * @see org.apache.jmeter.gui.JMeterGUIComponent#configure(TestElement tg)
	 */
	public void configure(TestElement tg) {
		super.configure(tg);
	}

	/**
	 * @see java.awt.event.ItemListener#itemStateChanged(ItemEvent)
	 * 
	 */
	public void itemStateChanged(ItemEvent ie) {

	}

	/**
	 * @see org.apache.jmeter.gui.JMeterGUIComponent#createPopupMenu()
	 * 
	 */
	public JPopupMenu createPopupMenu() {
        return null;
    }

	/**
	 * @see org.apache.jmeter.gui.JMeterGUIComponent#clearGui()
	 * 
	 */
	public void clearGui() {
		super.clearGui();
		initGui();
	}

	/**
	 * rebuild gui
	 * 
	 */
	private void initGui() {
		jvmPanel.removeAll();
	}

	/**
	 * get JDesktopPane panel
	 * 
	 */
	public JDesktopPane getMainPanel() {
		return jvmPanel;
	}

	/**
	 * init gui
	 * 
	 */
	private void init() {
		setLayout(new BorderLayout(0, 5));
		setBorder(makeBorder());

		Box box = Box.createVerticalBox();
		box.add(makeTitlePanel());
		add(box, BorderLayout.NORTH);

		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		controlPanel = new JPanel();
		controlPanel.setLayout(new BorderLayout());
		jvmPanel = new JDesktopPane();
		jvmPanel.setLayout(new BorderLayout());
		jvmPanel.setBackground(new Color(235, 233, 237));
		JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel right = new JPanel();
		info_lbl.setText("Click the right button to start jvm monitor");
//		left.add(info_lbl);
//		right.add(start_btn);
		controlPanel.add(right, BorderLayout.EAST);
		controlPanel.add(left, BorderLayout.CENTER);
		controlPanel.setBackground(new Color(235, 233, 237));
		mainPanel.add(controlPanel, BorderLayout.NORTH);
		mainPanel.add(jvmPanel, BorderLayout.CENTER);
		controlPanel.setVisible(true);
		jvmPanel.setVisible(true);
		mainPanel.setVisible(true);
		this.add(mainPanel, BorderLayout.CENTER);
	}

	/**
	 * @see org.apache.jmeter.gui.JMeterGUIComponent#setNode(javax.swing.tree.TreeNode)
	 * 
	 */
	public void setNode(JMeterTreeNode node) {
		getNamePanel().setNode(node);
	}

	/**
	 * @see javax.swing.JComponent#getPreferredSize()
	 * 
	 */
	@Override
	public Dimension getPreferredSize() {
		return getMinimumSize();
	}

	/**
	 * @see org.apache.jmeter.gui.JMeterGUIComponent#getLabelResource()
	 * 
	 */
	@Override
	public String getLabelResource() {
		return "m_server";
	}
}
