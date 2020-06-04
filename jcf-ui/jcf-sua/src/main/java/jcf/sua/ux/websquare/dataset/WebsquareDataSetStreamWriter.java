package jcf.sua.ux.websquare.dataset;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import jcf.sua.dataset.DataSetStreamWriter;
import jcf.sua.ux.websquare.dataset.annotation.ColumnOrder;

import org.springframework.util.ReflectionUtils;

import java.util.Arrays;

/**
 *
 * {@link DataSetStreamWriter} 의 웹스퀘어 구현체
 *
 * @author nolang
 *
 */
public class WebsquareDataSetStreamWriter implements DataSetStreamWriter {

	private static final String delimeter = "%||%";
	private List<Field> fieldOrder;
	private HttpServletResponse response;
	private PrintWriter writer;
	private boolean isFirstRow = true;
	
	public WebsquareDataSetStreamWriter(HttpServletResponse response) {
//		response.setContentType("application/xml");
		
		this.response = response;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void startStream(String dataSetId, int bufferSize) {
//		writer.append("<map id=\"\">");
//		writer.append("<vector id=\"" + dataSetId + "\">");
//		writer.append("<data value=\"");
		
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		
		try {
			writer = response.getWriter();
		} catch (IOException e) {
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void addStreamData(Object data) {
		
		String value = "";
		
		if (data != null) {
			if (data instanceof String) {
				value = String.valueOf(data);
			} else {
				value = buildStreamMessage(data);
			}
		}
		
//		writer.append("<map id=\"\">");
//		writer.append(String.format("<data value=\"%s\" />", value));
//		writer.append("</map>");
		
		writer.append(value);
	}

	private String buildStreamMessage(Object data) {
		StringBuilder builder = new StringBuilder();
		
		if(!isFirstRow)	{
			builder.append(delimeter);
		} else {
			isFirstRow = false;
		}
		
		if(data instanceof Map){
			boolean first = true;
			
			for (Map.Entry<String, Object> e : ((Map<String, Object>) data).entrySet()) {
				if(!first)	{
					builder.append(delimeter);
				}
				
				builder.append(e.getValue());
				first = false;
			}
		} else	{
			if(fieldOrder == null)	{
				Field[] fields = data.getClass().getDeclaredFields();
				
				Arrays.sort(fields, new Comparator<Field>() {
					public int compare(Field f1, Field f2) {
						ColumnOrder f1Order = f1.getAnnotation(ColumnOrder.class);
						ColumnOrder f2Order = f2.getAnnotation(ColumnOrder.class);
						
						if (f1Order == null && f2Order == null) {
							return -1;
						} else if (f1Order == null) {
							return 1;
						} else if (f2Order == null) {
							return -1;
						} else {
							return f1Order.order() - f2Order.order();
						}
					}
				});
			
				fieldOrder = Arrays.asList(fields);
			}
			
			for (int i = 0; i < fieldOrder.size(); ++i) {
				if (i > 0) {
					builder.append(delimeter);
				}
				
				ReflectionUtils.makeAccessible(fieldOrder.get(i));
				builder.append(ReflectionUtils.getField(fieldOrder.get(i), data));
			}
		}
		
		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public void endStream() {
//		writer.append("</vector>");
//		writer.append("\" />");
//		writer.append("</map>");
		writer.flush();
		writer.close();
	}

}
