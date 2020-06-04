package jcf.data.handler;

/**
*
* 대용량 데이터의 스트리밍 처리시 사용된다.
*
* @author nolang
*
* @param <T>
*/
public interface StreamHandler<T> {

	/**
	 *
	 * 스트리밍 헤더 쓰기.
	 *
	 */
	void open() ;

	/**
	 *
	 * 스트리밍 마무리.
	 *
	 */
	void close();

	/**
	 *
	 * 행 단위 처리.
	 *
	 * @param valueObject 행 데이터.
	 */
	void handleRow(T valueObject);
}
