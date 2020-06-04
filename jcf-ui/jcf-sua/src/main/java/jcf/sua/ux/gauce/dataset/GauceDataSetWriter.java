package jcf.sua.ux.gauce.dataset;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import jcf.sua.dataset.DataSet;
import jcf.sua.dataset.DataSetRow;
import jcf.sua.dataset.DataSetWriter;
import jcf.sua.exception.MciException;
import jcf.sua.mvc.MciDataSetAccessor;
import jcf.sua.mvc.MciRequestContextHolder;

import org.springframework.util.StringUtils;

import com.gauce.GauceDataColumn;
import com.gauce.GauceDataRow;
import com.gauce.GauceDataSet;
import com.gauce.GauceException;
import com.gauce.http.HttpGauceResponse;
import com.gauce.io.GauceOutputStream;

/**
 *
 * {@link DataSetWriter}
 *
 * @author nolang
 *
 */
public class GauceDataSetWriter implements DataSetWriter {

	private MciDataSetAccessor accessor;

	private HttpGauceResponse response;

	private GauceOutputStream out;

	private int firstRowsSize;

	public GauceDataSetWriter(HttpServletResponse response, MciDataSetAccessor accessor, int firstRowSize) {
		this.accessor = accessor;
		this.response = (HttpGauceResponse) response;
		this.firstRowsSize = firstRowSize;
	}

	/**
	 * {@inheritDoc}
	 */
	public void write()	{
		/*
		 * Open OutputStream
		 */
		open();

		Map<String, DataSet> dataSetMap = accessor.getDataSetMap();

		if (dataSetMap == null) {
			dataSetMap = MciRequestContextHolder.get().getDataSetAccessor().getDataSetMap();
		}

		Iterator<String> it = dataSetMap.keySet().iterator();

		while (it.hasNext()) {
			GauceDataSet gauceDataSet = createGauceDataSet(dataSetMap.get(it.next()));

			try {
				out.write(gauceDataSet);
			} catch (IOException e) {
				throw new MciException("IOException - write", e);
			}
		}

		/*
		 * Success Message 전송
		 */
		List<String> successMessages = accessor.getSuccessMessags();

		for (String message : successMessages) {
			response.addMessage(message);
		}

		/*
		 * Exception Message 전송
		 */
		if(StringUtils.hasText(accessor.getExceptionMessage()))	{
			response.addException(new GauceException("", 0, accessor.getExceptionMessage()));
		}

		/*
		 * Close OutputStream
		 */
		close();
	}

	private void open()	{
		try {
			out = response.getGauceOutputStream();
		} catch (IOException e) {
			throw new MciException("IOException", e);
		}
	}

	private void close() {
		try {
			out.close();
		} catch (IOException e) {
			throw new MciException("IOException - close", e);
		}
	}

	private GauceDataSet createGauceDataSet(DataSet dataSet) {
		GauceDataSet gauceDataSet = new GauceDataSet(dataSet.getId());

		if(firstRowsSize > 0){
			/*
			 * FirstRow 전송을 위해서는 GauceDataSet을 output stream의 fragment로 정의해주어야 한다.
			 */
			out.fragment(gauceDataSet, firstRowsSize <= 0 ? 100 : firstRowsSize);
		}

		int rowCount = dataSet.getRowCount();
		int columnCount = dataSet.getColumnCount();

		for (int col = 0; col < columnCount; ++col) {
			String columnName = dataSet.getDataSetColumn(col).getColumnName();
			Class<?> columnType = dataSet.getDataSetColumn(col).getColumnType();

			gauceDataSet.addDataColumn(new GauceDataColumn(columnName, getGauceType(columnType)));
		}

		for (int row = 0; row < rowCount; ++row) {
			DataSetRow dataSetRow = dataSet.getDataSetRow(row);

			GauceDataRow gauceDataRow = new GauceDataRow(GauceDataRow.TB_JOB_NORMAL);

			for (int col = 0; col < columnCount; ++col) {
				String columnName = dataSet.getDataSetColumn(col).getColumnName();
				Object colunmValue = dataSetRow.get(columnName);

				gauceDataRow.addColumnValue(colunmValue);
			}

			gauceDataSet.addDataRow(gauceDataRow);
		}

		return gauceDataSet;
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

	/**
	 * {@inheritDoc}
	 */
	public void setDataSetAccessor(MciDataSetAccessor accessor) {
		this.accessor = accessor;
	}
}
