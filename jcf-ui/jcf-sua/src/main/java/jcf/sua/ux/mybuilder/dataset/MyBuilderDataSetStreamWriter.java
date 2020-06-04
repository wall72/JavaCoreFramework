package jcf.sua.ux.mybuilder.dataset;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import jcf.sua.dataset.DataSetStreamWriter;
import jcf.sua.ux.mybuilder.MybuilderConstants;
import jcf.sua.ux.websquare.dataset.annotation.ColumnOrder;

import org.apache.commons.codec.binary.Base64;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * 
 * @author nolang
 *
 */
public class MyBuilderDataSetStreamWriter implements DataSetStreamWriter {

	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MybuilderConstants.DEFAULT_DATE_FORMAT);

	private HttpServletResponse response;
	private PrintWriter writer;
	private boolean isFirstRow = true;
	private int readCountPerRequest;
	private int currentRowNum;

	private List<Field> fieldOrder;

	public MyBuilderDataSetStreamWriter(HttpServletResponse response) {
		this.response = response;
	}
	
	public void startStream(String dataSetId, int bufferSize) {
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		
		try {
			writer = response.getWriter();
		} catch (IOException e) {
		}
		
		this.readCountPerRequest = bufferSize;
		this.currentRowNum = 0;
	}

	public void addStreamData(Object data) {
		String value = "";
		
		if (data != null) {
			if (data instanceof String) {
				value = String.valueOf(data);
			} else {
				value = buildStreamMessage(data);
			}
		}
		
		writer.print(value);
	}

	private String buildStreamMessage(Object data) {
		StringBuilder builder = new StringBuilder();
		
		if (isFirstRow) {
			if (data instanceof Map) {
				boolean first = true;
				
				for (Map.Entry<String, Object> e : ((Map<String, Object>) data).entrySet()) {
					if(!first)	{
						builder.append(MybuilderConstants.MSV_COL_SEP);
					}
					
					builder.append(e.getKey());
					
					if(e.getValue() != null){
						if( isNumeralType(e.getValue().getClass()) ) {
							builder.append("#"); // 숫자 컬럼은 컬럼명뒤에 '#'을 붙여준다.
						}
					}
					
					first = false;
				}
			} else {
				
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
						builder.append(MybuilderConstants.MSV_COL_SEP);
					}
					
					builder.append(fieldOrder.get(i).getName());
					
					if( isNumeralType(fieldOrder.get(i).getType()) ) {
						builder.append("#"); // 숫자 컬럼은 컬럼명뒤에 '#'을 붙여준다.
					}

					ReflectionUtils.makeAccessible(fieldOrder.get(i));
				}
			}
			
			builder.append(MybuilderConstants.MSV_LINE_SEP);

			isFirstRow = false;
		}
		

		if(data instanceof Map){
			boolean first = true;
			
			for (Map.Entry<String, Object> e : ((Map<String, Object>) data).entrySet()) {
				if(!first)	{
					builder.append(MybuilderConstants.MSV_COL_SEP);
				}
				
				builder.append(e.getValue());
				
				if(e.getValue() != null){
					if( isNumeralType(e.getValue().getClass()) ) {
						builder.append("#"); // 숫자 컬럼은 컬럼명뒤에 '#'을 붙여준다.
					}
				}
				
				first = false;
			}
		} else	{
			for (int i = 0; i < fieldOrder.size(); ++i) {
				if (i > 0) {
					builder.append(MybuilderConstants.MSV_COL_SEP);
				}
				
				builder.append(getPropertyValue(ReflectionUtils.getField(fieldOrder.get(i), data)));
				
				if( isNumeralType(fieldOrder.get(i).getType()) ) {
					builder.append("#"); // 숫자 컬럼은 컬럼명뒤에 '#'을 붙여준다.
				}
			}
		}
		
		builder.append(MybuilderConstants.MSV_LINE_SEP);
		
		if(readCountPerRequest > 0){
			if(++currentRowNum % readCountPerRequest == 0)	{
				builder.append(MybuilderConstants.MORE);
			}
		}
		
		return builder.toString();
	}

	private String getPropertyValue(Object property) {
		String propertyValue = "";
		
		if(property != null)	{
			if (property instanceof Date) {
				propertyValue = simpleDateFormat.format((Date) property);
			} else if (property instanceof Byte[]) {
				propertyValue = new String(Base64.encodeBase64((byte[]) property));
			} else {
				propertyValue = String.valueOf(property);
			}
		}

		return propertyValue;
	}

	private boolean isNumeralType(Class<?> type){
		return ClassUtils.isAssignable(type, Integer.class) ||
				ClassUtils.isAssignable(type, Double.class) ||
				ClassUtils.isAssignable(type, Float.class)	||
				ClassUtils.isAssignable(type, BigDecimal.class) ||
				ClassUtils.isAssignable(type, BigInteger.class)	||
				ClassUtils.isAssignable(type, Number.class) ||
				ClassUtils.isAssignable(type, Long.class) ||
				ClassUtils.isAssignable(type, Short.class);
	}

	public void endStream() {
		writer.flush();
		writer.close();
	}

}
