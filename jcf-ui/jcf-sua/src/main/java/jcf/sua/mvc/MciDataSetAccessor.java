package jcf.sua.mvc;

import java.util.List;
import java.util.Map;

import jcf.sua.dataset.DataSet;
import jcf.sua.dataset.DataSetConverter;
import jcf.upload.FileInfo;

import org.springframework.web.servlet.ModelAndView;

/**
*
* 클라이언트로 전송할 데이터에 대한 연산을 지원한다.
*
* @author nolang
*
*/
public interface MciDataSetAccessor {

	/**
	 *
	 * 데이터셋을 반환한다.
	 *
	 * @return
	 */
	Map<String, DataSet> getDataSetMap();

	/**
	 *
	 * Parameter를 반환한다.
	 *
	 * @return
	 */
	Map<String, String> getParams();

	/**
	 *
	 * 성공메세지를 반환한다.
	 *
	 * @return
	 */
	List<String> getSuccessMessags();

	/**
	 *
	 * 예외메세지를 반환한다.
	 *
	 * @return
	 */
	String getExceptionMessage();

	/**
	 *
	 * 예외 메세지를 설정한다.
	 *
	 * @param exceptionMessage
	 */
	void setExceptionMessage(String exceptionMessage);

	/**
	 *
	 * 화면 전환이 요구되는 요청 (표준웹, WEBFLOW)에서 사용되며, {@link InternalResourceViewResolver} 에서 사용될 ModelAndView를 반환한다.
	 *
	 * @return
	 */
	ModelAndView getModelAndView();

	/**
	 *
	 * 다운로드할 파일을 반환한다.
	 *
	 * @return
	 */
	FileInfo getDownloadFile();

	/**
	 *
	 * 파일 처리/스트리밍 처리인 경우 True
	 *
	 * @return
	 */
	boolean isFileProcessing();

	/**
	 *
	 * 데이터셋 컨버터 지정
	 *
	 * @param converter
	 */
	void setDataSetConverter(DataSetConverter converter);
}
