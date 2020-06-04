package jcf.sua.ux.excel.core.workbook;

import java.util.ArrayList;
import java.util.List;

import jcf.sua.ux.excel.core.Constants;
import jcf.sua.ux.excel.core.stylesheet.StyleSheet;
import jcf.sua.ux.excel.core.worksheet.WorkSheet;

/**
 *
 * @author nolang
 *
 */
public abstract class AbstractWorkBook implements WorkBook {
	protected List<WorkSheet> worksheets = new ArrayList<WorkSheet>();

	public abstract String getWorkbook();

	public abstract String getDocumentProperties();

	public abstract String getExcelWorkbook();

	public abstract String getStyle();

	public abstract String getWorkSheetOptions(int shtIndex);

	public final String getMetadata(String encoding) {
		StringBuffer buf = new StringBuffer();

		buf.append("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n");
//		buf.append("<?xml version=\"1.0\"?>\n");
		buf.append("<?mso-application progid=\"Excel.Sheet\"?>\n");

		return buf.toString();
	}

	public final String getEndWorkbook() {
		return "</Workbook>\n";
	}

	public final String getStyles() {
		return "<Styles>\n";
	}

	public final String getEndStyles() {
		return "</Styles>\n";
	}

	public String getWorksheet(int shtIndex) {
		return "<Worksheet ss:Name=\"Sheet" + (shtIndex + 1) + "\">\n";
	}

	public final String getWorksheet(String sheetName) {
		return "<Worksheet ss:Name=\"" + sheetName + "\">\n";
	}

	public final String getEndWorksheet() {
		return "</Worksheet>\n";
	}

	public String getNames(int shtIndex) {
		return "";
	}

	public String getTable(int shtIndex) {
		StringBuffer buf = new StringBuffer();

		buf.append("<Table ss:ExpandedColumnCount=\"");
		buf.append(Constants.SHEET_COL_CAPACITY);
		buf.append("\" ss:ExpandedRowCount=\"");
		buf.append(Constants.SHEET_ROW_CAPACITY);
		buf.append("\" x:FullColumns=\"1\" ");
		buf.append("x:FullRows=\"1\" ss:DefaultColumnWidth=\"");
		buf.append(Constants.DEFAULT_COLUMN_WIDTH);
		buf.append("\" ss:DefaultRowHeight=\"");
		buf.append(Constants.DEFAULT_ROW_HIGHT);
		buf.append("\">\n");

		return buf.toString();
	}

	public final String getEndTable() {
		return "</Table>";
	}

	public String getColumns(int shtIndex) {
		StyleSheet styleSheet = (StyleSheet) worksheets.get(shtIndex);

		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < worksheets.get(shtIndex).getColumnCount(); ++i) {
			buf.append("<Column ss:AutoFitWidth=\"0\" ss:Width=\"" + styleSheet.getWidth(i) + "\"/>\n");
		}

		return buf.toString();
	}

	public WorkSheet getWorkSheetObject(int shtIndex) {
		return worksheets.get(shtIndex);
	}

	public void addWorkSheet(WorkSheet worksheet) {
		worksheets.add(worksheet);
	}

	public final int getLogicalSheetCount() {
		return worksheets.size();
	}
}
