package org.apache.jmeter.server.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.jmeter.gui.AbstractJMeterGuiComponent;
import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.server.Server;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.layout.VerticalLayout;


/**
 * Server Gui
 * 
 * @since jex002A
 * @author chenchao.yecc
 */
public class ServerGui  extends AbstractJMeterGuiComponent implements ItemListener,ActionListener{
	private static final long serialVersionUID = 1L;

	public static final String STATIC_LABEL = "server";
	public static final String CPU_INFO = "cpu_info";
	public static final String MEMORY_INFO = "mem_info";
	public static final String DISK_INFO = "disk_info";
	public static final String NET_INFO = "net_info";
	public Map<String,JTextArea> tamap=new HashMap<String,JTextArea>();
	public static final int WEITH = 600;
	public static final int HIGHT = 120;
	private Color c=new Color(238, 238, 238);
//	private Server server =null;
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
			Server s = (Server) tg;
			setHardDriveInfo(CPU_INFO, s.getCpuInfo());
			setHardDriveInfo(MEMORY_INFO, s.getMemoryInfo());
			setHardDriveInfo(DISK_INFO, s.getDiskInfo());
			setHardDriveInfo(NET_INFO, s.getOsInfo());
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
	}

	/**
	 * rebuild gui
	 * 
	 */
	private void initGui() {
		setLayout(new VerticalLayout(5, VerticalLayout.BOTH));
		add(getItemPanel(CPU_INFO));
		add(getItemPanel(MEMORY_INFO));
		add(getItemPanel(DISK_INFO));
		add(getItemPanel(NET_INFO));
	}

	private JPanel getItemPanel(String item) {
		JPanel tmpPanel = new JPanel(new BorderLayout());
		tmpPanel.setBorder(BorderFactory.createTitledBorder(JMeterUtils.getResString(item)));
		JTextArea ta = new JTextArea();
		JScrollPane sc = new JScrollPane(ta);
		ta.setEditable(false);
		ta.setBackground(c);
		sc.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sc.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tamap.put(item, ta);
		sc.setPreferredSize(new Dimension(WEITH, HIGHT));
		tmpPanel.add(sc,BorderLayout.CENTER);
		return tmpPanel;
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
	
	public void setHardDriveInfo(String type,String Info){
		JTextArea ta=this.tamap.get(type);
		if (ta!=null) {
			ta.setText(Info);
		}
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