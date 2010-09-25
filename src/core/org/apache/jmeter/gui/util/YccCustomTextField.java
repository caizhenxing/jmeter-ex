package org.apache.jmeter.gui.util;

import java.awt.Toolkit;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * 只允许输入数字,同时可以设置最大最小值的JTextField
 * 
 * @author chenchao.yecc
 * @since jex003A
 */
public class YccCustomTextField extends JTextField {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -2732387087480277531L;

	/**
	 * 最大值
	 */
	private int maxValue = Integer.MAX_VALUE;

	/**
	 * 最小值
	 */
	private int minValue = Integer.MIN_VALUE;

	/**
	 * 宽度
	 */
	private int maxLength = 0;

	/**
	 * 构造方法 最大宽度
	 * 
	 * @param maxLength
	 */
	public YccCustomTextField(int maxLength) {
		this(maxLength, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	/**
	 * 构造方法
	 * 
	 * @param minValue
	 *            最小值
	 * @param maxValue
	 *            最大值
	 */
	public YccCustomTextField(int minValue, int maxValue) {
		this(0, minValue, maxValue);
	}

	/**
	 * 构造方法
	 * 
	 * @param maxLength
	 *            最大宽度
	 * @param minValue
	 *            最小值
	 * @param maxValue
	 *            最大值
	 */
	public YccCustomTextField(int maxLength, int minValue, int maxValue) {
		super();
		this.maxLength = maxLength;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	/**
	 * 设置最大值
	 * 
	 * @param maxValue
	 *            最大值
	 */
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	/**
	 * 获取最大值
	 * 
	 * @return 最大值
	 */
	protected int getMaxValue() {
		return this.maxValue;
	}

	/**
	 * 设置最小值
	 * 
	 * @param minValue
	 *            最小值
	 */
	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}

	/**
	 * 获取最小值
	 * 
	 * @return 最小值
	 */
	protected int getMinValue() {
		return this.minValue;
	}

	/**
	 * 获取当前值
	 * 
	 * @return 当前值
	 */
	public int getValue() {
		int retVal = 0;

		try {
			retVal = Integer.parseInt(getText());

			if (retVal > maxValue) {
				retVal = maxValue;
			} else if (retVal < minValue) {
				retVal = minValue;
			}
		} catch (NumberFormatException e) {
			retVal = 0;
		}

		return (new Integer(retVal)).intValue();
	}

	/**
	 * 设置当前值
	 * 
	 * @param value
	 *            当前值
	 */
	public void setValue(Object value) {
		if (value == null || value.equals("")) {
			setText("0");
		} else {
			setText(value.toString());
		}
	}

	/**
	 * 创建缺省模板
	 */
	protected Document createDefaultModel() {
		return new NumberDocument(this);
	}

	/**
	 * 文本控制类
	 * 
	 * @author 003
	 */
	protected class NumberDocument extends PlainDocument {
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = -5643002098563548951L;

		/**
		 * NumberField对象
		 */
		YccCustomTextField field;

		/**
		 * 构造方法
		 * 
		 * @param field
		 *            NumberField对象
		 */
		public NumberDocument(YccCustomTextField field) {
			this.field = field;
		}

		/**
		 * 插入规则
		 */
		public void insertString(int offs, String str, AttributeSet a)
				throws BadLocationException {
			if (str == null || str.equals("")) {
				return;
			}

			char[] source = str.toCharArray();
			char[] result = new char[source.length];

			int j = 0;

			for (int i = 0; i < result.length; i++) {
				if (Character.isDigit(source[i])) {
					result[j++] = source[i];
				} else {
					Toolkit.getDefaultToolkit().beep();
				}
			}

			StringBuffer sb = new StringBuffer();
			sb.append(field.getText());
			sb.insert(offs, new String(result, 0, j));

			try {
				int value = Integer.parseInt(sb.toString());

				boolean flag1 = maxLength > 0 && this.getLength() < maxLength
						&& value <= field.getMaxValue()
						&& value >= field.getMinValue();
				boolean flag2 = maxLength == 0 && value <= field.getMaxValue()
						&& value >= field.getMinValue();

				if (flag1 || flag2) {
					super.insertString(offs, new String(result, 0, j), a);
				}
			} catch (NumberFormatException ex) {
			}
		}
	}
}