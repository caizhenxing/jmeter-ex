package org.apache.jmeter.monitor.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.apache.jmeter.gui.AbstractJMeterGuiComponent;
import org.apache.jmeter.monitor.Monitor;
import org.apache.jmeter.monitor.MonitorCatogory;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;

/**
 * Monitor Gui
 * @author chenchao.yecc
 * @version jex002A
 *
 */
public class MonitorGui extends AbstractJMeterGuiComponent{
	
	private static final long serialVersionUID = 1L;
	
	// 当前的Monitor
	private Monitor monitor = null;
	private JPanel mainPanel =new JPanel();
	private JPanel upPanel =new JPanel();
	private JPanel downPanel =new JPanel();
	private CardLayout chartPanelCard;
	private CardLayout checkboxPanelCard;
	private CardLayout tablePanelCard;

	public static Map<String,MonitorCatogory> MONITOR_CONFIGURE = new HashMap<String,MonitorCatogory>();
	
	static {
		FileInputStream is;
		try {
			is = new FileInputStream("monitorConfigure.xml");
			MONITOR_CONFIGURE = (Map<String,MonitorCatogory>)SaveService.loadMonitorConfigure(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		for (int i = 0; i < CATEGORY.length; i++) {
//			MonitorCatogory m = new MonitorCatogory();
//			m.setCategory(CATEGORY[i]);
//			m.setYAxiscount(COLLECTION_COUNT[i]);
//			for (int j = 0; j < ITEM[i].length; j++) {
//				MonitorLine line = new MonitorLine();
//				line.setName(ITEM[i][j]);
//				line.setShowType(LINE_COLLECTION[i][j]);
//				line.setLineType("");
//				line.setLineColor(COLOR[i][j]);
//				line.setDataType(TYPE[i][j]);
//				m.getLines().put(ITEM[i][j], line);
//			}
//			MONITOR_CONFIGURE.put(CATEGORY[i], m);
//		}
//        OutputStreamWriter outputStreamWriter;
//		try {
//			outputStreamWriter = new OutputStreamWriter(new FileOutputStream(
//					"D:\\a.xml"));
//			// Use deprecated method, to avoid duplicating code
//			SaveService.JMXSAVER.toXML(MONITOR_CONFIGURE, outputStreamWriter);
//			outputStreamWriter.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	/**
	 * get current monitor
	 * 
	 */
	public Monitor getMonitor() {
		return monitor;
	}

	/**
	 * construct method
	 * 
	 */
	public MonitorGui() {
		super();
		init();
		initGui();
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
		this.add(box, BorderLayout.NORTH);
		
		chartPanelCard=new CardLayout(); 
		checkboxPanelCard=new CardLayout(); 
		tablePanelCard=new CardLayout(); 
		mainPanel.setLayout(chartPanelCard);
		upPanel.setLayout(checkboxPanelCard);
		downPanel.setLayout(tablePanelCard);
		this.add(upPanel, BorderLayout.NORTH);
		this.add(mainPanel, BorderLayout.CENTER);
		this.add(downPanel, BorderLayout.SOUTH);
//		this.add(myScrollPane, BorderLayout.SOUTH);
	}

	/**
	 * rebuild gui
	 * 
	 */
	private void initGui() {
	}

	public JPopupMenu createPopupMenu() {
		return null;
	}

	/**
	 * 
	 * @see org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()
	 */
	public TestElement createTestElement() {
		Monitor monitor = new Monitor();
		modifyTestElement(monitor);
		return monitor;
	}

	public JPanel getMainPanel(){
		return mainPanel;
	}
	
	public JPanel getCheckBoxPanel(){
		return upPanel;
	}
	
	public JPanel getTablePanel(){
		return downPanel;
	}
	
	public String getLabelResource() {
		return null;
	}

	/**
	 * 
	 * @see org.apache.jmeter.gui.JMeterGUIComponent#getMenuCategories()
	 */
	public Collection<String> getMenuCategories() {
		return null;
	}

	@Override
	public void configure(TestElement tg) {
		super.configure(tg);
		if (tg instanceof Monitor) {
			monitor = (Monitor) tg;
			chartPanelCard.show(mainPanel, monitor.toString());
			checkboxPanelCard.show(upPanel, monitor.toString());
			tablePanelCard.show(downPanel, monitor.toString());
		}
	}

	public void modifyTestElement(TestElement element) {
		super.configureTestElement(element);
	}
}
