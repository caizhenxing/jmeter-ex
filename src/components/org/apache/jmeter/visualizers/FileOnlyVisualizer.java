package org.apache.jmeter.visualizers;

import java.awt.BorderLayout;

import org.apache.jmeter.reporters.CustomResultCollector;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.visualizers.gui.AbstractVisualizer;

/**
 * A visualizer to saving sample result as line data;
 * 
 * @author chenchao.yecc
 * @since jex002A
 */
public class FileOnlyVisualizer extends AbstractVisualizer {

	private static final long serialVersionUID = 1L;

	public FileOnlyVisualizer(){
		super();
		this.setLayout(new BorderLayout());
		getSaveConfigButton().setVisible(false);
		this.add(super.makeTitlePanel(),BorderLayout.CENTER);
	    setModel(new CustomResultCollector());
	}

	public void clearData() {
		
	}

	public String getLabelResource() {
		return "file_only_report";
	}

	public void add(SampleResult sample) {
		
	}

}
