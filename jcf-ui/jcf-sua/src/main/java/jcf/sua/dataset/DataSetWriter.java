package jcf.sua.dataset;

import jcf.sua.mvc.MciDataSetAccessor;

/**
*
* 하향 채널의 메소드를 정의한 인터페이스
*
* @author nolang
*
*/
public interface DataSetWriter {

	/**
	 * 데이터를 클라이언트로 전송한다.
	 */
	void write();

	/**
	 *
	 * 전송할 데이터를 설정한다.
	 *
	 * @param accessor
	 */
	void setDataSetAccessor(MciDataSetAccessor accessor);
}
