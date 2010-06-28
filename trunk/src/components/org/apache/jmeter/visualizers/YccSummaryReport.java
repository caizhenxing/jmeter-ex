package org.apache.jmeter.visualizers;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import org.apache.jmeter.gui.util.FileDialoger;
import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
import org.apache.jmeter.gui.util.YccCustomTable;
import org.apache.jmeter.samplers.Clearable;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.save.CSVSaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.Calculator;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
import org.apache.jorphan.gui.NumberRenderer;
import org.apache.jorphan.gui.ObjectTableModel;
import org.apache.jorphan.gui.RateRenderer;
import org.apache.jorphan.gui.RendererUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.jorphan.reflect.Functor;
import org.apache.jorphan.util.JOrphanUtils;
import org.apache.log.Logger;

/**
 * custom visualizer with jfreechart components
 * 
 * @author chenchao.yecc
 * @since jex001A
 */
public class YccSummaryReport extends AbstractVisualizer implements Clearable,
		ActionListener, ItemListener {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggingManager.getLoggerForClass();

	private static final String USE_GROUP_NAME = "useGroupName";

	private static final String[] COLUMNS = { "sampler_label",
			"aggregate_report_count", "average", "aggregate_report_min",
			"aggregate_report_max", "aggregate_report_stddev",
			"aggregate_report_error%", "aggregate_report_rate",
			"aggregate_report_bandwidth", "average_bytes", };

	private final String TOTAL_ROW_LABEL = JMeterUtils
			.getResString("aggregate_report_total_label");

	private JTable myJTable;

	private JScrollPane myScrollPane;

	private final JButton saveTable = new JButton(JMeterUtils
			.getResString("aggregate_graph_save_table"));

	private final JCheckBox useGroupName = new JCheckBox(JMeterUtils
			.getResString("aggregate_graph_use_group_name"));

	private transient ObjectTableModel model;

	private final Map<String, Calculator> tableRows = Collections
			.synchronizedMap(new HashMap<String, Calculator>());

	// Column renderers
	private static final TableCellRenderer[] RENDERERS = new TableCellRenderer[] {
			null, // Label
			null, // count
			null, // Mean
			null, // Min
			null, // Max
			new NumberRenderer("#0.00"), // Std Dev.
			new NumberRenderer("#0.00%"), // Error %age
			new RateRenderer("#.0"), // Throughpur
			new NumberRenderer("#0.00"), // kB/sec
			new NumberRenderer("#.0"), // avg. pageSize
	};

	private JFreeChartGraph m_jfGraph;
	private JCheckBox average;
	private JCheckBox deviation;
	private JCheckBox throughput;
	private final JButton saveGraph = new JButton("Save Graph Result");

	public YccSummaryReport() {
		super();
		model = new ObjectTableModel(COLUMNS, Calculator.class,
				new Functor[] { new Functor("getLabel"),
						new Functor("getCount"),
						new Functor("getMeanAsNumber"), new Functor("getMin"),
						new Functor("getMax"),
						new Functor("getStandardDeviation"),
						new Functor("getErrorPercentage"),
						new Functor("getRate"), new Functor("getKBPerSecond"),
						new Functor("getAvgPageBytes"), }, new Functor[] {
						null, null, null, null, null, null, null, null, null,
						null },
				new Class[] { String.class, Long.class, Long.class, Long.class,
						Long.class, String.class, String.class, String.class,
						String.class, String.class });
		m_jfGraph = new JFreeChartGraph();
		clearData();
		init();
	}

	/**
	 * @deprecated - only for use in testing
	 */
	public static boolean testFunctors() {
		YccSummaryReport instance = new YccSummaryReport();
		return instance.model.checkFunctors(null, instance.getClass());
	}

	public String getLabelResource() {
		return "ycc_summary_report";
	}

	public void add(SampleResult res) {
		Calculator row = null;
		final String sampleLabel = res
				.getSampleLabel(useGroupName.isSelected());
		synchronized (tableRows) {
			row = tableRows.get(sampleLabel);
			if (row == null) {
				row = new Calculator(sampleLabel);
				tableRows.put(row.getLabel(), row);
				model.insertRow(row, model.getRowCount() - 1);
			}
		}
		// Synch is needed because multiple threads can update the counts.
		synchronized (row) {
			row.addSample(res);
		}
		Calculator tot = ((Calculator) tableRows.get(TOTAL_ROW_LABEL));
		synchronized (tot) {
			tot.addSample(res);
		}
		m_jfGraph.updateGui(tot);
		model.fireTableDataChanged();
	}

	/**
	 * @see org.apache.jmeter.samplers.Clearable#clearData()
	 */
	public void clearData() {
		synchronized (tableRows) {
			model.clearData();
			tableRows.clear();
			tableRows.put(TOTAL_ROW_LABEL, new Calculator(TOTAL_ROW_LABEL));
			model.addRow(tableRows.get(TOTAL_ROW_LABEL));
			m_jfGraph.clearAllData();
		}
	}

	/**
	 * Main visualizer setup.
	 */
	private void init() {
		this.setLayout(new BorderLayout());

		// MAIN PANEL
		JPanel mainPanel = new JPanel();
		Border margin = new EmptyBorder(10, 10, 5, 10);

		mainPanel.setBorder(margin);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		mainPanel.add(makeTitlePanel());

		myJTable = new YccCustomTable(model);
		myJTable.getTableHeader().setDefaultRenderer(
				new HeaderAsPropertyRenderer());
		myJTable.setPreferredScrollableViewportSize(new Dimension(500, 50));
		RendererUtils.applyRenderers(myJTable, RENDERERS);
		myScrollPane = new JScrollPane(myJTable);
		this.add(mainPanel, BorderLayout.NORTH);

		JPanel content = new JPanel(new BorderLayout());
		content.add(myScrollPane, BorderLayout.NORTH);
		content.add(createChoosePanel(), BorderLayout.CENTER);
		content.add(m_jfGraph.getJFreeChartPanel(), BorderLayout.SOUTH);
		this.add(content, BorderLayout.CENTER);
		saveTable.addActionListener(this);
		saveGraph.addActionListener(this);
		JPanel opts = new JPanel();
		opts.add(useGroupName, BorderLayout.WEST);
		opts.add(saveTable, BorderLayout.CENTER);
		opts.add(saveGraph, BorderLayout.EAST);
		this.add(opts, BorderLayout.SOUTH);
	}

	private JPanel createChoosePanel() {
		JPanel chooseGraphsPanel = new JPanel();

		chooseGraphsPanel.setLayout(new FlowLayout());
		JLabel selectGraphsLabel = new JLabel(JMeterUtils
				.getResString("graph_choose_curve"));
		average = createChooseCheckBox(JMeterUtils
				.getResString("curve_results_average"), Color.BLUE);
		deviation = createChooseCheckBox(JMeterUtils
				.getResString("curve_results_deviation"), Color.RED);
		throughput = createChooseCheckBox(JMeterUtils
				.getResString("curve_results_throughput"), Color.GREEN);

		chooseGraphsPanel.add(selectGraphsLabel);
		chooseGraphsPanel.add(average);
		chooseGraphsPanel.add(throughput);
		chooseGraphsPanel.add(deviation);
		return chooseGraphsPanel;
	}

	private JCheckBox createChooseCheckBox(String labelResourceName, Color color) {
		JCheckBox checkBox = new JCheckBox(labelResourceName);
		checkBox.setSelected(true);
		checkBox.addItemListener(this);
		checkBox.setForeground(color);
		return checkBox;
	}

	public void modifyTestElement(TestElement c) {
		super.modifyTestElement(c);
		c.setProperty(USE_GROUP_NAME, useGroupName.isSelected(), false);
	}

	public void configure(TestElement el) {
		super.configure(el);
		useGroupName
				.setSelected(el.getPropertyAsBoolean(USE_GROUP_NAME, false));
	}

	public void actionPerformed(ActionEvent ev) {
		if (ev.getSource() == saveTable) {
			JFileChooser chooser = FileDialoger.promptToSaveFile("summary.csv");//$NON-NLS-1$
			if (chooser == null) {
				return;
			}
			FileWriter writer = null;
			try {
				writer = new FileWriter(chooser.getSelectedFile());
				CSVSaveService.saveCSVStats(model, writer);
			} catch (FileNotFoundException e) {
				log.warn(e.getMessage());
			} catch (IOException e) {
				log.warn(e.getMessage());
			} finally {
				JOrphanUtils.closeQuietly(writer);
			}
		} else if (ev.getSource() == saveGraph) {
			JFileChooser chooser = FileDialoger.promptToSaveFile("summary.png");
			if (chooser == null) {
				return;
			}
			m_jfGraph.saveAsFile(chooser.getSelectedFile().getAbsolutePath());
			JOptionPane.showMessageDialog(null, "保存成功", "完成",
					JOptionPane.YES_NO_OPTION);
		} else {
			log.error("Non action perform!");
		}
	}

	public void itemStateChanged(ItemEvent e) {
		if (e.getItem() == average) {
			m_jfGraph.setLineDisplay(JFreeChartGraph.AVERANGE, e
					.getStateChange() == ItemEvent.SELECTED);
		} else if (e.getItem() == deviation) {
			m_jfGraph.setLineDisplay(JFreeChartGraph.DEVIATION, e
					.getStateChange() == ItemEvent.SELECTED);
		} else if (e.getItem() == throughput) {
			m_jfGraph.setLineDisplay(JFreeChartGraph.TPS,
					e.getStateChange() == ItemEvent.SELECTED);
		} else {
			log.error("Non item changed!");
		}
	}
}
