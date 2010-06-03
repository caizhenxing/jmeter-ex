package org.apache.jmeter.visualizers;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.jmeter.util.Calculator;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 * jfreechart component
 * 
 * @author chenchao.yecc
 * @since jex001A
 */
public class JFreeChartGraph {
	private ChartPanel chartPanel;
	private JFreeChart jfc = null;
	private TimeSeriesCollection timeseriescollection;
	private XYLineAndShapeRenderer xylinerenderer;
	private TimeSeries m_average;
	private TimeSeries m_tps;
	private TimeSeries m_deviation;
	private List<TimeSeries> timeSeriesList;
	private Map<String, TimeSeries> stateMap = new HashMap<String, TimeSeries>();
	public static final String AVERANGE = "average";
	public static final String TPS = "tps";
	public static final String DEVIATION = "deviation";

	public JFreeChartGraph() {
		init();
	}

	private void init() {
		timeSeriesList = new LinkedList<TimeSeries>();
		m_average = new TimeSeries(AVERANGE,
				org.jfree.data.time.Millisecond.class);
		m_tps = new TimeSeries(TPS, org.jfree.data.time.Millisecond.class);
		m_deviation = new TimeSeries(DEVIATION,
				org.jfree.data.time.Millisecond.class);
		stateMap.put(AVERANGE, m_average);
		stateMap.put(DEVIATION, m_deviation);
		stateMap.put(TPS, m_tps);
		timeSeriesList.add(m_average);
		timeSeriesList.add(m_tps);
		timeSeriesList.add(m_deviation);
		timeseriescollection = new TimeSeriesCollection();
		for (Iterator<TimeSeries> iterator = timeSeriesList.iterator(); iterator
				.hasNext();) {
			timeseriescollection.addSeries(iterator.next());
		}
		jfc = ChartFactory.createTimeSeriesChart("Title", "unit", "yaxisName",
				timeseriescollection, true, true, false);
		// jfc.setTitle(new TextTitle("图表", new Font("黑体", Font.BOLD, 20)));
		// 取得统计图表
		XYPlot xyplot = jfc.getXYPlot();

		xylinerenderer = (XYLineAndShapeRenderer) xyplot.getRenderer();
		xylinerenderer.setSeriesPaint(0, Color.BLUE);
		xylinerenderer.setSeriesPaint(1, Color.GREEN);
		xylinerenderer.setSeriesPaint(2, Color.RED);

		ValueAxis valueaxis = xyplot.getDomainAxis();
		valueaxis.setAutoRange(true);
		valueaxis.setFixedAutoRange(30000D);
		valueaxis = xyplot.getRangeAxis();
		chartPanel = new ChartPanel(jfc);
		chartPanel.setPreferredSize(new Dimension(600, 450));
	}

	public ChartPanel getJFreeChartPanel() {
		return chartPanel;
	}

	public void updateGui(final Calculator tot) {
		m_average.addOrUpdate(new Millisecond(), tot.getMean());
		m_tps.addOrUpdate(new Millisecond(), tot.getRate());
		m_deviation.addOrUpdate(new Millisecond(), tot.getStandardDeviation());
	}

	public void clearAllData() {
		int len = timeseriescollection.getSeriesCount();
		if (len != 0) {
			for (int i = 0; i < len; i++) {
				timeseriescollection.getSeries(i).clear();
			}
			m_average.clear();
		}
	}

	public void saveAsFile(String outputPath) {
		FileOutputStream out = null;
		try {
			File outFile = new File(outputPath);
			if (!outFile.getParentFile().exists()) {
				outFile.getParentFile().mkdirs();
			}
			out = new FileOutputStream(outputPath);
			// 保存为PNG
			ChartUtilities.writeChartAsPNG(out, jfc, 800, 600);
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

	public void setLineDisplay(String item, boolean selected) {
		TimeSeries t = stateMap.get(item);
		if (selected) {
			timeseriescollection.addSeries(t);
		} else {
			timeseriescollection.removeSeries(t);
		}
		updateColor();
	}

	private void updateColor() {
		int len = timeseriescollection.getSeriesCount();
		if (len != 0) {
			for (int i = 0; i < len; i++) {
				TimeSeries t = timeseriescollection.getSeries(i);
				if (t.getKey().equals(AVERANGE)) {
					xylinerenderer.setSeriesPaint(i, Color.BLUE);
				} else if (t.getKey().equals(TPS)) {
					xylinerenderer.setSeriesPaint(i, Color.GREEN);
				} else if (t.getKey().equals(DEVIATION)) {
					xylinerenderer.setSeriesPaint(i, Color.RED);
				} else {

				}
			}
		}
	}
}
