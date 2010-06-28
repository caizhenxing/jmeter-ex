package org.apache.jmeter.gui.util;

import javax.swing.JTable;
import javax.swing.table.TableModel;

/**
 * Custom table
 * 
 * @author chenchao.yecc
 * @since jex001A
 */
@SuppressWarnings("serial")
public class YccCustomTable extends JTable {
	public YccCustomTable(TableModel dm) {
		super(dm);
	}

	public boolean isCellEditable(int row, int column) {
		return false;
	}
}
