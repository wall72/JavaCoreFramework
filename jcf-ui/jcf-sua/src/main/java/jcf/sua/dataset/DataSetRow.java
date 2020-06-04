package jcf.sua.dataset;

/**
*
* 데이터셋의 행 정보
*
* @author nolang
*
*/
public interface DataSetRow {

	/**
	 *
	 * 지정한 컬럼에 값을 추가한다.
	 *
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 */
	DataSetRow add(String fieldName, Object fieldValue);

	/**
	 *
	 * 지정한 컬럼의 값을 반환한다.
	 *
	 * @param fieldName
	 * @return
	 */
	Object get(String fieldName);

	/**
	 *
	 * 행의 상태 정보를 반환한다.
	 *
	 * @return
	 */
	String getRowStatus();

	/**
	 *
	 * 행의 상태 정보를 설정한다.
	 *
	 * @param rowStatus
	 */
	void setRowStatus(String rowStatus);

	/**
	 *
	 * 수정되기 전의 행정보를 설정한다.
	 *
	 * @param savedDataSetRow
	 */
	void setOrgDataSetRow(DataSetRow savedDataSetRow);

	/**
	 *
	 * 수정되기 전의 행정보를 반환한다.
	 *
	 * @param savedDataSetRow
	 */
	DataSetRow getOrgDataSetRow();
}