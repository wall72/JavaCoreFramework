package jcf.sua.dataset;

import javax.servlet.http.HttpServletResponse;

/**
*
* 사용자 정의 대용량 처리 스트리밍 채널을 생성하기 위한 Factory 클래스
*
* @author nolang
*
*/
public interface DataSetStreamWriterFactory {

	/**
	 *
	 * 사용자 정의 스트리밍 채널을 반환한다.
	 *
	 * @param response
	 * @return
	 */
	DataSetStreamWriter getStreamWriter(HttpServletResponse response);

}
