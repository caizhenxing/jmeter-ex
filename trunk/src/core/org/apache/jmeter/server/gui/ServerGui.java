package org.apache.jmeter.server.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;

import javax.swing.Box;
import javax.swing.JPopupMenu;

import org.apache.jmeter.gui.AbstractJMeterGuiComponent;
import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.server.Server;
import org.apache.jmeter.testelement.TestElement;
import com.alibaba.b2b.qa.monitor.RemoteAgent;

/**
 * Server Gui
 * 
 * @since jex002A
 * @author chenchao.yecc
 */
public class ServerGui  extends AbstractJMeterGuiComponent implements ItemListener,ActionListener{
	private static final long serialVersionUID = 1L;

	public static final String STATIC_LABEL = "server";
	
	private Server server =null;
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
		Server server = new Server();
		modifyTestElement(server);
		return server;
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
		if (tg instanceof Server) {
			server = (Server) tg;
			RemoteAgent ra=server.getRemoteAgent();
			if (ra != null) {
			}
		}
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
	public String getLabelResource() {
		return "m_server";
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
