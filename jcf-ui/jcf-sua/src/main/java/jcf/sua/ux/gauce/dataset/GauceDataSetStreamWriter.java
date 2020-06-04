package jcf.sua.ux.gauce.dataset;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import jcf.sua.dataset.DataSetStreamWriter;
import jcf.sua.exception.MciException;

import com.gauce.GauceDataColumn;
import com.gauce.GauceDataRow;
import com.gauce.GauceDataSet;
import com.gauce.http.HttpGauceResponse;
import com.gauce.io.GauceOutputStream;

/**
 *
 * {@link DataSetStreamWriter}
 *
 * @author nolang
 *
 */
@SuppressWarnings("unchecked")
public class GauceDataSetStreamWriter implements DataSetStreamWriter {

	private HttpGauceResponse response;

	private GauceOutputStream out;

	private GauceDataSet gauceDataSet;

	public GauceDataSetStreamWriter(HttpServletResponse response) {
		this.response = (HttpGauceResponse) response;
	}

	/**
	 * {@inheritDoc}
	 */
	public void startStream(String dataSetId, int bufferSize) {
		this.gauceDataSet = new GauceDataSet(dataSetId);

		try {
			out = response.getGauceOutputStream();

			if (bufferSize > 0) {
				out.fragment(gauceDataSet, bufferSize);
			}
		} catch (IOException e) {
			throw new MciException("IOException", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void addStreamData(Object data) {
		if (gauceDataSet.getDataRowCnt() == 0) {
			setColumnMetadata(data);
		}

		setColumnData(data);
	}

	/**
	 * {@inheritDoc}
	 */
	public void endStream() {
		try {
			out.write(gauceDataSet);
			out.close();
		} catch (IOException e) {
			throw new MciException("IOException - close", e);
		}
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

				gauceDataSet.addDataColumn(new GauceDataColumn(columnName, getGauceType(columnType)));
			}
		} else {
			Field[] fields = data.getClass().getDeclaredFields();

			for (Field field : fields) {
				gauceDataSet.addDataColumn(new GauceDataColumn(field.getName(), getGauceType(field.getType())));
			}
		}
	}

	private void setColumnData(Object data) {
		GauceDataRow gauceDataRow = new GauceDataRow(GauceDataRow.TB_JOB_NORMAL);

		if (Map.class.isAssignableFrom(data.getClass())) {
			Iterator<String> it = ((Map<String, ?>) data).keySet().iterator();

			while (it.hasNext()) {
				gauceDataRow.addColumnValue(((Map<String, ?>) data).get(it.next()));
			}
		} else {
			Field[] fields = data.getClass().getDeclaredFields();

			for (Field field : fields) {
				gauceDataRow.addColumnValue(getColumnValue(field, data));
			}
		}

		gauceDataSet.addDataRow(gauceDataRow);
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

	private int getGauceType(Class<?> clazz) {
		int gauceType = GauceDataColumn.TB_BLOB;

		if (clazz.isAssignableFrom(String.class)) {
			gauceType = GauceDataColumn.TB_STRING;
		} else if (clazz.isAssignableFrom(Integer.class) || clazz.isAssignableFrom(int.class)) {
			gauceType = GauceDataColumn.TB_INT;
		} else if (clazz.isAssignableFrom(Long.class) || clazz.isAssignableFrom(long.class)) {
			gauceType = GauceDataColumn.TB_BIGINT;
		} else if (clazz.isAssignableFrom(Double.class) || clazz.isAssignableFrom(double.class)) {
			gauceType = GauceDataColumn.TB_DECIMAL;
		} else if (clazz.isAssignableFrom(Date.class)) {
			gauceType = GauceDataColumn.TB_DATE;
		}

		return gauceType;
	}
}
