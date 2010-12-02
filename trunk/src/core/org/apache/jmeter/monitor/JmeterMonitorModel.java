package org.apache.jmeter.monitor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.math.BigDecimal;
import java.util.Date;

import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.lang.StringUtils;
import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
import org.apache.jmeter.gui.util.YccCustomTable;
import org.apache.jmeter.monitor.gui.MonitorGui;
import org.apache.jorphan.gui.NumberRenderer;
import org.apache.jorphan.gui.ObjectTableModel;
import org.apache.jorphan.gui.RendererUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.jorphan.reflect.Functor;
import org.apache.log.Logger;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;

/**
 * 监控Jmeter数据的监控器
 * 
 * @author chenchao.yecc
 * @since jex003A
 * 
 */
public class JmeterMonitorModel extends MonitorModel {
	private static final Logger log = LoggingManager.getLoggerForClass();

	private static final String[] COLUMNS = { "jf_name",
			"aggregate_report_count", "aggregate_graph_response_time",
			"aggregate_report_max", "aggregate_report_min",
//			"aggregate_report_stddev", 
			"aggregate_report_error%",
			"aggregate_report_rate" };

	private static final TableCellRenderer[] RENDERERS = new TableCellRenderer[] {
			null, // Label
			null, // count
			null, // respone time
			null, // Min
			null, // Max
//			null, // stddev
			new NumberRenderer("#0.00%"), // error
			new NumberRenderer("#0.00"), // tps
	};

	public synchronized void addTimeSeries(String name, TimeSeries ts) {
		if (MonitorGui.MONITOR_CONFIGURE.get(category).getYAxisCount() == 1) {
			localTimeSeriesCollectionL.addSeries(ts);
		} else {
			String[] tmp = name.split("\\$\\$");
			String item = tmp[1];
			String state = MonitorGui.MONITOR_CONFIGURE.get(category)
					.getShowType(item);
			if (state.equals("1")) {
				localTimeSeriesCollectionL.addSeries(ts);
			} else if (state.equals("2")) {
				localTimeSeriesCollectionR.addSeries(ts);
			} else {
			}
		}
		dataMap.put(name, ts);
		String[] tmp = name.split("\\$\\$");
		JCheckBox jb = createChooseCheckBox(tmp[1], Color.BLACK);
		checkboxPanel.add(jb);
		cbMap.put(jb, tmp[1]);
	}

	/*
	 * 在查看历史的时候将已经计算好的结果输出至表格上，Jmeter专用
	 * 
	 * @since jex003A
	 */
	public synchronized void addRowToTable(String name, MonitorDataStat mds) {
		String[] tmp = name.split("\\$\\$");
		boolean hasInTable = false;
		mds.setLabel(tmp[1]);
		for (int i = 0; i < model.getRowCount(); i++) {
			if (tmp[1].equals(model.getValueAt(i, 0))) {
				hasInTable = true;
				break;
			}
		}
		if (!hasInTable) {
			model.insertRow(mds, model.getRowCount());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.jmeter.monitor.MonitorModel#addLineToTable(java.lang.String)
	 */
	public synchronized void addLineToTable(String name) {
		// 增加列表行
		JmeterMonitorDataStat mds = new JmeterMonitorDataStat();
		mds.setLabel(name);
		model.insertRow(mds, model.getRowCount());
		tableRowMap.put(name, mds);
	}

	// 初始化列表
	protected void initTable() {
		model = new ObjectTableModel(COLUMNS, JmeterMonitorDataStat.class,
				new Functor[] { new Functor("getLabel"),
						new Functor("getCount"), new Functor("getResponeTime"),
						new Functor("getMinValue"), new Functor("getMaxValue"),
//						new Functor("getStddev"),
						new Functor("getError"),
						new Functor("getTps") }, new Functor[] { null, null,
						null, null, null, 
//						null, 
						null, null }, new Class[] {
						String.class, BigDecimal.class, BigDecimal.class,
						BigDecimal.class, BigDecimal.class, BigDecimal.class,
//						BigDecimal.class,
						BigDecimal.class, Double.class });

		myJTable = new YccCustomTable(model);
		myJTable.getTableHeader().setDefaultRenderer(
				new HeaderAsPropertyRenderer());
		myJTable.setPreferredScrollableViewportSize(new Dimension(500, 100));
		RendererUtils.applyRenderers(myJTable, RENDERERS);
		JScrollPane myScrollPane = new JScrollPane(myJTable);
		tablePanel.add(myScrollPane, BorderLayout.CENTER);
	}

	public void updateGui(String category, String[] fs, String[] strings) {
		updateAverageTime(strings);
		updateTps(strings);

		// 更新表
		tableRowMap.get(super.getHost()).addData(strings);
		myJTable.repaint();
	}

	private void updateGui(TimeSeries ts, String value, String t) {
		Date time = null;
		try {
			time = new Date(Long.parseLong(t));
		} catch (NumberFormatException e) {
			log.error("Error date value:" + t);
		}
		Long v = Long.parseLong(StringUtils.strip(value));
		updateGui(ts, new Second(time), v);
	}

	public synchronized void updateGui(TimeSeries ts, Second s, Number v) {
		ts.addOrUpdate(s, v);
	}

	private void updateAverageTime(String[] strings) {
		TimeSeries ts = dataMap.get("jmeter$$avgTime");
		updateGui(ts, strings[2], strings[0]);
	}

	private void updateTps(String[] strings) {
		TimeSeries ts = dataMap.get("jmeter$$avgTps");
		Date time = null;
		try {
			time = new Date(Long.parseLong(strings[0]));
		} catch (NumberFormatException e) {
			log.error("Error date value:" + strings[0]);
		}
		Double v = Double.parseDouble(StringUtils.strip(strings[7]));
		updateGui(ts, new Second(time), v);
	}
}
