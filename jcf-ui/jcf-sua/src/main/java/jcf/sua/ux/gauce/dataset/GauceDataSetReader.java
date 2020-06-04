package jcf.sua.ux.gauce.dataset;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jcf.data.RowStatus;
import jcf.sua.dataset.DataSetColumn;
import jcf.sua.dataset.DataSetReader;
import jcf.sua.dataset.DataSetRow;
import jcf.sua.dataset.DataSetRowImpl;
import jcf.sua.exception.MciException;
import jcf.sua.ux.UxConstants;
import jcf.sua.ux.gauce.exception.GauceParserException;
import jcf.upload.FileInfo;

import com.gauce.GauceDataColumn;
import com.gauce.GauceDataRow;
import com.gauce.GauceDataSet;
import com.gauce.http.HttpGauceRequest;
import com.gauce.io.GauceInputStream;

/**
 *
 * {@link DataSetReader}
 *
 * @author nolang
 *
 */
public class GauceDataSetReader implements DataSetReader {

	private HttpServletRequest request;
	private Map<String, GauceDataSet> dataSetMap = new HashMap<String, GauceDataSet>();

	public GauceDataSetReader(HttpServletRequest request) {
		this.request = request;

		try {
			GauceInputStream in = ((HttpGauceRequest) this.request).getGauceInputStream();

			if(in != null)	{
				GauceDataSet[] dataSets = in.readAll();

				for(GauceDataSet dataSet : dataSets) {
					dataSetMap.put(dataSet.getName(), dataSet);
				}
			}
		} catch (IOException e) {
			throw new GauceParserException("", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, Object> getParamMap() {
		Map<String, Object> paramMap = new HashMap<String, Object>();

		@SuppressWarnings("unchecked")
		Map<String, String[]> requestParams = request.getParameterMap();

		Iterator<String> it = requestParams.keySet().iterator();

		while (it.hasNext()) {
			String paramName = it.next();
			String[] paramValue = requestParams.get(paramName);

			if (paramValue != null) {
				if(paramValue.length > 1){
					paramMap.put(paramName, paramValue);
				} else {
					paramMap.put(paramName, paramValue[0]);
				}
			}
		}

		return paramMap;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<String> getDataSetIdList() {
		return Collections.unmodifiableList(new ArrayList<String>(dataSetMap.keySet()));
	}

	/**
	 * {@inheritDoc}
	 */
	public List<DataSetRow> getDataSetRows(String dataSetId)	{
		GauceDataSet dataSet = getGauceDataSet(dataSetId);

		List<DataSetRow> rows = new ArrayList<DataSetRow>();

		GauceDataColumn[] dataCols = dataSet.getDataColumns();
		GauceDataRow[] dataRows = dataSet.getDataRows();

		for (GauceDataRow row : dataRows) {
			DataSetRow dataSetRow = new DataSetRowImpl();

			for(GauceDataColumn column : dataCols){
				if(column.getColType() == GauceDataColumn.TB_BLOB){
					/*
					 * Column의 type = GauceDataColumn.TB_BLOB 인 경우 파일의 스트림을 입력받는다.
					 */
					int fileUrlColumn = dataSet.indexOfColumn(UxConstants.Gauce.COLUMN_FILEURL);

					try {
						InputStream is = (InputStream) row.getInputStream(fileUrlColumn);

						ByteArrayOutputStream bos = new ByteArrayOutputStream();

						byte[] buf = new byte[1024];
						int readByte = -1;

						while ((readByte = is.read(buf)) != -1) {
							bos.write(buf);
						}

						dataSetRow.add(column.getColName(), bos.toByteArray());

						bos.close();

					} catch (IOException e) {
						throw new MciException("파일처리중 에러발생", e);
					}
				} else {
					dataSetRow.add(column.getColName(), row.getColumnValue(dataSet.indexOfColumn(column.getColName())));
				}
			}

			dataSetRow.setRowStatus(getStatusOf(row.getJobType()));

			rows.add(dataSetRow);
		}

		return rows;

	}

	/**
	 * {@inheritDoc}
	 */
	public List<DataSetColumn> getDataSetColumns(String dataSetId) {
		GauceDataSet dataSet = getGauceDataSet(dataSetId);
		List<DataSetColumn> cols = new ArrayList<DataSetColumn>();

		GauceDataColumn[] columns = dataSet.getDataColumns();

		for (GauceDataColumn column : columns) {
			cols.add(new DataSetColumn(column.getColName(), getJavaType(column.getColType())));
		}

		return cols;
	}

	private Class<?> getJavaType(int colType) {

		Class<?> javaType = null;

		switch(colType){
			case GauceDataColumn.TB_STRING :
				javaType = String.class;
				break;
			case GauceDataColumn.TB_INT:
				javaType = Integer.class;
				break;
			case GauceDataColumn.TB_BIGINT:
				javaType = Long.class;
				break;
			case GauceDataColumn.TB_DECIMAL:
				javaType = Double.class;
				break;
			case GauceDataColumn.TB_DATE:
				javaType = Date.class;
				break;
			case GauceDataColumn.TB_BLOB:
				javaType = byte[].class;
			default:
				javaType = Object.class;
		}

		return javaType;
	}

	private GauceDataSet getGauceDataSet(String dataSetId)	{
		return dataSetMap.get(dataSetId);
	}

	private String getStatusOf(int jobType) {
		RowStatus rowStatus = null;

		switch (jobType) {
			case GauceDataRow.TB_JOB_INSERT:
				rowStatus = RowStatus.INSERT;
				break;
			case GauceDataRow.TB_JOB_UPDATE:
				rowStatus = RowStatus.UPDATE;
				break;
			case GauceDataRow.TB_JOB_DELETE:
				rowStatus = RowStatus.DELETE;
				break;
			default:
				rowStatus = RowStatus.NORMAL;
		}

		return rowStatus.name();
	}

	/**
	 * {@inheritDoc}
	 */
	public List<FileInfo> getAttachments() {
		return Collections.emptyList();
	}
}
