package jcf.sua.ux.miplatform.dataset;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import jcf.sua.dataset.DataSetStreamWriter;
import jcf.sua.ux.UxConstants;

import com.tobesoft.platform.data.ColumnInfo;
import com.tobesoft.platform.data.Dataset;
import com.tobesoft.platform.data.Variant;



/**
 *
 * {@link DataSetStreamWriter}
 *
 * @author alsdkzz
 *
 */
public class  MiplatformDataSetStreamWriter implements DataSetStreamWriter {

	private Dataset dataset;
	private int bufferSize;
	private boolean isFirst = true;
	private HttpServletResponse response;
	private OutputStream out;

	public MiplatformDataSetStreamWriter(HttpServletResponse response) {
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

		this.dataset = new Dataset(dataSetId);
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
			output(buildDataSetHeaderMessage(dataset));
		}

		int currRowCount = dataset.appendRow();

		setColumnData(data, currRowCount);

		if(currRowCount >= bufferSize)	{
			output(buildDataSetMessage(dataset));
			dataset.clearAll();
		}
	}


	/**
	 * {@inheritDoc}
	 */
	public void endStream() {
		output(buildDataSetMessage(dataset));

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

		builder.append("\"").append(UxConstants.ERROR_CODE_PARAMETER_NAME);
		builder.append("=");
		builder.append("0").append("\",");
		builder.append("\"").append(UxConstants.ERROR_MSG_PARAMETER_NAME);
		builder.append("=");
		builder.append("SUCCESS").append("\",");

		return builder.toString();
	}

	private String buildDataSetHeaderMessage(Dataset ds) {
		StringBuilder builder = new StringBuilder();

		builder.append("Dataset:").append(ds.getDataSetID()).append(System.getProperty("line.separator"));
		for (int i = 0; i < ds.getColumnCount(); i++) {
			builder.append(	ds.getColumnID(i)).append(":").append(ds.getColumnInfo(i).getColumnType()).append(",");
	}


		if (0 < ds.getColumnCount())	{
			builder.replace(builder.length() - 1, builder.length(), System.getProperty("line.separator"));
		}

		return builder.toString();
	}

	private String buildDataSetMessage(Dataset ds) {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < ds.getRowCount(); i++) {
			for (int j = 0; j < ds.getColumnCount(); j++) {
				Object obj = ds.getColumnAsObject(i, j);
				if (obj != null) {
					if (obj instanceof String) {
						builder.append("\"").append(ds.getColumnAsString(i, j)).append("\"");
					} else {
						builder.append(ds.getColumnAsString(i, j));
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
				dataset.addColumn(columnName, getMiPlatformType(columnType), getMiPlatformColSize(columnType));
			}
		} else {
			Field[] fields = data.getClass().getDeclaredFields();
			for (Field field : fields) {
				dataset.addColumn(field.getName(), getMiPlatformType(field.getType()),  getMiPlatformColSize(field.getType()));
			}
		}
	}


	private short getMiPlatformColSize(Class<?> clazz){
		short miplatformColSize = 255;
		if (clazz.isAssignableFrom(byte[].class)) {
			miplatformColSize=20000;
		}
		return miplatformColSize;
	}


	private void setColumnData(Object data, int row) {

		int col = 0;

		if (Map.class.isAssignableFrom(data.getClass())) {
			Iterator<String> it = ((Map<String, ?>) data).keySet().iterator();

			while (it.hasNext()) {
				Variant colunmValue= new Variant( ((Map<String, ?>) data).get(it.next()));
				dataset.setColumn(row, col++,colunmValue );
			}
		} else {
			Field[] fields = data.getClass().getDeclaredFields();

			for (Field field : fields) {
				Variant colunmValue= new Variant( getColumnValue(field, data));
				dataset.setColumn(row, col++,colunmValue );

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

	private short getMiPlatformType(Class<?> clazz) {
		short miplatformType = ColumnInfo.COLUMN_TYPE_BLOB;
		if (clazz.isAssignableFrom(String.class)) {
			miplatformType =ColumnInfo.COLUMN_TYPE_STRING;
		} else if (clazz.isAssignableFrom(Integer.class) || clazz.isAssignableFrom(int.class)) {
			miplatformType = ColumnInfo.COLUMN_TYPE_INT;
		} else if (clazz.isAssignableFrom(BigDecimal.class) || clazz.isAssignableFrom(BigDecimal.class)) {
			miplatformType = ColumnInfo.COLUMN_TYPE_INT;
		} else if (clazz.isAssignableFrom(Long.class) || clazz.isAssignableFrom(long.class)) {
			miplatformType = ColumnInfo.COLUMN_TYPE_LONG;
		} else if (clazz.isAssignableFrom(Double.class)	|| clazz.isAssignableFrom(double.class)) {
			miplatformType = ColumnInfo.COLUMN_TYPE_DECIMAL;
		} else if (clazz.isAssignableFrom(Date.class)) {
			miplatformType = ColumnInfo.COLUMN_TYPE_DATE;
		}
		return miplatformType;
	}

}
