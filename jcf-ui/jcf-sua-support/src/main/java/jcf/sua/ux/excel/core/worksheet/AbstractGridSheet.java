package jcf.sua.ux.excel.core.worksheet;

import jcf.sua.ux.excel.core.stylesheet.DefaultStyleSheet;
import jcf.sua.ux.excel.core.stylesheet.StyleSheet;
import jcf.sua.ux.excel.core.stylesheet.style.Style;


/**
 *
 * @author nolang
 *
 */
public abstract class AbstractGridSheet implements WorkSheet, StyleSheet {
	protected StyleSheet styleSheet = new DefaultStyleSheet();

	public abstract Object getValueAt(int rowIndex, int colIndex);

	public abstract long getRowCount();

	public abstract int getColumnCount();

	public abstract String getTitle(int column);

	public String getCaption() {
		return "";
	}

	public String getSheetName() {
		return "";
	}

	public double getWidth(int column) {
		return styleSheet.getWidth(column);
	}

	public double getHeight(int row) {
		return styleSheet.getHeight(row);
	}

	public String getTableExpandedColumnCount() {
		return styleSheet.getTableExpandedColumnCount();
	}

	public String getTableExpandedRowCount() {
		return styleSheet.getTableExpandedRowCount();
	}

	public String getTableFullColumn() {
		return styleSheet.getTableFullColumn();
	}

	public String getTableFullRows() {
		return styleSheet.getTableFullRows();
	}

	public String getTableDefaultColumnWidth() {
		return styleSheet.getTableDefaultColumnWidth();
	}

	public String getTableDefaultRowHeight() {
		return styleSheet.getTableDefaultRowHeight();
	}

	public String getStyleIdAt(int rowIndex, int colIndex) {
		return styleSheet.getStyleIdAt(rowIndex, colIndex);
	}

	public int getMergeAcross(int rowIndex, int colIndex) {
		return styleSheet.getMergeAcross(rowIndex, colIndex);
	}

	public int getMergeDown(int rowIndex, int colIndex) {
		return styleSheet.getMergeDown(rowIndex, colIndex);
	}

	public String getFormulaAt(int rowIndex, int colIndex) {
		return styleSheet.getFormulaAt(rowIndex, colIndex);
	}

	public String getTypeAt(int rowIndex, int colIndex) {
		return styleSheet.getTypeAt(rowIndex, colIndex);
	}

	public String getNamedCellAt(int rowIndex, int colIndex) {
		return styleSheet.getNamedCellAt(rowIndex, colIndex);
	}

	public Style[] getCustomStyles() {
		return styleSheet.getCustomStyles();
	}

	public void setStyleSheet(StyleSheet styleSheet) {
		this.styleSheet = styleSheet;
	}
}
