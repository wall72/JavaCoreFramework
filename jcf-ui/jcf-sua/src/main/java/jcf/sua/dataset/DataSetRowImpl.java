package jcf.sua.dataset;

import java.util.HashMap;
import java.util.Map;

import jcf.data.RowStatus;
import jcf.sua.exception.MciException;

/**
*
* {@link DataSetRow} 의 구현체
*
* @author nolang
*/
public class DataSetRowImpl implements DataSetRow {

	private String rowStatus;
	private Map<String, Object> dataSetRowMap = new HashMap<String, Object>();

	/*
	 * 변경전 RowData
	 */
	private DataSetRow orgDataSetRow;

	/**
	 * {@inheritDoc}
	 */
	public DataSetRow add(String fieldName, Object fieldValue) {
		dataSetRowMap.put(fieldName, fieldValue);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object get(String fieldName) {
		Object value = dataSetRowMap.get(fieldName);

		if (value == null && value instanceof String) {
			value = "";
		}

		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getRowStatus() {
		return rowStatus;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setRowStatus(String rowStatus) {
		RowStatus rst = RowStatus.valueOf(RowStatus.class,
				rowStatus.toUpperCase());

		if (rst == null) {
			throw new MciException(String.format("지원하지 않는 RowStatus Type 입니다. - RowsStatus={%s}", rowStatus));
		}

		this.rowStatus = rowStatus;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setOrgDataSetRow(DataSetRow orgDataSetRow) {
		this.orgDataSetRow = orgDataSetRow;
	}

	/**
	 * {@inheritDoc}
	 */
	public DataSetRow getOrgDataSetRow() {
		return orgDataSetRow;
	}
}
