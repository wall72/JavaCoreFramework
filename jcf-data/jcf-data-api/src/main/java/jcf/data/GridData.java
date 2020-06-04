package jcf.data;

import java.util.List;

import jcf.data.RowStatus;

/**
*
* GRID 각 행의 상태 정보 및 행 데이터를 반환한다.
*
* @author nolang
*
* @param <E>
*
*/
public interface GridData<E> {

	/**
	 *
	 * GRID 행 데이터를 반환한다.
	 *
	 * @param rowIndex
	 * @return
	 */
	E get(int rowIndex);

	/**
	 *
	 * GRID의 전체 행 리스트를 반환한다.
	 *
	 * @return
	 */
	List<E> getList();

	/**
	 *
	 * GRID 각 행의 상태정보를 반환한다.
	 *
	 * @param rowIndex
	 * @return
	 */
	RowStatus getStatusOf(int rowIndex);

	/**
	 *
	 * GRID의 전체 행의 개수를 반환한다.
	 *
	 * @return
	 */
	int size();

	/**
	 *
	 * GRID 각 행에 대한 개별 연산을 수행한다.
	 *
	 * @param callback
	 */
	void forEachRow(RowStatusCallback<E> callback);

}