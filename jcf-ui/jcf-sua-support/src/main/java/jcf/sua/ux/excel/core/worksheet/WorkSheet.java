package jcf.sua.ux.excel.core.worksheet;

import jcf.sua.ux.excel.core.stylesheet.StyleSheet;


/**
 *
 * @author nolang
 *
 */
public interface WorkSheet {
	public Object getValueAt(int rowIndex, int colIndex);

	public long getRowCount();

	public int getColumnCount();

	public String getCaption();

	public String getTitle(int column);

	public String getSheetName();

	public void setStyleSheet(StyleSheet stylesheet);

}