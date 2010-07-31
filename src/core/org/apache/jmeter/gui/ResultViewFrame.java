package org.apache.jmeter.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
	private JButton update = new JButton("更新工程");
	private JButton view = new JButton("开始查看");
	private YccTab tab=new YccTab();
	private JButton savegraph=new JButton("保存当前图片");
	private JButton saveall = new JButton("保存所有图片");
	private ServerBenchGui benchgui=null;
	private MonitorClientModel model = new MonitorClientModel();
	
	public void setServerBenchGui(ServerBenchGui benchgui){
		this.benchgui=benchgui;
	}
	
	public void setMonitorClientModel(MonitorClientModel model){
		this.model=model;
	}
	
	public void showFrame() {
		this.pack();
		this.clearAll();
		JMeterUtils.centerWindow(this);
	}
	
	private void clearAll(){
		
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
	
		tab.add("Cpu",getChartPanel());
		tab.add("Memory",getChartPanel());
		tab.add("Net",getChartPanel());
		tab.add("IO",getChartPanel());
		midPanel.add(tab,BorderLayout.CENTER);
		
		this.getContentPane().add(upPanel,BorderLayout.NORTH);
		this.getContentPane().add(midPanel,BorderLayout.CENTER);
		this.getContentPane().add(downPanel,BorderLayout.SOUTH);
	}
	
	private ChartPanel getChartPanel(){
		DateAxis localDateAxis = new DateAxis("TT");

		// 设置左侧主轴
		NumberAxis localNumberAxisL = new NumberAxis("");
		localNumberAxisL.setTickLabelFont(new Font("SansSerif", 0, 12));
		localNumberAxisL.setLabelFont(new Font("SansSerif", 0, 14));

		localDateAxis.setTickLabelFont(new Font("SansSerif", 0, 12));
		localDateAxis.setLabelFont(new Font("SansSerif", 0, 14));
		// TODO 显示格式加入配置
		localDateAxis.setDateFormatOverride(new SimpleDateFormat("mm:ss:SSS"));

		XYLineAndShapeRenderer localXYLineAndShapeRendererL = new XYLineAndShapeRenderer(true, false);
		localXYLineAndShapeRendererL.setSeriesStroke(0, new BasicStroke(1.0F,
				0, 2));
		// XYPlot localXYPlot = new XYPlot(localTimeSeriesCollectionL,
		// localDateAxis, localNumberAxisL, localXYLineAndShapeRendererL);
		XYPlot localXYPlot = new XYPlot();
		TimeSeriesCollection localTimeSeriesCollectionL = new TimeSeriesCollection();

		localXYPlot.setDataset(0, localTimeSeriesCollectionL);
		localXYPlot.setRenderer(0, localXYLineAndShapeRendererL);
		localXYPlot.setDomainAxis(localDateAxis);
		localXYPlot.setRangeAxis(0, localNumberAxisL);
		localDateAxis.setAutoRange(true);
		localDateAxis.setLowerMargin(0.0D);
		localDateAxis.setUpperMargin(0.0D);
		localDateAxis.setTickLabelsVisible(true);
		localNumberAxisL.setStandardTickUnits(NumberAxis
				.createIntegerTickUnits());
		localJFreeChart = new JFreeChart("", new Font("SansSerif", 1, 24),
				localXYPlot, true);
		ChartPanel chartPanel = new ChartPanel(localJFreeChart, true);
	
		addTimeSeries(localTimeSeriesCollectionL);
		return chartPanel;
	}
	public void addTimeSeries(TimeSeriesCollection localTimeSeriesCollectionL) {
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
	
	public void addListToTimeSeries() {
		TimeSeries ts = new TimeSeries("T",
				org.jfree.data.time.Second.class);
	}
	
	public static void main(String[] args) {
		long i=System.currentTimeMillis();
		ResultViewFrame r=new ResultViewFrame();
		r.pack();
		r.setTitle("YccTry");
		r.setVisible(true);
		System.out.println(System.currentTimeMillis()-i);
	}
	
	private class YccTab extends JTabbedPane{
		
	}

	private void checkDate(){
		String from=fromTf.getText();
		String to=toTf.getText();
		// null验证
		if (StringUtils.isEmpty(from)) {
			// 开始日期为空
		}
		if (StringUtils.isEmpty(to)){
			// 结束日期为空
		}
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==update){
			System.out.println("update");
		} else if(e.getSource()==view){
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
