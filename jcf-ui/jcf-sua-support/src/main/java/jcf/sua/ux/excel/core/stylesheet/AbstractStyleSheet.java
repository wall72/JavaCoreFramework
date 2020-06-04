package jcf.sua.ux.excel.core.stylesheet;

import jcf.sua.ux.excel.core.Constants;
import jcf.sua.ux.excel.core.stylesheet.style.Style;

/**
 *
 * @author nolang
 *
 */
public abstract class AbstractStyleSheet implements StyleSheet {

	public abstract Style[] getCustomStyles();

	public abstract String getStyleIdAt(int rowIndex, int colIndex);

	public abstract String getTypeAt(int rowIndex, int colIndex);

	public abstract String getNamedCellAt(int rowIndex, int colIndex);

	public String getFormulaAt(int rowIndex, int colIndex) {
		return null;
	}

	public int getMergeAcross(int rowIndex, int colIndex) {
		return 0;
	}

	public int getMergeDown(int rowIndex, int colIndex) {
		return 0;
	}

	public double getHeight(int row) {
		return Constants.DEFAULT_ROW_HIGHT;
	}

	public double getWidth(int column) {
		return Constants.DEFAULT_COLUMN_WIDTH;
	}

	public String getTableExpandedColumnCount() {
		return String.valueOf(Constants.SHEET_COL_CAPACITY);
	}

	public String getTableExpandedRowCount() {
		return String.valueOf(Constants.SHEET_ROW_CAPACITY);
	}

	public String getTableFullColumn() {
		return "1";
	}

	public String getTableFullRows() {
		return "1";
	}

	public String getTableDefaultColumnWidth() {
		return Double.toString(Constants.DEFAULT_COLUMN_WIDTH);
	}

	public String getTableDefaultRowHeight() {
		return Double.toString(Constants.DEFAULT_ROW_HIGHT);
	}
}
