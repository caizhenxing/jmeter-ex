package org.apache.jmeter.monitor.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.apache.jmeter.gui.AbstractJMeterGuiComponent;
import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
import org.apache.jmeter.gui.util.YccCustomTable;
import org.apache.jmeter.monitor.Monitor;
import org.apache.jmeter.monitor.MonitorDataStat;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.Calculator;
import org.apache.jorphan.gui.ObjectTableModel;
import org.apache.jorphan.gui.RendererUtils;
import org.apache.jorphan.reflect.Functor;

/**
 * Monitor Gui
 * @author chenchao.yecc
 * @version jex002A
 *
 */
public class MonitorGui extends AbstractJMeterGuiComponent{

	// 监控分类
	public static final String[] CATEGORY={
		"file",
		"jsat_heap",
		"jsat_gc",
		"memory",
		"loadavg",
		"pid_io",
		"cpu",
		"io",
		"pid_cpu",
		"net",
	};

	// 监控类对应的Y轴个数
	public static Integer[] COLLECTION_COUNT={
		1,
		1,
		2,
		1,
		1,
		1,
		1,
		2,
		1,
		1,
	};
	
	// 线的显示设置、线和坐标轴的映射关系
	public static final String[][] LINE_COLLECTION = {
		{ "time", "1", "1", "1", },
		{ "time", "1", "1", "1", "1", "1", },
		{ "time", "1", "2", "1", "2","2", },
		{ "time", "1", "1", "1", "1", "1", "1","1",},
		{ "time", "1", "1", "1", },
		{ "time", "-", "1", "1", "1",},
		{ "time", "1", "1", "1", "1","1","1",},
		{ "time", "2", "2", "2", "1","1",},
		{ "time", "-", "1", "1", "1",},
		{ "time", "-", "-", "-", "1","1","-","-","-",},
	};
	
	// 线的种类
	public static final String[][] ITEM = {
			{ "time", "total", "used", "allocated", },
			{ "time", "S0", "S1", "E", "O", "P", },
			{ "time", "YGC", "YGCT", "FGC", "FGCT","GCT", },
			{ "time", "total", "used", "free", "buffer", "cached", "swapTotal","swapFree",},
			{ "time", "loadAvg1", "loadAvg5", "loadAvg10", },
			{ "time", "pid", "KB_rd/s", "KB_Wr/s", "kB_ccwr/s",},
			{ "time", "user%", "nice%", "system%", "iowait%","steal%","idle%",},
			{ "time", "tps", "rtps", "wtps","bread/s","bwrtn/s",},
			{ "time", "pid", "%user", "%system", "%cpu",},
			{ "time", "iface", "rxpck/s", "txpck/s", "rxkB/s","txkB/s","rxcmp/s","txcmp/s","rxmcst/s",},
	};
	
	// 线的颜色
	public static final String[][] COLOR = {
		{ "", "RED", "GREEN", "BLUE", },
		{ "", "RED", "GREEN", "ORANGE", "BLUE", "MAGENTA", },
		{ "", "RED", "GREEN", "ORANGE", "MAGENTA", "BLUE", },
		{ "", "RED", "GREEN", "ORANGE", "BLUE", "MAGENTA", "YELLOW", "PINK",},
		{ "", "RED", "GREEN", "BLUE", },
		{ "", "RED", "GREEN", "ORANGE", "BLUE",},
		{ "", "RED", "GREEN", "ORANGE", "BLUE", "MAGENTA", "YELLOW",},
		{ "", "RED", "GREEN", "ORANGE", "BLUE", "MAGENTA", },
		{ "", "RED", "GREEN", "ORANGE", "BLUE",},
		{ "", "RED", "GREEN", "ORANGE", "BLUE", "MAGENTA", "YELLOW", "PINK", "CYAN",},
	};
	
	// 线对应的数据
	public static final String[][] TYPE = {
		{ "Long", "Long", "Long", "Long", },
		{ "Long", "Double", "Double", "Double", "Double", "Double"},
		{ "Long", "Long", "Double", "Long", "Double","Double",},
		{ "Long", "Long", "Long", "Long", "Long", "Long", "Long","Long",},
		{ "Long", "Double", "Double", "Double", },
		{ "Long", "Long", "Double", "Double", "Double",},
		{ "Long", "Double", "Double", "Double", "Double","Double","Double",},
		{ "Long", "Double", "Double", "Double","Double","Double",},
		{ "Long", "Long", "Double", "Double", "Double",},
		{ "Long", "String[5]", "Double", "Double", "Double","Double","Double","Double","Double",},
	};
	
	public static final List<String> CATEGORY_LIST = new ArrayList<String>(Arrays
			.asList(MonitorGui.CATEGORY));
	private static final long serialVersionUID = 1L;
	
	// 当前的Monitor
	private Monitor monitor = null;
	private JPanel mainPanel =new JPanel();
	private JPanel upPanel =new JPanel();
	private JPanel downPanel =new JPanel();
	private CardLayout chartPanelCard;
	private CardLayout checkboxPanelCard;
	private CardLayout tablePanelCard;

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
		// TODO Auto-generated method stub
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
