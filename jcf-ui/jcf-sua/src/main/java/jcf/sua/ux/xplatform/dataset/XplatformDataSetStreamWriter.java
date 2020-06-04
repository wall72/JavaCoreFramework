package jcf.sua.ux.xplatform.dataset;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import jcf.sua.dataset.DataSetStreamWriter;

import com.tobesoft.xplatform.data.DataSet;
import com.tobesoft.xplatform.data.DataTypes;

/**
 *
 * {@link DataSetStreamWriter} 의  Xplatform 구현체
 *
 * @author nolang
 *
 */
public class XplatformDataSetStreamWriter implements DataSetStreamWriter {

	private DataSet dataSet;
	private int bufferSize;
	private boolean isFirst = true;
	private HttpServletResponse response;
	private OutputStream out;

	public XplatformDataSetStreamWriter(HttpServletResponse response) {
		this.response = response;
	}

	/**
	 * {@inheritDoc}
	 */
	public void startStream(String dataSetId, int bufferSize) {
		try {
			this.out = response.getOutputStream();
		} catch (IOException e) {
		}

		this.dataSet = new DataSet(dataSetId);
		this.bufferSize = bufferSize;
		this.isFirst = true;
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
	public void endStream() {
		output(buildDataSetMessage(dataSet));

		try {
			out.close();
		} catch (IOException e) {
		}
	}

	private void output(String message) {
		try {
			out.write(message.getBytes());
			out.write(System.getProperty("line.separator").getBytes());
			out.flush();
		} catch (Exception e) {
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

	private String buildDataSetHeaderMessage(DataSet ds) {
		StringBuilder builder = new StringBuilder();

		builder.append("Dataset:").append(ds.getName()).append(System.getProperty("line.separator"));

		for (int i = 0; i < ds.getColumnCount(); i++) {
			builder.append(ds.getColumn(i).getName()).append(":").append(ds.getColumn(i).getDataType()).append(",");
		}

		if (0 < ds.getColumnCount())	{
			builder.replace(builder.length() - 1, builder.length(), System.getProperty("line.separator"));
		}

		return builder.toString();
	}

	private String buildDataSetMessage(DataSet ds) {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < ds.getRowCount(); i++) {
			for (int j = 0; j < ds.getColumnCount(); j++) {
				Object obj = ds.getObject(i, j);

				if (obj != null) {
					if (obj instanceof String) {
						builder.append("\"").append(ds.getString(i, j)).append("\"");
					} else {
						builder.append(ds.getString(i, j));
					}
				}

				if (j < ds.getColumnCount() - 1)	{
					builder.append(",");
				}
			}

			if (0 < ds.getColumnCount() && i < ds.getRowCount() - 1)	{
				builder.replace(builder.length() - 1, builder.length(),	System.getProperty("line.separator"));
			}
		}

		return builder.toString();
	}

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

				dataSet.addColumn(columnName, getXPlatformType(columnType));
			}
		} else {
			Field[] fields = data.getClass().getDeclaredFields();

			for (Field field : fields) {
				dataSet.addColumn(field.getName(), getXPlatformType(field.getType()));
			}
		}
	}

	private void setColumnData(Object data, int row) {

		int col = 0;

		if (Map.class.isAssignableFrom(data.getClass())) {
			Iterator<String> it = ((Map<String, ?>) data).keySet().iterator();

			while (it.hasNext()) {
				dataSet.set(row, col++, ((Map<String, ?>) data).get(it.next()));
			}
		} else {
			Field[] fields = data.getClass().getDeclaredFields();

			for (Field field : fields) {
				dataSet.set(row, col++, getColumnValue(field, data));
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

	private int getXPlatformType(Class<?> clazz) {
		int xplatformType = DataTypes.BLOB;

		if (clazz.isAssignableFrom(String.class)) {
			xplatformType = DataTypes.STRING;
		} else if (clazz.isAssignableFrom(Integer.class) || clazz.isAssignableFrom(int.class)) {
			xplatformType = DataTypes.INT;
		} else if (clazz.isAssignableFrom(Long.class) || clazz.isAssignableFrom(long.class)) {
			xplatformType = DataTypes.LONG;
		} else if (clazz.isAssignableFrom(Double.class)	|| clazz.isAssignableFrom(double.class)) {
			xplatformType = DataTypes.BIG_DECIMAL;
		} else if (clazz.isAssignableFrom(Date.class)) {
			xplatformType = DataTypes.DATE;
		}

		return xplatformType;
	}
}
