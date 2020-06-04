package jcf.sua.ux.excel.core.stylesheet;

import jcf.sua.ux.excel.core.stylesheet.style.Style;

/**
 *
 * @author nolang
 *
 */
public interface StyleSheet {
	double getWidth(int column);

	 double getHeight(int row);

	 String getTableExpandedColumnCount();

	 String getTableExpandedRowCount();

	 String getTableFullColumn();

	 String getTableFullRows();

	 String getTableDefaultColumnWidth();

	 String getTableDefaultRowHeight();

	 String getStyleIdAt(int rowIndex, int colIndex);

	 int getMergeAcross(int rowIndex, int colIndex);

	 int getMergeDown(int rowIndex, int colIndex);

	 String getFormulaAt(int rowIndex, int colIndex);

	 String getTypeAt(int rowIndex, int colIndex);

	 String getNamedCellAt(int rowIndex, int colIndex);

	 Style[] getCustomStyles();
}
