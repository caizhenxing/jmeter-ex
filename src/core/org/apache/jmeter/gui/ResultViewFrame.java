package org.apache.jmeter.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;
import org.apache.jmeter.control.MonitorClientModel;
import org.apache.jmeter.control.gui.ServerBenchGui;
import org.apache.jmeter.util.JMeterUtils;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class ResultViewFrame extends JFrame implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	private JFreeChart localJFreeChart = null;
	private JComboBox projects=new JComboBox();
	private JTextField fromTf = new JTextField(12);
	private JTextField toTf = new JTextField(12);
	private JTextField url = new JTextField(12);
	private JButton update = new JButton("更新工程");
	private JButton view = new JButton("开始查看");
	private SimpleDateFormat format= new  SimpleDateFormat("mm-dd hh:MM");
	private Map<Integer,YccTab> tabMap=new HashMap<Integer,YccTab>();
	private JButton savegraph=new JButton("保存当前图片");
	private JButton saveall = new JButton("保存所有图片");
	private long beginTime=0;
	private long endTime=0;
	// 用于查看历史按钮活性设置的回调
	private ServerBenchGui benchgui=null;
	private MonitorClientModel model = new MonitorClientModel();
	
	public void setServerBenchGui(ServerBenchGui benchgui){
		this.benchgui=benchgui;
	}
	
	public void setMonitorClientModel(MonitorClientModel model){
		this.model=model;
	}
	
	public void showFrame() {
		// 取最新的工程列表
		List<String> lst = null;
		try {
			lst = model.getProjects(benchgui.getCurrentServerUrl());
			projects.setModel(new DefaultComboBoxModel(lst.toArray()));
		} catch (MalformedURLException e1) {
			JOptionPane.showMessageDialog(GuiPackage.getInstance()
					.getMainFrame(), JMeterUtils
					.getResString("server_bench_failed")
					+ model.getServiceUrl(), JMeterUtils
					.getResString("server_bench_error"),
					JOptionPane.ERROR_MESSAGE);
			return;
		} catch (UndeclaredThrowableException e1) {
			JOptionPane.showMessageDialog(GuiPackage.getInstance()
					.getMainFrame(), JMeterUtils
					.getResString("server_bench_failed")
					+ model.getServiceUrl(), JMeterUtils
					.getResString("server_bench_error"),
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		this.pack();
		// 初始化控件
		this.clearAll();
		// 只在第一次显示的时候清空日期
		fromTf.setText("");
		toTf.setText("");
		url.setText(benchgui.getCurrentServerUrl());
		JMeterUtils.centerWindow(this);
	}
	
	private void clearAll() {
		for (Iterator<Integer> iterator = tabMap.keySet().iterator(); iterator
				.hasNext();) {
			int index = iterator.next();
			YccTab tab = tabMap.get(index);
			tab.removeAllSubTabPanel();
		}
	}
	
	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			benchgui.setHistoryButtonEnable(true);
		}
		super.processWindowEvent(e);
	}

	public ResultViewFrame() {
		update.addActionListener(this);
		view.addActionListener(this);
		savegraph.addActionListener(this);
		saveall.addActionListener(this);
		this.setTitle("历史数据查看器");
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		initGui();
	}
	
	private void initGui(){
		// 上层面板
		JPanel upPanel=new JPanel(new FlowLayout());
		upPanel.add(new JLabel("工程"));
		upPanel.add(projects);
		upPanel.add(update);
		upPanel.add(new JLabel("开始时间"));
		upPanel.add(fromTf);
		upPanel.add(new JLabel(" 结束时间"));
		upPanel.add(toTf);
		upPanel.add(view);
		// 中层面板
		JPanel midPanel=new JPanel(new BorderLayout());
		
		// 下层面板
		JPanel downPanel=new JPanel(new FlowLayout());
		downPanel.add(savegraph);
		downPanel.add(saveall);
	
//		tab.add("Cpu",getChartPanel());
//		tab.add("Memory",getChartPanel());
//		tab.add("Net",getChartPanel());
//		tab.add("IO",getChartPanel());
//		midPanel.add(tab,BorderLayout.CENTER);?
		
		this.getContentPane().add(upPanel,BorderLayout.NORTH);
		this.getContentPane().add(midPanel,BorderLayout.CENTER);
		this.getContentPane().add(downPanel,BorderLayout.SOUTH);
	}
	
	private void addTimeSeries(TimeSeriesCollection localTimeSeriesCollectionL) {
		Random r= new Random();
		TimeSeries ts = new TimeSeries("T",Millisecond.class);
				localTimeSeriesCollectionL.addSeries(ts);
				for (int i = 0; i < 10000; i++) {
					Date time = new Date();
					ts.addOrUpdate(new Millisecond(time), r.nextInt(100));
					try {
						Thread.sleep(0);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
	}
	
	private void addListToTimeSeries() {
		TimeSeries ts = new TimeSeries("T",
				org.jfree.data.time.Second.class);
	}
	
	class YccTab extends JTabbedPane{
		JTabbedPane subTab = new JTabbedPane();
		public void addSubTabPanel(String title,ChartPanel chart){
			super.addTab(title, chart);
		}
		
		public void removeAllSubTabPanel(){
			super.removeAll();
		}
	}
	
	public static void main(String[] args) {
		long i=System.currentTimeMillis();
		ResultViewFrame r=new ResultViewFrame();
		r.pack();
		r.setTitle("YccTry");
		r.setVisible(true);
		System.out.println(System.currentTimeMillis()-i);
	}

	private boolean checkDate() {
		String from = fromTf.getText();
		String to = toTf.getText();
		// null验证
		if (StringUtils.isEmpty(from)) {
			// 开始日期为空
			return false;
		}
		if (StringUtils.isEmpty(to)) {
			// 结束日期为空
			return false;
		}

		try {
			Date fromtime = format.parse(from);
			beginTime = fromtime.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
		try {
			Date totime = format.parse(to);
			endTime = totime.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==update){
			System.out.println("update");
		} else if(e.getSource()==view){
			if (checkDate()) {
				// TODO 生成Tab页面代码
				Map<String,ChartPanel> chartMap = model.getChartPanel(benchgui.getCurrentServerUrl(),(String)this.projects.getSelectedItem(),beginTime,endTime);
			}
//			model.getAllDataForProject(agent, startTime, stopTime);
//			startField.getText();
//			endField.getText();
//			model.view();
		} else if(e.getSource()==savegraph){
			System.out.println("savegraph");
		} else if(e.getSource()==saveall){
			System.out.println("saveall");
			
		}
		
	}
}
