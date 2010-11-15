package org.apache.jmeter.monitor;

/**
 * Monitor 配置单元
 * @author chenchao.yecc
 * @version jex002A
 *
 */
public class MonitorLine {

	// 名字
	private String name = "";

	// 线的显示设置、线和坐标轴的映射关系
	private String showType = "-";

	// 线的种类(保留字段)
	private String lineType = "";

	// 线的颜色
	private String lineColor = "";

	// 线对应的数据
	private String dataType = "";
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShowType() {
		return showType;
	}

	public void setShowType(String showType) {
		this.showType = showType;
	}

	public String getLineType() {
		return lineType;
	}

	public void setLineType(String lineType) {
		this.lineType = lineType;
	}

	public String getLineColor() {
		return lineColor;
	}

	public void setLineColor(String lineColor) {
		this.lineColor = lineColor;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
}
