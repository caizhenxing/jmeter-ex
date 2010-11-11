package org.apache.jmeter.gui.util;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
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
        //		this.setColumnSelectionAllowed(false);
        this.setRowSelectionAllowed(true);
        super.tableHeader.setReorderingAllowed(false);
        addColumnResizeListener();
        this.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {
            }

            public void mouseMoved(MouseEvent e) {

                Point point = e.getPoint();

                int x = YccCustomTable.this.rowAtPoint(point);

                int y = YccCustomTable.this.columnAtPoint(point);

                Object tip = YccCustomTable.this.getValueAt(x, y);

                if (tip == null || (tip instanceof String && tip.equals(""))) {
                    YccCustomTable.this.setToolTipText(null);
                } else {
                    YccCustomTable.this.setToolTipText(YccCustomTable
                            .getToolTipText(tip.toString()));
                }
            }
        });
    }

    public static String getToolTipText(String str) {
        int max = 50;
        StringBuilder sb = new StringBuilder("<html>");
        //        <html>hello<br>hello</html>
        if (str.length() <= 100) {
            sb.append(str).append("</html>");
        } else {
            String tmp = null;
            int len = str.length();
            int cusor = 0;
            while (len - cusor >= max) {
                tmp = str.substring(cusor, cusor + max);
                sb.append(tmp).append("<br>");
                cusor = cusor + max;
            }
            tmp = str.substring(cusor, len);
            sb.append(tmp).append("</html>");
        }
        return sb.toString();
    }

    public boolean isCellEditable(int row, int column) {
        return false;
    }

    /**
     * 根据给定的列号调整表格的列宽，此算法取自Swing hacker
     * 
     * @param table
     * @param col
     */
    public static void adjustColumnPreferredWidths(JTable table, int col) {
        // strategy - get max width for cells in column and   
        // make that the preferred width   
        TableColumnModel columnModel = table.getColumnModel();
        int maxwidth = 0;
        for (int row = 0; row < table.getRowCount(); row++) {
            TableCellRenderer rend = table.getCellRenderer(row, col);
            Object value = table.getValueAt(row, col);
            Component comp = rend.getTableCellRendererComponent(table, value, false, false, row,
                    col);
            maxwidth = Math.max(comp.getPreferredSize().width, maxwidth);
        } // for row   
        TableColumn column = columnModel.getColumn(col);
        column.setPreferredWidth(maxwidth + 3);
    }

    private void addColumnResizeListener() {
        tableHeader = this.getTableHeader();
        tableHeader.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                // 当光标处于两个列表间的分隔线上时，表头的光标呈东西调整的样式，通过
                // 鼠标的样式和点击次数来判断是否需要进行列宽调整
                int cursorType = tableHeader.getCursor().getType();
                if (cursorType == Cursor.E_RESIZE_CURSOR || cursorType == Cursor.W_RESIZE_CURSOR) {
                    if (e.getClickCount() == 2) {
                        // 获取光标点击位置的列号，这里将X的坐标减去3个像素,是为了保证取到的点始终是分隔线前的列号 
                        int col = YccCustomTable.this.getTableHeader().getColumnModel()
                                .getColumnIndexAtX(e.getX() - 3);
                        adjustColumnPreferredWidths(YccCustomTable.this, col);
                    }
                }
            }
        });
    }
}
