package jcf.sua.ux.nexacro.dataset;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import jcf.sua.dataset.DataSetStreamWriter;
import jcf.sua.exception.MciException;
import jcf.sua.ux.xplatform.dataset.XplatformDataSetWriter;

import com.nexacro.xapi.data.DataSet;
import com.nexacro.xapi.data.DataTypes;

public class NexacroDataSetStreamWriter implements DataSetStreamWriter {

	private DataSet dataSet;
	private int bufferSize;
	private boolean isFirst = true;
	private HttpServletResponse response;
	private OutputStream out;
	
	public NexacroDataSetStreamWriter(HttpServletResponse response) {
		this.response = response;
	}

	public void startStream(String dataSetId, int bufferSize) {
		try {
			this.out = response.getOutputStream();
		} catch (IOException e) {
			throw new MciException("[JCF-SUA] NexacroDataSetStreamWriter - Streaming 전송을 시작하지 못했습니다. ");
		}

		this.dataSet = new DataSet(dataSetId);
		this.bufferSize = bufferSize;
		this.isFirst = true;
	}

	public void addStreamData(Object data) {
		if (isFirst) {
			isFirst = false;

			setColumnMetadata(data);

			output(buildHeaderMessage("utf-8"));
			output(buildVariableMessage());
			output(buildDataSetHeaderMessage(dataSet));
		}

		int currRowCount = dataSet.newRow();

		setColumnData(data, currRowCount);

		if(currRowCount >= bufferSize)	{
			output(buildDataSetMessage(dataSet));
			dataSet.clearData();
		}
	}
	
	public void endStream() {
		output(buildDataSetMessage(dataSet));
		
		try {
			out.close();
		} catch (IOException e) {
			throw new MciException("[JCF-SUA] NexacroDataSetStreamWriter - Stream을 정상적으로 닫지 못했습니다. ");
		}
	}

	private void output(String message) {
		try {
			out.write(message.getBytes());
			out.write(System.getProperty("line.separator").getBytes());
			out.flush();
		} catch (Exception e) {
			throw new MciException("[JCF-SUA] NexacroDataSetStreamWriter - Streaming 전송 중 예기지 못한 오류가 발생했습니다. ");
		}
	}
	
	private String buildHeaderMessage(String charset) {
		StringBuilder builder = new StringBuilder();
		builder.append("CSV:").append(charset).append(System.getProperty("line.separator"));
		return builder.toString();
	}
	
	private String buildVariableMessage() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("\"").append(XplatformDataSetWriter.ERROR_CODE_PARAMETER_NAME);
		builder.append("=");
		builder.append("0").append("\",");
		builder.append("\"").append(XplatformDataSetWriter.ERROR_MSG_PARAMETER_NAME);
		builder.append("=");
		builder.append("SUCCESS").append("\",");
		
		return builder.toString();
	}
	
	private String buildDataSetHeaderMessage(DataSet dataSet) {
		StringBuilder builder = new StringBuilder();
		
		builder.append("Dataset:").append(dataSet).append(System.getProperty("line.separator"));
		
		for (int i = 0; i < dataSet.getColumnCount(); i++) {
			builder.append(dataSet.getColumn(i).getName()).append(":").append(dataSet.getColumn(i).getDataType()).append(",");
		}
		
		if (0 < dataSet.getColumnCount())	{
			builder.replace(builder.length() - 1, builder.length(), System.getProperty("line.separator"));
		}
		
		return builder.toString();
	}
	
	private String buildDataSetMessage(DataSet dataSet) {
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < dataSet.getRowCount(); i++) {
			for (int j = 0; j < dataSet.getColumnCount(); j++) {
				Object obj = dataSet.getObject(i, j);
				
				if (obj != null) {
					if (obj instanceof String) {
						builder.append("\"").append(dataSet.getString(i, j)).append("\"");
					} else {
						builder.append(dataSet.getString(i, j));
					}
				}
				
				if (j < dataSet.getColumnCount() - 1)	{
					builder.append(",");
				}
			}
			
			if (0 < dataSet.getColumnCount() && i < dataSet.getRowCount() - 1)	{
				builder.replace(builder.length() - 1, builder.length(),	System.getProperty("line.separator"));
			}
		}
		
		return builder.toString();
	}
	
	@SuppressWarnings("unchecked")
	private void setColumnMetadata(Object data) {
		if (Map.class.isAssignableFrom(data.getClass())) {
			Iterator<String> it = ((Map<String, ?>) data).keySet().iterator();

			while (it.hasNext()) {
				String columnName = it.next();
				Class<?> columnType = null;

				if (((Map<String, ?>) data).get(columnName) == null) {
					columnType = String.class;
				} else {
					columnType = ((Map<String, ?>) data).get(columnName).getClass();
				}

				dataSet.addColumn(columnName, getNexacroType(columnType));
			}
		} else {
			Field[] fields = data.getClass().getDeclaredFields();

			for (Field field : fields) {
				dataSet.addColumn(field.getName(), getNexacroType(field.getType()));
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void setColumnData(Object data, int rowCount) {
		int col = 0;

		if (Map.class.isAssignableFrom(data.getClass())) {
			Iterator<String> it = ((Map<String, ?>) data).keySet().iterator();

			while (it.hasNext()) {
				dataSet.set(rowCount, col++, ((Map<String, ?>) data).get(it.next()));
			}
		} else {
			Field[] fields = data.getClass().getDeclaredFields();

			for (Field field : fields) {
				dataSet.set(rowCount, col++, getColumnValue(field, data));
			}
		}
	}
	
	private Object getColumnValue(Field field, Object data) {
		Object columnValue = null;

		field.setAccessible(true);

		try {
			columnValue = field.get(data);
		} catch (Exception e) {
		}

		return columnValue;
	}
	
	private int getNexacroType(Class<?> clazz) {
		int nexacroType = DataTypes.BLOB;

		if (clazz.isAssignableFrom(String.class)) {
			nexacroType = DataTypes.STRING;
		} else if (clazz.isAssignableFrom(Integer.class) || clazz.isAssignableFrom(int.class)) {
			nexacroType = DataTypes.INT;
		} else if (clazz.isAssignableFrom(Long.class) || clazz.isAssignableFrom(long.class)) {
			nexacroType = DataTypes.LONG;
		} else if (clazz.isAssignableFrom(Double.class)	|| clazz.isAssignableFrom(double.class)) {
			nexacroType = DataTypes.BIG_DECIMAL;
		} else if (clazz.isAssignableFrom(Date.class)) {
			nexacroType = DataTypes.DATE;
		}

		return nexacroType;
	}

}
