package jcf.data;

/**
*
* GridData의 일괄 처리 연산 수행에서 개별 연산 수행을 정의한 인터페이스. 일반적으로 Service 클래스 내부에서 구현하여
* 사용하며, 수행 중 개별 연산 수행을 정의하고 싶은 경우에 확장하여 사용한다.
*
* @author nolang
*
* @param <E>
*
*/
public interface RowStatusCallback<E> {

	/**
	 *
	 * NORMAL ROW 처리 작업 수행.
	 *
	 * @param record 추가된 행 데이터
	 * @param rowNum row number
	 */
	void normal(E record, int rowNum);

	/**
	 *
	 * ROW INSERT 수행.
	 *
	 * @param record 추가된 행 데이터
	 * @param rowNum row number
	 */
	void insert(E record, int rowNum);

	/**
	 *
	 * ROW UPDATE 수행.
	 *
	 * @param newRecord 변경된 행 데이터
	 * @param oldRecord 변경되기 이전의 행 데이터
	 * @param rowNum row number
	 */
	void update(E newRecord, E oldRecord, int rowNum);

	/**
	 *
	 * ROW DELETE 수행.
	 *
	 * @param record 삭제된 행 데이터
	 * @param rowNum row number
	 */
	void delete(E record, int rowNum);

}
