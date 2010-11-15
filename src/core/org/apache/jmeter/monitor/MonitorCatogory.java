package org.apache.jmeter.monitor;

import java.util.HashMap;
import java.util.Map;

/**
 * Monitor 配置单元
 * @author chenchao.yecc
 * @version jex003A
 *
 */
public class MonitorCatogory {

	// 分类名字
	private String category = "";

	// 分类对应的Y轴个数
	private int yAxis = 0;
	
	// 对应的Line
	private Map<String, MonitorLine> lines = new HashMap<String, MonitorLine>();
	

	public void setYAxiscount(int ycount) {
		this.yAxis = ycount;
	}
	
	public Map<String, MonitorLine> getLines(){
		return lines;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getCategory() {
		return category;
	}

	// 线对应的Y轴个数
	public int getYAxisCount() {
		return yAxis;
	}

	// 线的显示设置、线和坐标轴的映射关系
	public String getShowType(String lineName) {
		return lines.get(lineName).getShowType();
	}

	// 线的种类
	public String getLineType(String lineName) {
		return lines.get(lineName).getLineType();
	}

	// 线的颜色
	public String getLineColor(String lineName) {
		return lines.get(lineName).getLineColor();
	}

	// 线对应的数据
	public String getDataType(String lineName) {
		return lines.get(lineName).getDataType();
	}
}
