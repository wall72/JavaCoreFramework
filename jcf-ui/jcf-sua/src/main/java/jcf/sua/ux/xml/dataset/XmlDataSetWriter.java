package jcf.sua.ux.xml.dataset;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import jcf.sua.dataset.DataSet;
import jcf.sua.dataset.DataSetRow;
import jcf.sua.dataset.DataSetWriter;
import jcf.sua.exception.MciException;
import jcf.sua.mvc.MciDataSetAccessor;
import jcf.sua.ux.xml.NodeConstants;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 *
 * {@link DataSetWriter} 의 XML 구현체
 *
 * @author nolang
 *
 */
public class XmlDataSetWriter implements DataSetWriter {

	private HttpServletResponse response;
	private MciDataSetAccessor accessor;

	private String contentType = "application/xml";

	public XmlDataSetWriter(HttpServletResponse response, MciDataSetAccessor accessor) {
		this.response = response;
		this.accessor = accessor;
	}

	/**
	 * {@inheritDoc}
	 */
	public void write() {
		String responseXml = buildXmlString(accessor.getParams(), accessor.getDataSetMap(), accessor.getSuccessMessags(), accessor.getExceptionMessage());

		response.setContentType(contentType);
		response.setCharacterEncoding("utf-8");

		ServletOutputStream out = null;

		try {
			out = response.getOutputStream();
			out.write(responseXml.getBytes());
		} catch (IOException e) {
			throw new MciException(e);
		} finally	{
			if(out != null){
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * @param params
	 * @param dataSetMap
	 * @return
	 */
	private String buildXmlString(Map<String, String> params, Map<String, DataSet> dataSetMap, List<String> successMessages, String exceptionMessage) {
		StringBuilder xmlBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");

		xmlBuilder.append("<").append(NodeConstants.rootNode).append(">");
		xmlBuilder.append("<").append(NodeConstants.headerNode).append(">");

		if(!params.isEmpty())	{
			for (Map.Entry<String, String> e : params.entrySet()) {
				xmlBuilder.append("<").append(e.getKey()).append(">");
				xmlBuilder.append(e.getValue());
				xmlBuilder.append("</").append(e.getKey()).append(">");
			}
		}

		/*
		 * builds xml string for status code.
		 */
		xmlBuilder.append(buildXmlForStatus(successMessages, exceptionMessage));

		xmlBuilder.append("</").append(NodeConstants.headerNode).append(">");
		xmlBuilder.append("<").append(NodeConstants.contentNode).append(">");

		if (!dataSetMap.isEmpty()) {
			for (Map.Entry<String, DataSet> e : dataSetMap.entrySet()) {
				xmlBuilder.append("<").append(e.getKey()).append(">");
				xmlBuilder.append("<").append(NodeConstants.dataNode).append(">");

				int rowCount = e.getValue().getRowCount();
				int colCount = e.getValue().getColumnCount();

				for (int i = 0; i < rowCount; ++i) {
					xmlBuilder.append("<").append(NodeConstants.recordNode).append(">");

					DataSetRow row = e.getValue().getDataSetRow(i);

					for (int j = 0; j < colCount; ++j) {
						String columnName = e.getValue().getDataSetColumn(j).getColumnName();
						Object columnValue = row.get(columnName);

						xmlBuilder.append(buildXmlForRecordItem(columnName, columnValue));
					}

					xmlBuilder.append("</").append(NodeConstants.recordNode).append(">");
				}

				xmlBuilder.append("</").append(NodeConstants.dataNode).append(">");
				xmlBuilder.append("</").append(e.getKey()).append(">");
			}
		}

		xmlBuilder.append("</").append(NodeConstants.contentNode).append(">");
		xmlBuilder.append("</").append(NodeConstants.rootNode).append(">");

		return xmlBuilder.toString();
	}

	/**
	 * @param successMessages
	 * @param exceptionMessage
	 * @return
	 */
	private String buildXmlForStatus(List<String> successMessages, String exceptionMessage) {
		StringBuilder statusXml = new StringBuilder();

		if(StringUtils.hasText(exceptionMessage))	{
			statusXml.append("<").append(NodeConstants.codeNode).append(">");
			statusXml.append(NodeConstants.failure);
			statusXml.append("</").append(NodeConstants.codeNode).append(">");
			statusXml.append("<").append(NodeConstants.messageNode).append(">");
			statusXml.append(StringEscapeUtils.escapeXml(exceptionMessage));
			statusXml.append("</").append(NodeConstants.messageNode).append(">");
		} else {
			statusXml.append("<").append(NodeConstants.codeNode).append(">");
			statusXml.append(NodeConstants.success);
			statusXml.append("</").append(NodeConstants.codeNode).append(">");

			StringBuilder succ = new StringBuilder();

			if(successMessages != null && successMessages.size() > 0)	{
				for(String success : successMessages)	{
					if(StringUtils.hasText(succ.toString()))	{
						succ.append("\n");
					}

					succ.append(success);
				}
			}

			statusXml.append("<").append(NodeConstants.messageNode).append(">");
			statusXml.append(StringEscapeUtils.escapeXml(succ.toString()));
			statusXml.append("</").append(NodeConstants.messageNode).append(">");
		}

		return statusXml.toString();
	}

	/**
	 * @param columnName
	 * @param columnValue
	 * @return
	 */
	private String buildXmlForRecordItem(String columnName, Object columnValue) {

		StringBuilder innerXml = new StringBuilder();

		if (StringUtils.hasText(columnName)) {
			innerXml.append("<").append(columnName).append(">");
		}

		if(columnValue instanceof String)	{
			innerXml.append(StringEscapeUtils.escapeXml((String) columnValue));
		} else {
			if (columnValue != null) {
				if(Map.class.isAssignableFrom(columnValue.getClass()))	{
					for (Map.Entry<String, ?> e : ((Map<String, ?>) columnValue).entrySet()) {
						innerXml.append(buildXmlForRecordItem(e.getKey(), e.getValue()));
					}
				} else if(Collection.class.isAssignableFrom(columnValue.getClass()))	{
					Iterator<?> it = ((Collection<?>) columnValue).iterator();

					while(it.hasNext())	{
						innerXml.append(buildXmlForRecordItem("listItem", it.next()));
					}
				} else {
					if(ClassUtils.isPrimitiveOrWrapper(columnValue.getClass()))	{
						innerXml.append(columnValue);
					} else {
						Field[] fields = columnValue.getClass().getDeclaredFields();

						for (Field field : fields) {
							field.setAccessible(true);
							innerXml.append(buildXmlForRecordItem(field.getName(), ReflectionUtils.getField(field, columnValue)));
							field.setAccessible(false);
						}
					}
				}
			}
		}

		if (StringUtils.hasText(columnName)) {
			innerXml.append("</").append(columnName).append(">");
		}


		return innerXml.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDataSetAccessor(MciDataSetAccessor accessor) {
		this.accessor = accessor;
	}

}
