package org.apache.jmeter.monitor;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.lang.StringUtils;
import org.apache.jmeter.gui.util.FileDialoger;
import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
import org.apache.jmeter.gui.util.YccCustomTable;
import org.apache.jmeter.monitor.gui.MonitorGui;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.ObjectTableModel;
import org.apache.jorphan.gui.RendererUtils;
import org.apache.jorphan.reflect.Functor;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public abstract class MonitorModel implements ItemListener, ActionListener{
	public static final String TYPE_LONG = "Long";
	public static final String TYPE_DOUBLE = "Double";
	public static final String TYPE_STRING = "String[5]";
	public static final String PRE_TITLE = "monitor_";
	public static final String PRE_NUM_AXISL = "number_axis_";
	public static final String PRE_NUM_AXISR = "number_axis_r_";
	public static final String PRE_TK = "tk_";
	
	private ChartPanel chartPanel = null;
	
	private JPanel checkboxPanel = new JPanel();
	
	private JPanel tablePanel = new JPanel(new BorderLayout());
	
	protected Map<String, TimeSeries> dataMap = new HashMap<String, TimeSeries>();
	
	private JFreeChart localJFreeChart = null;
	
	protected String host = null;

	protected String pathName = null;
	
	protected String category = null;
	
	protected TimeSeriesCollection localTimeSeriesCollectionL = new TimeSeriesCollection();

	protected TimeSeriesCollection localTimeSeriesCollectionR = new TimeSeriesCollection();

	protected XYLineAndShapeRenderer localXYLineAndShapeRendererL = null;

	protected XYLineAndShapeRenderer localXYLineAndShapeRendererR = null;

	protected NumberAxis localNumberAxisL = null;

	protected NumberAxis localNumberAxisR = null;

	protected Map<JCheckBox, String> cbMap = new HashMap<JCheckBox, String>();
	protected Map<String, MonitorDataStat> tableRowMap = new HashMap<String, MonitorDataStat>();
	
	private long dataEndPosition = 0;

	private boolean firstFetch = true;

	protected JLabel pid=new JLabel();
	
	private JButton save = new JButton("Save Graph");
	
	private JLabel info = new JLabel();
	
	private transient ObjectTableModel model;
	
	private JTable myJTable = null;
	
	private static final String[] COLUMNS = { "jf_name", "spline_visualizer_average",
		"spline_visualizer_minimum", "spline_visualizer_maximum", "jf_last", };
	
	private static final TableCellRenderer[] RENDERERS = new TableCellRenderer[] {
			null, // Label
			null, // Mean
			null, // Min
			null, // Max
			null, // Max
	};
	
	public MonitorModel(){
		// 初始化列表
		model = new ObjectTableModel(COLUMNS, MonitorDataStat.class, new Functor[] {
			new Functor("getLabel"), new Functor("getAverage"),
			new Functor("getMin"), new Functor("getMax"),
			new Functor("getLast"), }, new Functor[] { null, null, null,
			null, null }, new Class[] { String.class, BigDecimal.class,
			BigDecimal.class, BigDecimal.class, BigDecimal.class });
		
		myJTable = new YccCustomTable(model);
		myJTable.getTableHeader().setDefaultRenderer(
				new HeaderAsPropertyRenderer());
		myJTable.setPreferredScrollableViewportSize(new Dimension(500, 100));
		RendererUtils.applyRenderers(myJTable, RENDERERS);
		JScrollPane myScrollPane = new JScrollPane(myJTable);
		tablePanel.add(myScrollPane,BorderLayout.CENTER);
		
		DateAxis localDateAxis = new DateAxis(JMeterUtils
				.getResString("monitor_time"));

		// 设置左侧主轴
		localNumberAxisL = new NumberAxis("");
		localNumberAxisL.setTickLabelFont(new Font("SansSerif", 0, 12));
		localNumberAxisL.setLabelFont(new Font("SansSerif", 0, 14));

		localDateAxis.setTickLabelFont(new Font("SansSerif", 0, 12));
		localDateAxis.setLabelFont(new Font("SansSerif", 0, 14));
		// TODO 显示格式加入配置
		localDateAxis.setDateFormatOverride(new SimpleDateFormat("MM-dd hh:mm:ss"));

		localXYLineAndShapeRendererL = new XYLineAndShapeRenderer(true, false);
		localXYLineAndShapeRendererL.setSeriesStroke(0, new BasicStroke(1.0F,
				0, 2));
		// XYPlot localXYPlot = new XYPlot(localTimeSeriesCollectionL,
		// localDateAxis, localNumberAxisL, localXYLineAndShapeRendererL);
		XYPlot localXYPlot = new XYPlot();
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
		checkboxPanel.setLayout(new FlowLayout());
		checkboxPanel.add(pid);
		checkboxPanel.add(new JLabel("    "));
		checkboxPanel.add(info);
		checkboxPanel.add(save);
		save.addActionListener(this);
		localJFreeChart = new JFreeChart("", new Font("SansSerif", 1, 24),
				localXYPlot, true);
		chartPanel = new ChartPanel(localJFreeChart, true);
	}
	

	public void initSecondValueAxis(String category) {

		if (MonitorGui.MONITOR_CONFIGURE.get(category).getYAxisCount() == 1) {
			return;
		}
		XYPlot localXYPlot = localJFreeChart.getXYPlot();
		localNumberAxisR = new NumberAxis(JMeterUtils
				.getResString(PRE_NUM_AXISR + category));
		localNumberAxisR.setStandardTickUnits(NumberAxis
				.createIntegerTickUnits());
		localXYPlot.setRangeAxis(1, localNumberAxisR);
		localXYPlot.setDataset(1, localTimeSeriesCollectionR);
		localXYLineAndShapeRendererR = new XYLineAndShapeRenderer(true, false);
		localXYLineAndShapeRendererR.setBaseShapesFilled(true);
		localXYLineAndShapeRendererR.setSeriesStroke(0, new BasicStroke(1.0F,
				0, 2));
		localXYPlot.setRenderer(1, localXYLineAndShapeRendererR);
		localXYPlot.mapDatasetToRangeAxis(1, 1);
	}
	
	public ChartPanel getChartPanel() {
		return chartPanel;
	}
	
	public JPanel getCheckBoxPanel() {
		return checkboxPanel;
	}
	
	public JPanel getTablePanel() {
		return tablePanel;
	}


	public void setNumberAxis(String category) {
		localNumberAxisL.setLabel(JMeterUtils.getResString(PRE_NUM_AXISL
				+ category));
	}

	public void setTitle(String category) {
		localJFreeChart
				.setTitle(JMeterUtils.getResString(PRE_TITLE + category));
	}
	

	public boolean isFirstFetch() {
		return firstFetch;
	}

	public void setFirstFetch(boolean firstFetch) {
		this.firstFetch = firstFetch;
	}


	public void setHost(String host) {
		this.host = host;
	}

	public String getHost() {
		return host;
	}
	

	public long getDataEndPosition() {
		return dataEndPosition;
	}

	public void setDataEndPosition(long dataEndPosition) {
		this.dataEndPosition = dataEndPosition;
	}

	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	
	public synchronized void addTimeSeries(String name, TimeSeries ts) {
		if (MonitorGui.MONITOR_CONFIGURE.get(category).getYAxisCount() == 1) {
			localTimeSeriesCollectionL.addSeries(ts);
		} else {
			String[] tmp = name.split("\\$\\$");
			String item = tmp[2];
			String state = MonitorGui.MONITOR_CONFIGURE.get(category).getShowType(item);
			if (state.equals("1")) {
				localTimeSeriesCollectionL.addSeries(ts);
			} else if(state.equals("2")) {
				localTimeSeriesCollectionR.addSeries(ts);
			} else {
			}
		}
		dataMap.put(name, ts);
		
		String[] tmp = name.split("\\$\\$");
		JCheckBox jb = createChooseCheckBox(tmp[2], Color.BLACK);
		checkboxPanel.add(jb);
		cbMap.put(jb, tmp[2]);
		// 增加列表行
		MonitorDataStat mds=new MonitorDataStat();
		mds.setLabel(tmp[2]);
		model.insertRow(mds, model.getRowCount());
		tableRowMap.put(tmp[2], mds);
	}
	
	private JCheckBox createChooseCheckBox(String labelResourceName, Color color) {
		JCheckBox checkBox = new JCheckBox(labelResourceName);
		checkBox.setSelected(true);
		checkBox.addItemListener(this);
		checkBox.setForeground(color);
		checkBox.setToolTipText(JMeterUtils.getResString(PRE_TK+category+"_"+labelResourceName));
		return checkBox;
	}
	
	public void deleteTimeSeries(String name) {
		dataMap.remove(name);
	}

	public Collection<TimeSeries> getAllTimeSeries() {
		return dataMap.values();
	}

	public TimeSeries getTimeSeries(String name) {
		return dataMap.get(name);
	}

	public void setLineColor() {

		int len = localTimeSeriesCollectionL.getSeriesCount();
		if (len != 0) {
			for (int i = 0; i < len; i++) {
				localXYLineAndShapeRendererL.setSeriesPaint(i,
						getColor(MonitorGui.MONITOR_CONFIGURE.get(category).getLineColor((String) localTimeSeriesCollectionL.getSeries(i).getKey())));
			}
		}

		len = localTimeSeriesCollectionR.getSeriesCount();
		if (len != 0) {
			for (int i = 0; i < len; i++) {
				localXYLineAndShapeRendererR.setSeriesPaint(i,
						getColor(MonitorGui.MONITOR_CONFIGURE.get(category).getLineColor((String) localTimeSeriesCollectionR.getSeries(i).getKey())));
			}
		}
	}
	
	protected Color getColor(String name) {
		if (name.equals("RED")) {
			return Color.RED;
		} else if (name.equals("RED")) {
			return Color.RED;
		} else if (name.equals("GREEN")) {
			return Color.GREEN;
		} else if (name.equals("ORANGE")) {
			return Color.ORANGE;
		} else if (name.equals("BLUE")) {
			return Color.BLUE;
		} else if (name.equals("YELLOW")) {
			return Color.YELLOW;
		} else if (name.equals("MAGENTA")) {
			return Color.MAGENTA;
		} else if (name.equals("PINK")) {
			return Color.PINK;
		} else if (name.equals("CYAN")) {
			return Color.CYAN;
		} else if (name.equals("DARK_GRAY")) {
			return Color.DARK_GRAY;
		} else if (name.equals("BLACK")) {
			return Color.BLACK;
		} else {
			return Color.GRAY;

		}
	}


	public void updateGui(String category, String[] fs, String[] strings) {
		for (int j = 1; j < fs.length; j++) {
			String name = pathName + "$$" + fs[j];
			TimeSeries ts = dataMap.get(name);
			if (ts == null) {
				continue;
			}

			Date time = null;
			try {
				time = new Date(Long.parseLong(strings[0]));
			} catch (NumberFormatException e) {
				System.out.println("Error date value:" + strings[j]);
			}
			String type = MonitorGui.MONITOR_CONFIGURE.get(category).getDataType(fs[j]);
			if (type.equals(MonitorModel.TYPE_LONG)) {
				Long v = Long.parseLong(StringUtils.strip(strings[j]));
				updateGui(ts, new Second(time), v);
			} else if (type.equals(MonitorModel.TYPE_DOUBLE)) {
				Double v = null;
				if (category.equals("net")) {
					v = Double.parseDouble(StringUtils.strip(strings[j + 9]));
				} else {
					v = Double.parseDouble(StringUtils.strip(strings[j]));
				}
				updateGui(ts, new Second(time), v);
			}
		}
	}
	
	protected synchronized void updateGui(TimeSeries ts, Second s, Number v) {
		ts.addOrUpdate(s, v);
		tableRowMap.get(ts.getKey()).addData(v);
		myJTable.repaint();
	}

	public void itemStateChanged(ItemEvent e) {
		String name = cbMap.get(e.getSource());
		int len = localTimeSeriesCollectionL.getSeriesCount();
		if (len != 0) {
			for (int i = 0; i < len; i++) {
				if (localTimeSeriesCollectionL.getSeries(i).getKey().equals(
						name)) {
					localXYLineAndShapeRendererL.setSeriesVisible(i, e
							.getStateChange() == ItemEvent.SELECTED);
				}
			}
		}

		len = localTimeSeriesCollectionR.getSeriesCount();
		if (len != 0) {
			for (int i = 0; i < len; i++) {
				if (localTimeSeriesCollectionR.getSeries(i).getKey().equals(
						name)) {
					localXYLineAndShapeRendererR.setSeriesVisible(i, e
							.getStateChange() == ItemEvent.SELECTED);
				}
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		JFileChooser chooser = FileDialoger.promptToSaveFile(category+".png");
		if (chooser == null) {
			return;
		}
		saveAsFile(chooser.getSelectedFile().getAbsolutePath());
		JOptionPane.showMessageDialog(null, "保存成功", "完成",
				JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void saveAsFile(String outputPath) {
		FileOutputStream out = null;
		try {
			File outFile = new File(outputPath);
			if (!outFile.getParentFile().exists()) {
				outFile.getParentFile().mkdirs();
			}
			out = new FileOutputStream(outputPath);
			// 保存为PNG
			ChartUtilities.writeChartAsPNG(out, localJFreeChart, 800, 600);
			// 保存为JPEG
			// ChartUtilities.writeChartAsJPEG(out, chart, 500, 400);
			out.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
	}
}
