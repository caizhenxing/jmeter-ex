package org.apache.jmeter.gui;

import java.util.Date;

/**
 * 优化平滑曲线的类
 * 
 * @author chenchao.yecc
 * @since jex003A
 * 
 */
public class DataMergeService {
	private double acceptAngle; // 可以接受的误差度数

	private DataPoint firstPoint = null;
	private DataPoint secondPoint = null;
	private double prevSlope;
	private double preLineValue;

	public DataMergeService(double angle) {
		this.acceptAngle = angle;
	}

	public void setAcceptAngle(double acceptAngle) {
		this.acceptAngle = acceptAngle;
	}

	public double getAcceptAngle() {
		return acceptAngle;
	}

	/**
	 * 增加数据点
	 * 
	 * @param time
	 * @param value
	 */
	public boolean isInsertData(Date time, double value, boolean last) {
		DataPoint point = new DataPoint();
		point.setTime(time.getTime());
		point.setValue(value);
		return addData(point, last);
	}

	/**
	 * 增加数据点
	 * 
	 * @param point
	 */
	private boolean addData(DataPoint point, boolean last) {
		if (null != firstPoint && null != secondPoint) {
			// 计算按照原先斜率得到的第三个点坐标
			DataPoint point3 = new DataPoint();
			point3.setTime(point.getTime());
			double value = preLineValue + prevSlope * point.getTime();
			point3.setValue(value);

			// double slope2 = getSlope(secondPoint, point);

			// double angle = Math.toDegrees(angle(prevSlope, slope2));
			double angle = Math.abs(((point.getValue() - value) / value) * 100);

			if (angle <= acceptAngle) { // 如果误差在可接受范围内
				secondPoint = point3;
				if (last) {
					return false;
				} else {
					return true;
				}
			} else {
				// System.out.println(angle);
				// mergeDataList.add(copyPoint(secondPoint));
				firstPoint = secondPoint;
				secondPoint = point;
				parseValue();
				return true;
			}
		} else {
			if (null == firstPoint) {
				firstPoint = point;
				// mergeDataList.add(point);
				return true;
			} else if (null == secondPoint) {
				secondPoint = point;
				parseValue();
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * 根据2个直线的斜率算出夹角
	 * 
	 * @param slope1
	 * @param slope2
	 * @return
	 */
	// private double angle(double slope1, double slope2) {
	// double tanAngle = Math.abs((slope2 - slope1) / (1 + slope1 * slope2));
	// return Math.atan(tanAngle);
	// }

	private void parseValue() {
		prevSlope = getSlope(firstPoint, secondPoint);
		preLineValue = getLineValue(firstPoint, secondPoint);
	}

	/**
	 * 得到2个点之间的斜率
	 * 
	 * @param point1
	 * @param point2
	 * @return
	 */
	private double getSlope(DataPoint point1, DataPoint point2) {
		return (point2.getValue() - point1.getValue())
				/ (point2.getTime() - point1.getTime());
	}

	/**
	 * 得到2个点表达式的常量
	 * 
	 * @param point1
	 * @param point2
	 * @return
	 */
	private double getLineValue(DataPoint point1, DataPoint point2) {
		double y1 = point1.getValue();
		double y2 = point2.getValue();
		long x1 = point1.getTime();
		long x2 = point2.getTime();
		return (x1 * y2 - x2 * y1) / (x1 - x2);
	}
}
