package jcf.sua.ux.excel.dataset;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import jcf.sua.dataset.DataSetStreamWriter;
import jcf.sua.exception.MciException;
import jcf.sua.ux.excel.core.Constants;
import jcf.sua.ux.excel.core.stylesheet.style.DefaultStyle;
import jcf.sua.ux.excel.core.workbook.DefaultWorkBook;
import jcf.sua.ux.excel.core.workbook.WorkBook;
import jcf.sua.ux.excel.core.worksheet.AbstractGridSheet;

import org.springframework.util.ReflectionUtils;

/**
 *
 * @author nolang
 *
 */
@SuppressWarnings("unchecked")
public class ExcelRadStreamWriter implements DataSetStreamWriter {

	private HttpServletResponse response;
	private BufferedWriter out;
//	private BufferedOutputStream out;

	private WorkBook workbook;
	private int sheetNum = 1;
	private int rowNum = 0;

	public ExcelRadStreamWriter(HttpServletResponse response) {
		this.response = response;
	}

	public void startStream(String dataSetId, int bufferSize) {
		response.setContentType("application/octet");
		response.setHeader("Content-Disposition", "attachment;filename=" + dataSetId + ".xls");

		try {
			out = new BufferedWriter(response.getWriter(), bufferSize);
//			out = new BufferedOutputStream(response.getOutputStream(), bufferSize);

			workbook = new DefaultWorkBook(new DummyWorkSheet());

			/*
			 * 테스트 해보셈. 혹시 이 소스들은 빌드하면 클래스쪽으로 가는건가요? 당연히 가죠. 그런데 문제는
			 * 제이씨에프 라이브러리 내에 있는 클래스명들과 겹쳐서 혹시 와스 설정에 따라서 라이브러리 것이 올라갈 수도 있다는 점이 좀 찜찜하고요.
			 * 일단은 그냥 프레임워크 버그라고 보시면 됩니다.
			 * 그러면 현재는 그대로 클래스만 새로 빌드해서 반영해주면 되는거군요?
			 * 단기적으로는 그렇고요.
			 * 일단 여기 있는 소스들을 좀 가져가야할 것 같네요. 제이씨에프 패키지 밑에 있는 소스들요.
			 * 저희쪽 소스에 반영해서 정식으로 빌드해서 업데이트해드리는게 낫겠습니다.
			 * 그러면 src 밑에 있는거 압축해서 보내드리면 될까요?너무 많고요 제이씨에프 쩜 에스유에이 쩜 유엑스 밑에것만 필요합니다.
			 * 아 넵. 옆에 저 패키지 말씀하시는거죠?네 그렇죠.
			 * 알겠
			 * 습니다 바로 보내드리겠습니다. 감사합니다. 고생하셨습니다.반영된 거 확인을 못해서 많이 아쉽네요 --
			 * 일단 서버쪽은 이따 제가 다시 빌드해서 올리고 확인해 보겠습니다.수고하셨습니다
			 * 감사합니다!
			 */
			
			out.write(workbook.getMetadata(response.getCharacterEncoding()));
			out.write(workbook.getWorkbook());
			out.write(workbook.getDocumentProperties());
			out.write(workbook.getExcelWorkbook());
			out.write(workbook.getStyles());
			out.write(workbook.getStyle());
			out.write(workbook.getEndStyles());

		} catch (IOException e) {
			throw new MciException(e);
		}

	}

	public void addStreamData(Object data) {
		if(rowNum == Constants.SHEET_ROW_CAPACITY)	{
			rowNum = 0;
		}

		try {
			if (rowNum++ == 0) {
				out.write(workbook.getWorksheet("Sheet" + sheetNum++));
				out.write(workbook.getNames(0));
				out.write(workbook.getTable(0));
				out.write(workbook.getColumns(0));
				out.write(getTitle(data));

				rowNum += 1;
			}

			out.write(getRowData(data));


			if(rowNum == Constants.SHEET_ROW_CAPACITY)	{
				out.write(workbook.getEndTable());
				out.write(workbook.getWorkSheetOptions(0));
				out.write(workbook.getEndWorksheet());
			}
		} catch (Exception e) {
			throw new MciException(e);
		}
	}

	public void endStream() {
		try {
			if (rowNum == 0) {
				out.write(workbook.getWorksheet("Sheet" + sheetNum++));
				out.write(workbook.getNames(0));
				out.write(workbook.getTable(0));
				out.write(workbook.getColumns(0));
			}

			if(rowNum != Constants.SHEET_ROW_CAPACITY)	{
				out.write(workbook.getEndTable());
				out.write(workbook.getWorkSheetOptions(0));
				out.write(workbook.getEndWorksheet());
			}

			out.write(workbook.getEndWorkbook());

			out.flush();
			out.close();
		} catch (Exception e) {
			throw new MciException(e);
		}
	}

	private String getTitle(Object rowData) {
		StringBuilder buf = new StringBuilder();

		buf.append("<Row ss:AutoFitHeight=\"0\" ss:Height=\"13.5\">\n");

		if(Map.class.isAssignableFrom(rowData.getClass()))	{
			for (Map.Entry<String, Object> entry : ((Map<String, Object>)rowData).entrySet()) {
				buf.append("<Cell ss:StyleID=\"");
				buf.append(DefaultStyle.STYLE_HEADER);
				buf.append("\"><Data ss:Type=\"String\">");
				buf.append(entry.getKey());
				buf.append("</Data></Cell>\n");
			}
		} else {
			Field[] fields = rowData.getClass().getDeclaredFields();

			for (Field f : fields) {
				buf.append("<Cell ss:StyleID=\"");
				buf.append(DefaultStyle.STYLE_HEADER);
				buf.append("\"><Data ss:Type=\"String\">");
				buf.append(f.getName());
				buf.append("</Data></Cell>\n");
			}
		}

		buf.append("</Row>\n");

		return buf.toString();
	}

	private String getRowData(Object rowData) {
		StringBuffer buf = new StringBuffer();

		buf.append("<Row");
		buf.append(" ss:AutoFitHeight=\"0\" ss:Height=\"");
		buf.append(Constants.DEFAULT_ROW_HIGHT).append("\">\n");

		if(Map.class.isAssignableFrom(rowData.getClass()))	{
			for (Map.Entry<String, Object> entry : ((Map<String, Object>)rowData).entrySet()) {
				buf.append("<Cell");
				buf.append(" ss:StyleID=\"");
				buf.append(DefaultStyle.STYLE_STRING_LEFT).append("\">");
				buf.append("<Data");
				buf.append(" ss:Type=\"").append(DefaultStyle.TYPE_STRING).append("\">");
				buf.append("<![CDATA[");
				buf.append(entry.getValue() == null ? "" : entry.getValue());
				buf.append("]]></Data>");
				buf.append("</Cell>\n");
			}
		} else {
			Field[] fields = rowData.getClass().getDeclaredFields();

			for (Field f : fields) {
				f.setAccessible(true);

				buf.append("<Cell");
				buf.append(" ss:StyleID=\"");
				buf.append(DefaultStyle.STYLE_STRING_LEFT).append("\">");
				buf.append("<Data");
				buf.append(" ss:Type=\"").append(DefaultStyle.TYPE_STRING).append("\">");
				buf.append("<![CDATA[");
				buf.append(ReflectionUtils.getField(f, rowData) == null ? "" : ReflectionUtils.getField(f, rowData));
				buf.append("]]></Data>");
				buf.append("</Cell>\n");
			}
		}

		buf.append("</Row>\n");

		return buf.toString();
	}

	static class DummyWorkSheet extends AbstractGridSheet {

		@Override
		public Object getValueAt(int rowIndex, int colIndex) {
			return null;
		}

		@Override
		public long getRowCount() {
			return Constants.SHEET_ROW_CAPACITY;
		}

		@Override
		public int getColumnCount() {
			return Constants.SHEET_COL_CAPACITY;
		}

		@Override
		public String getTitle(int column) {
			return null;
		}

	}
}
