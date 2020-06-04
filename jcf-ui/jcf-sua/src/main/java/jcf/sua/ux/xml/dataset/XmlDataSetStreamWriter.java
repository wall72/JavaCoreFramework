package jcf.sua.ux.xml.dataset;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import jcf.sua.dataset.DataSetStreamWriter;
import jcf.sua.ux.xml.NodeConstants;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 *
 * {@link DataSetStreamWrter} 의 XML 구현체
 *
 * @author nolang
 *
 */
public class XmlDataSetStreamWriter implements DataSetStreamWriter {

	private HttpServletResponse response;

	private String contentType = "application/json";

	private BufferedWriter writer;
	private String dataSetId;

	public XmlDataSetStreamWriter(HttpServletResponse response) {
		this.response = response;
	}

	/**
	 * {@inheritDoc}
	 */
	public void startStream(String dataSetId, int bufferSize) {
		this.dataSetId = dataSetId;

		response.setContentType(contentType);
		response.setCharacterEncoding("utf-8");

		StringBuilder xmlBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");

		xmlBuilder.append("<").append(NodeConstants.rootNode).append(">");
		xmlBuilder.append("<").append(NodeConstants.headerNode).append(">");
		xmlBuilder.append("<").append(NodeConstants.codeNode).append(">");
		xmlBuilder.append(NodeConstants.streaming);
		xmlBuilder.append("</").append(NodeConstants.codeNode).append(">");
		xmlBuilder.append("<").append(NodeConstants.messageNode).append(">");
		xmlBuilder.append("</").append(NodeConstants.messageNode).append(">");
		xmlBuilder.append("</").append(NodeConstants.headerNode).append(">");
		xmlBuilder.append("<").append(NodeConstants.contentNode).append(">");
		xmlBuilder.append("<").append(dataSetId).append(">");
		xmlBuilder.append("<").append(NodeConstants.dataNode).append(">");

		try {
			writer = new BufferedWriter(response.getWriter(), bufferSize);
			writer.write(xmlBuilder.toString());
		} catch (IOException e) {
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void addStreamData(Object data) {
		try {
			writer.write(buildXmlForRecordItem(NodeConstants.recordNode, data));
		} catch (IOException e) {
		}
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
	public void endStream() {
		StringBuilder xmlBuilder = new StringBuilder();

		xmlBuilder.append("</").append(NodeConstants.dataNode).append(">");
		xmlBuilder.append("</").append(dataSetId).append(">");
		xmlBuilder.append("</").append(NodeConstants.contentNode).append(">");
		xmlBuilder.append("</").append(NodeConstants.rootNode).append(">");

		try {
			writer.write(xmlBuilder.toString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
		}

	}

}
