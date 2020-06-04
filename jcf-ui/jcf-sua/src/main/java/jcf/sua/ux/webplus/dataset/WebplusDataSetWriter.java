package jcf.sua.ux.webplus.dataset;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import jcf.data.RowStatus;
import jcf.sua.dataset.DataSet;
import jcf.sua.dataset.DataSetRow;
import jcf.sua.dataset.DataSetWriter;
import jcf.sua.exception.MciException;
import jcf.sua.mvc.MciDataSetAccessor;

/**
 *
 * {@link DataSetWriter} 의 웹플러스 구현체
 *
 * @author Jeado
 *
 */
public class WebplusDataSetWriter implements DataSetWriter {

	private HttpServletResponse response;
	private MciDataSetAccessor accessor;

	private final String DEFUALT_SUCCESS_MESSAGE = "SUCCESS";
	private final String DEFUALT_SUCCESS_CODE = "0";

	public WebplusDataSetWriter(HttpServletResponse response,
			MciDataSetAccessor accessor) {
		this.response = response;
		this.accessor = accessor;
	}

	/**
	 * {@inheritDoc}
	 */
	public void write() {
		try {
			ServletOutputStream outputStream = response.getOutputStream();
			Map<String, DataSet> dataSetMap = accessor.getDataSetMap();

			StringBuilder stringBuilder = new StringBuilder();

			buildDataSetXML(dataSetMap, stringBuilder);

			byte[] bytes = stringBuilder.toString().getBytes();

			outputStream.write(bytes);
			outputStream.flush();
		} catch (IOException e) {
			throw new MciException("WebplusDataSetWriter : " + e.getMessage());
		}
	}

	private void buildDataSetXML(Map<String, DataSet> dataSetMap, StringBuilder sb) {
		sb.append("<dataset>");
		Set<String> dataSetIds = dataSetMap.keySet();
		for (String string : dataSetIds) {
			DataSet dataSet = dataSetMap.get(string);
			for (int i = 0; i < dataSet.getRowCount(); i++) {
				DataSetRow dataSetRow = dataSet.getDataSetRow(i);
				sb.append("<" + dataSet.getId() + " crud="
						+ getResolvedRowStatus(dataSet.getRowStatus(0)) + ">");
				buildRowInfo(sb, dataSet, dataSetRow);
				sb.append("</" + dataSet.getId() + ">");
			}
		}

		// �먮윭硫붿떆吏�鍮뚮뱶
		// SccuessMessages�먯꽌 泥ル쾲吏�媛믪� 硫붿떆吏�퀬 �먮쾲吏�媛믪� 肄붾뱶�대떎.
		if (accessor.getSuccessMessags().size() < 1) {
			buildBasicXMLNotation(sb, "ErrorMsg",DEFUALT_SUCCESS_MESSAGE);
		}else{
			buildErrorMSG(sb, accessor);
		}

		// �먮윭肄붾뱶 鍮뚮뱶 SccuessMessages�먯꽌 泥ル쾲吏�媛믪� 硫붿떆吏�퀬 �먮쾲吏�媛믪� 肄붾뱶�대떎.
		if (accessor.getSuccessMessags().size() < 2) {
			buildBasicXMLNotation(sb, "ErrorCode",DEFUALT_SUCCESS_CODE);
		}else{
			buildErrorCode(sb, accessor);
		}

		sb.append("</dataset>");
	}

	private String getResolvedRowStatus(RowStatus rowStatus) {
		String resolvedRowStatus = null;
		switch (rowStatus) {
		case INSERT:
			resolvedRowStatus = "C";
			break;
		case DELETE:
			resolvedRowStatus = "D";
			break;
		case UPDATE:
			resolvedRowStatus = "U";
			break;
		case NORMAL:
			resolvedRowStatus = "N";
			break;
		default:
			resolvedRowStatus = "R";
			break;
		}
		return resolvedRowStatus;
	}

	private void buildRowInfo(StringBuilder sb, DataSet dataSet,
			DataSetRow dataSetRow) {
		for (int j = 0; j < dataSet.getColumnCount(); j++) {
			String columnName = dataSet.getDataSetColumn(j).getColumnName();
			String value = String.valueOf(dataSetRow.get(columnName));
			buildBasicXMLNotation(sb, columnName, value);
		}
	}

	/**
	 * @param sb
	 * @param accessor
	 *
	 *            SccuessMessages�먯꽌 泥ル쾲吏�媛믪� 硫붿떆吏�퀬 �먮쾲吏�媛믪� 肄붾뱶�대떎.
	 */
	private void buildErrorMSG(StringBuilder sb, MciDataSetAccessor accessor) {
		buildBasicXMLNotation(sb, "ErrorMsg",
				accessor.getSuccessMessags().get(0));
	}

	/**
	 * @param sb
	 * @param accessor
	 *
	 *            SccuessMessages�먯꽌 泥ル쾲吏�媛믪� 硫붿떆吏�퀬 �먮쾲吏�媛믪� 肄붾뱶�대떎.
	 */
	private void buildErrorCode(StringBuilder sb, MciDataSetAccessor accessor) {
		buildBasicXMLNotation(sb, "ErrorCode", accessor.getSuccessMessags()
				.get(1));
	}

	/**
	 * @param StringBuilder
	 * @param tagName
	 * @param valueInsideOfTag
	 *
	 *            �ㅼ쓬怨�媛숈� �뺤떇�쇰줈 �섑��쒕떎.
	 *
	 *            <tagName>valueInsideOfTag</tagName>
	 */
	private void buildBasicXMLNotation(StringBuilder sb, String tagName,
			String valueInsideOfTag) {
		sb.append("<" + tagName + ">");
		sb.append(valueInsideOfTag);
		sb.append("</" + tagName + ">");
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDataSetAccessor(MciDataSetAccessor accessor) {
		this.accessor = accessor;
	}
}
