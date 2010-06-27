package org.apache.jmeter.control.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import org.apache.jmeter.gui.AbstractJMeterGuiComponent;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.server.MonitorClientModel;
import org.apache.jmeter.testelement.ServerBench;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.gui.layout.VerticalLayout;

/**
 * Server Bench
 * 
 * @since jex002A
 * @author chenchao.yecc
 */
public class ServerBenchGui extends AbstractJMeterGuiComponent implements ActionListener{
	private static final long serialVersionUID = 1L;

	private JComboBox com = new JComboBox();
	private JTextField rangeField = new JTextField(58);
	private JButton update = new JButton("update");
	private JButton connect = new JButton("Connect");
	private JButton disConnect = new JButton("disConnect");
	private MonitorClientModel model = new MonitorClientModel();
	
	/**
	 * Create a new JVMbenchGui.
	 */
	public ServerBenchGui() {
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
		ServerBench jb = new ServerBench();
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
//		JMenu addMenu = new JMenu(JMeterUtils.getResString("add"));
//		addMenu.add(MenuFactory.makeMenuItem(JMeterUtils
//				.getResString("monitor_server"), ServerGui.class.getName(),
//				ActionNames.ADD));
//		menu.add(addMenu);
		return menu;
	}

	public String getLabelResource() {
		return "serverbench_title";
	}

	/**
	 * Initialize the components and layout of this component.
	 */
	private void init() {
		setLayout(new VerticalLayout(5, VerticalLayout.BOTH));
		setBorder(makeBorder());

		add(makeTitlePanel());

		// URL
		VerticalPanel mainPanel = new VerticalPanel();

		mainPanel.setLayout(new VerticalLayout(5, VerticalLayout.LEFT));
		mainPanel.setBorder(BorderFactory.createTitledBorder("登陆服务器"));

		// project
		Box urlsPanel = Box.createHorizontalBox();
		JLabel urls=new JLabel("Server URL:");
		urls.setPreferredSize(new Dimension(80,20));
		urlsPanel.add(urls);

//		rangeField.setText("http://10.20.136.18:8080/aliper-server/AliperServlet");
		rangeField.setText("http://10.249.129.159:8080/monitor.server/remote/remoteDataService");
		urlsPanel.add(rangeField);

		mainPanel.add(urlsPanel);

		Box projectPanel = Box.createHorizontalBox();
		JLabel pro=new JLabel("Projects:");
		pro.setPreferredSize(new Dimension(80,20));
		projectPanel.add(pro);

		String s[]={"press the update button to get the info of projects'"};
		com.setModel(new DefaultComboBoxModel(s));
		com.setPreferredSize(new Dimension(300, 20));
		projectPanel.add(com);
		projectPanel.add(Box.createHorizontalStrut(5));
		projectPanel.add(update);
		update.addActionListener(this);
		mainPanel.add(projectPanel);
		
		Box buttonPanel = Box.createHorizontalBox();
		buttonPanel.add(Box.createHorizontalStrut(20));
		buttonPanel.add(connect);
		buttonPanel.add(Box.createHorizontalStrut(20));
		buttonPanel.add(disConnect);
		connect.addActionListener(this);
		disConnect.addActionListener(this);
		mainPanel.add(buttonPanel);
		add(mainPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == update) {
			List<String> lst = null;
			try {
				lst = this.model.getProjects(rangeField.getText());
				com.setModel(new DefaultComboBoxModel(lst.toArray()));
			} catch (MalformedURLException e1) {
				JOptionPane.showMessageDialog(this, "can not connect to "
						+ rangeField.getText());
				return;
			}
		} else if (e.getSource() == connect) {
			this.model.setServiceUrl(rangeField.getText());
        	try {
        		model.setProject((String)com.getSelectedItem());
        		if (model.connect()) {
        			connect.setEnabled(false);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (e.getSource() == disConnect) {
			model.disConnect();
			connect.setEnabled(true);
		}
	}
}
