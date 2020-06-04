package jcf.sua.ux.excel.core.workbook;

import jcf.sua.ux.excel.core.worksheet.WorkSheet;

/**
 *
 * @author nolang
 *
 */
public interface WorkBook {
	WorkSheet getWorkSheetObject(int shtIndex);

	void addWorkSheet(WorkSheet worksheet);

	int getLogicalSheetCount();

	String getMetadata(String encoding);

	String getWorkbook();

	String getEndWorkbook();

	String getDocumentProperties();

	String getExcelWorkbook();

	String getStyles();

	String getEndStyles();

	String getStyle();

	String getWorksheet(int shtIndex);

	String getWorksheet(String sheetName);

	String getEndWorksheet();

	String getNames(int shtIndex);

	String getTable(int shtIndex);

	String getEndTable();

	String getColumns(int shtIndex);

	String getWorkSheetOptions(int shtIndex);
}
