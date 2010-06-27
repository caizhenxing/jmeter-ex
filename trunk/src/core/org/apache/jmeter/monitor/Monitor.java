package org.apache.jmeter.monitor;

import java.awt.BasicStroke;
import java.awt.Color;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.lang.StringUtils;
import org.apache.jmeter.gui.util.FileDialoger;
import org.apache.jmeter.monitor.gui.MonitorGui;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.util.JMeterUtils;
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

/**
 * Monitor
 * @author chenchao.yecc
 * @version jex002A
 *
 */
public class Monitor extends AbstractTestElement implements Serializable, ItemListener, ActionListener {

	private static final long serialVersionUID = 1L;
	private static final String TYPE_LONG = "Long";
	private static final String TYPE_DOUBLE = "Double";
	private static final String TYPE_STRING = "String[5]";
	public static final String PRE_TITLE = "monitor_";
	private static final String PRE_NUM_AXISL = "number_axis_";
	private static final String PRE_NUM_AXISR = "number_axis_r_";
	private static final String PRE_TK = "tk_";

	private Map<String, TimeSeries> dataMap = new HashMap<String, TimeSeries>();

	private String pathName = null;

	private String host = null;

	private String category = null;

	private ChartPanel chartPanel = null;

	private JFreeChart localJFreeChart = null;

	private long dataEndPosition = 0;

	private boolean firstFetch = true;

	private TimeSeriesCollection localTimeSeriesCollectionL = new TimeSeriesCollection();

	private TimeSeriesCollection localTimeSeriesCollectionR = new TimeSeriesCollection();

	private XYLineAndShapeRenderer localXYLineAndShapeRendererL = null;

	private XYLineAndShapeRenderer localXYLineAndShapeRendererR = null;

	private NumberAxis localNumberAxisL = null;

	private NumberAxis localNumberAxisR = null;

	private Map<JCheckBox, String> cbMap = new HashMap<JCheckBox, String>();

	private JPanel checkboxPanel = new JPanel();
	
	private JButton save = new JButton("Save Graph");

	public Monitor() {
		DateAxis localDateAxis = new DateAxis(JMeterUtils
				.getResString("monitor_time"));

		// 设置左侧主轴
		localNumberAxisL = new NumberAxis("");
		localNumberAxisL.setTickLabelFont(new Font("SansSerif", 0, 12));
		localNumberAxisL.setLabelFont(new Font("SansSerif", 0, 14));

		localDateAxis.setTickLabelFont(new Font("SansSerif", 0, 12));
		localDateAxis.setLabelFont(new Font("SansSerif", 0, 14));

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
		checkboxPanel.add(save);
		save.addActionListener(this);
		localJFreeChart = new JFreeChart("", new Font("SansSerif", 1, 24),
				localXYPlot, true);
		chartPanel = new ChartPanel(localJFreeChart, true);
	}

	public void initSecondValueAxis(int index) {

		if (MonitorGui.COLLECTION_COUNT[index] == 1) {
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

	public void setLineColor() {

		int pos = -1;
		String[] lineSpec = pathName.split("\\$\\$");
		String name = lineSpec[1];
		for (int i = 0; i < MonitorGui.CATEGORY.length; i++) {
			if (name.equals(MonitorGui.CATEGORY[i])) {
				pos = i;
				break;
			}
		}
		List<String> itemLst = new ArrayList<String>(Arrays
				.asList(MonitorGui.ITEM[pos]));
		int len = localTimeSeriesCollectionL.getSeriesCount();
		if (len != 0) {
			for (int i = 0; i < len; i++) {
				int c = itemLst.indexOf(localTimeSeriesCollectionL.getSeries(i)
						.getKey());
				localXYLineAndShapeRendererL.setSeriesPaint(i,
						getColor(MonitorGui.COLOR[pos][c]));
			}
		}

		len = localTimeSeriesCollectionR.getSeriesCount();
		if (len != 0) {
			for (int i = 0; i < len; i++) {
				int c = itemLst.indexOf(localTimeSeriesCollectionR.getSeries(i)
						.getKey());
				localXYLineAndShapeRendererR.setSeriesPaint(i,
						getColor(MonitorGui.COLOR[pos][c]));
			}
		}
	}

	private Color getColor(String name) {
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

	public void setNumberAxis(String category) {
		localNumberAxisL.setLabel(JMeterUtils.getResString(PRE_NUM_AXISL
				+ category));
	}

	public void setTitle(String category) {
		localJFreeChart
				.setTitle(JMeterUtils.getResString(PRE_TITLE + category));
	}

	public JPanel getCheckBoxPanel() {
		return this.checkboxPanel;
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

	public ChartPanel getChartPanel() {
		return chartPanel;
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

	public void addTimeSeries(String name, TimeSeries ts) {
		int index = MonitorGui.CATEGORY_LIST.indexOf(category);
		if (MonitorGui.COLLECTION_COUNT[index] == 1) {
			localTimeSeriesCollectionL.addSeries(ts);
		} else {
			String[] items = MonitorGui.ITEM[index];
			String[] lines = MonitorGui.LINE_COLLECTION[index];
			List<String> lst = new ArrayList<String>(Arrays.asList(items));
			String[] tmp = name.split("\\$\\$");
			String item = tmp[2];
			int pos = lst.indexOf(item);
			String state = lines[pos];
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

	public void updateGui(String category, String[] fs, String[] strings) {
		// if (category.equals("net")||category.equals("io")) {
		// return;
		// }
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
			int pos = MonitorGui.CATEGORY_LIST.indexOf(category);
			String type = MonitorGui.TYPE[pos][j];
			if (type.equals(Monitor.TYPE_LONG)) {
				Long v = Long.parseLong(StringUtils.strip(strings[j]));
				updateGui(ts, new Second(time), v);
			} else if (type.equals(Monitor.TYPE_DOUBLE)) {
				// TODO 把Monitor根据category进行抽象，将net的判断加至子类
				Double v = null;
				if (category.equals("net")) {
					v = Double.parseDouble(StringUtils.strip(strings[j + 9]));
				} else {
					v = Double.parseDouble(StringUtils.strip(strings[j]));
				}
				updateGui(ts, new Second(time), v);
			} else if (type.equals(Monitor.TYPE_STRING)) {

			}
		}
	}

	private synchronized void updateGui(TimeSeries ts, Second s, Number v) {
		ts.addOrUpdate(s, v);
	}

	@Override
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

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
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
