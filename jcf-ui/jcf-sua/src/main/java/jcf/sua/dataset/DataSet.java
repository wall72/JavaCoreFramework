package jcf.sua.dataset;

import java.util.Map;

import jcf.data.RowStatus;

/**
*
* 채널을 통해 생성된 데이터를 표준화된 형태로 변환하여 저장한다.
*
* @author nolang
*
*/
public interface DataSet {

	/**
	 *
	 * 데이터셋의 ID를 반환한다.
	 *
	 * @return
	 */
	String getId();

	/**
	 *
	 * 데이터셋이 가지는 컬럼의 크기를 반환한다.
	 *
	 * @return
	 */
	int getColumnCount();

	/**
	 *
	 * 데이터셋에 포함되어 있는 ROW의 개수를 반환한다.
	 *
	 * @return
	 */
	int getRowCount();

	/**
	 *
	 * 데이터셋의 row 번째 행의 RowStatus(NORMAL/INSERT/UPDATE/DELETE)를 반환한다.
	 *
	 * @param row row number
	 * @return
	 */
	RowStatus getRowStatus(int row);

	/**
	 *
	 * 데이터셋의 row번째 행의 데이터를 반환한다.
	 *
	 * @param row
	 * @return
	 */
	DataSetRow getDataSetRow(int row);

	/**
	 *
	 * 데이터셋의 col 번째 컬럼 정보를 반환한다.
	 *
	 * @param col
	 * @return
	 */
	DataSetColumn getDataSetColumn(int col);

	/**
	 *
	 * 데이터셋의 row번째 행을 주어진 타입의 객체로 변환하여 반환한다.
	 *
	 * @param <E>
	 * @param clazz 리턴받을 객체의 타입
	 * @param row row number
	 * @return
	 */
	<E> E getBean(Class<E> clazz, int row);

	/**
	 *
	 * 데이터셋의 row번째 행을 주어진 타입의 객체로 변환하여 반환하며, 주어진 filter expression과 관련한 validation 연산을 수행한다.
	 *
	 * @param <E>
	 * @param clazz  리턴받을 객체의 타입
	 * @param row row number
	 * @param filter 유효성 체크를 위한 Expression (ex. id+,name,description : id=required, name, description 조회)
	 * @return
	 */
	<E> E getBean(Class<E> clazz, int row, String filter);

	/**
	 *
	 * 데이터셋의 row 번째 행의 변경된 행(RowStatus=UPDATE)인 경우, 변경되기 이전의 정보를 반환한다.
	 *
	 * @param <E>
	 * @param clazz 리턴받을 객체의 타입
	 * @param row row number
	 * @return UX에서 지원하지 않는 기능인 경우 null
	 */
	<E> E getOrgDataBean(Class<E> clazz, int row);

	/**
	 *
	 * 데이터셋에 행을 추가한다.
	 *
	 * @param bean
	 * @param clazz TODO
	 */
	void addRowBean(Object bean, Class<?> clazz);

	/**
	 *
	 * 데이터셋에 행을 추가한다.
	 *
	 * @param map
	 * @param clazz TODO
	 */
	void addRowMap(Map<String, ?> map, Class<?> clazz);
}