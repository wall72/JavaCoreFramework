package jcf.sua.ux.excel.core.workbook;

import java.util.HashMap;
import java.util.Map;

import jcf.sua.ux.excel.core.Constants;
import jcf.sua.ux.excel.core.stylesheet.StyleSheet;
import jcf.sua.ux.excel.core.stylesheet.style.DefaultStyle;
import jcf.sua.ux.excel.core.stylesheet.style.Style;
import jcf.sua.ux.excel.core.worksheet.AbstractGridSheet;
import jcf.sua.ux.excel.core.worksheet.WorkSheet;

/**
 *
 * @author nolang
 *
 */
public class DefaultWorkBook extends AbstractWorkBook {

	public DefaultWorkBook(WorkSheet... worksheets) {
		for (int i = 0; i < worksheets.length; ++i) {
			addWorkSheet(worksheets[i]);
		}
	}

	@Override
	public String getWorkbook() {
		StringBuffer buf = new StringBuffer();

		buf.append("<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"\n");
		buf.append(" xmlns:o=\"urn:schemas-microsoft-com:office:office\"\n");
		buf.append(" xmlns:x=\"urn:schemas-microsoft-com:office:excel\"\n");
		buf.append(" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\"\n");
		buf.append(" xmlns:html=\"http://www.w3.org/TR/REC-html40\">\n");

		return buf.toString();
	}

	@Override
	public String getDocumentProperties() {
		StringBuffer buf = new StringBuffer();

		buf.append("<DocumentProperties xmlns=\"urn:schemas-microsoft-com:office:office\">\n");
		buf.append("<LastAuthor>ExcelManager.java</LastAuthor>\n");
		buf.append("<LastPrinted>2010-05-06T01:39:36Z</LastPrinted>\n");
		buf.append("<Created>2005-06-22T05:49:24Z</Created>\n");
		buf.append("<Version>11.5606</Version>\n");
		buf.append("</DocumentProperties>\n");

		return buf.toString();
	}

	@Override
	public String getExcelWorkbook() {
		StringBuffer buf = new StringBuffer();

		buf.append("<ExcelWorkbook xmlns=\"urn:schemas-microsoft-com:office:excel\">\n");
		buf.append("<WindowHeight>9450</WindowHeight>\n");
		buf.append("<WindowWidth>14145</WindowWidth>\n");
		buf.append("<WindowTopX>600</WindowTopX>\n");
		buf.append("<WindowTopY>15</WindowTopY>\n");
		buf.append("<ProtectStructure>False</ProtectStructure>\n");
		buf.append("<ProtectWindows>False</ProtectWindows>\n");
		buf.append("</ExcelWorkbook>\n");

		return buf.toString();
	}

	@Override
	public String getStyle() {
		Map<String, String> styleMap = new HashMap<String, String>();
		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < worksheets.size(); ++i) {
			Style[] styles = ((StyleSheet) worksheets.get(i)).getCustomStyles();

			for (int j = 0; j < styles.length; ++j) {
				if (!styleMap.containsKey(styles[j].getClass().getName())) {
					styleMap.put(styles[j].getClass().getName(), "");

					buf.append(styles[j].getStyle());
				}
			}
		}

		if (buf.toString().length() == 0) {
			buf.append((new DefaultStyle()).getStyle());
		}

		return buf.toString();
	}

	@Override
	public String getTable(int shtIndex) {
		StringBuffer buf = new StringBuffer();

		buf.append("<Table ss:ExpandedColumnCount=\"");
		buf.append(worksheets.get(shtIndex).getColumnCount());
		buf.append("\" ss:ExpandedRowCount=\"");

		long rowCount = worksheets.get(shtIndex).getRowCount();

		if (worksheets.get(shtIndex) instanceof AbstractGridSheet) {
			rowCount++;
		}

		if (worksheets.get(shtIndex).getCaption() != null && !worksheets.get(shtIndex).getCaption().equals("")) {
			rowCount += 2;
		}

		buf.append(rowCount);
		buf.append("\" x:FullColumns=\"1\" ");
		buf.append("x:FullRows=\"1\" ss:DefaultColumnWidth=\"");
		buf.append(Constants.DEFAULT_COLUMN_WIDTH);
		buf.append("\" ss:DefaultRowHeight=\"");
		buf.append(Constants.DEFAULT_ROW_HIGHT);
		buf.append("\">\n");

		return buf.toString();
	}

	@Override
	public String getWorkSheetOptions(int shtIndex) {
		StringBuffer buf = new StringBuffer();

		buf.append("<WorksheetOptions xmlns=\"urn:schemas-microsoft-com:office:excel\">\n");
		buf.append("<PageSetup>\n");
		buf.append("<Layout x:Orientation=\"Landscape\"/>\n");
		buf.append("<Header x:Margin=\"0.27559055118110237\"/>\n");
		buf.append("<Footer x:Margin=\"0.27559099999999999\"/>\n");
		buf.append("<PageMargins x:Bottom=\"0.31496062992125984\" x:Left=\"0.35433070866141736\"\n");
		buf.append("x:Right=\"0.23622047244094491\" x:Top=\"0.43307086614173229\"/>\n");
		buf.append("</PageSetup>\n");
		buf.append("<Unsynced/>\n");
		buf.append("<Print>\n");
		buf.append("<ValidPrinterInfo/>\n");
		buf.append("<PaperSizeIndex>9</PaperSizeIndex>\n");
		buf.append("<Scale>77</Scale>\n");
		buf.append("<HorizontalResolution>600</HorizontalResolution>\n");
		buf.append("<VerticalResolution>600</VerticalResolution>\n");
		buf.append("</Print>\n");
		buf.append("<Selected/>\n");
		buf.append("<Panes>\n");
		buf.append("<Pane>\n");
		buf.append("<Number>3</Number>\n");
		buf.append("<ActiveRow>0</ActiveRow>\n");
		buf.append("<ActiveCol>0</ActiveCol>\n");
		buf.append("</Pane>\n");
		buf.append("</Panes>\n");
		buf.append("<Zoom>90</Zoom>\n");
		buf.append("<Selected/>\n");
		buf.append("<ProtectObjects>False</ProtectObjects>\n");
		buf.append("<ProtectScenarios>False</ProtectScenarios>\n");
		buf.append("</WorksheetOptions>\n");

		return buf.toString();
	}
}
