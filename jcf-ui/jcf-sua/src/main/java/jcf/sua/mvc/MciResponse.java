package jcf.sua.mvc;

import java.util.List;
import java.util.Map;

import jcf.sua.SuaChannels;
import jcf.sua.dataset.AbstractDataSetStreamWriterStreamHandlerAdapter;
import jcf.sua.dataset.DataSetStreamWriter;
import jcf.sua.dataset.DataSetStreamWriterFactory;
import jcf.sua.dataset.StreamSource;
import jcf.upload.FileInfo;
import jcf.upload.handler.DownloadEventHandler;

/**
*
* 하향 채널을 사용하기 위한 편의 메소드를 정의한 인터페이스
*
* @author nolang
*
*/
public interface MciResponse {

	/**
	 *
	 * 서버에서 조회한 모델(bean)정보를 가지고 주어진 DatasetId로 Dataset을 생성하여 화면에 전송한다.
	 *
	 * @param <E>
	 * @param datasetId
	 * @param bean
	 */
	<E> void set(String datasetId, E bean);

	/**
	 *
	 * 서버에서 조회한 모델(bean)정보를 가지고 주어진 DatasetId로 Dataset을 생성하여 화면에 전송한다.
	 *
	 * @param <E>
	 * @param datasetId
	 * @param bean
	 * @param type
	 */
	<E> void set(String datasetId, E bean, Class<E> type);

	/**
	 *
	 * 서버에서 조회한 모델(E)을 담고 있는 List 정보를 주어진 DatasetId로 Dataset 을 생성하여 화면에 전송한다.
	 *
	 * @param <E>
	 * @param datasetId
	 * @param listOfModel
	 */
	<E> void setList(String datasetId, List<E> listOfModel);

	/**
	 *
	 * 서버에서 조회한 모델(E)을 담고 있는 List 정보를 주어진 DatasetId로 Dataset 을 생성하여 화면에 전송한다.
	 *
	 * @param <E>
	 * @param datasetId
	 * @param listOfModel
	 * @param type
	 */
	<E> void setList(String datasetId, List<E> listOfModel, Class<E> type);

	/**
	 *
	 * 서버에서 조회한 데이터을 담고 있는 List 정보를 주어진 DatasetId로 Dataset 을 생성하여 화면에 전송한다.
	 *
	 * @param datasetId
	 * @param mapList
	 */
	void setMapList(String datasetId, List<? extends Map<String, ?>> mapList);

	/**
	 *
	 * 서버에서 조회한 정보를 가지고 주어진 DatasetId로 Dataset을 생성하여 화면에 전송한다.
	 *
	 * @param datasetId
	 * @param map
	 */
	void setMap(String datasetId, Map<String, ?> map);

	/**
	 *
	 * 성공 message를 화면으로 보낸다. 여러 message를 add할 수 있으며 List형태로 화면으로 전달된다.
	 *
	 * @param message
	 */
	void addSuccessMessage(String message);

	/**
	 *
	 * key, value로 표현 되는 param를 화면으로 전달할 시 key값과 value 값을 지정하여 화면에 전송한다.
	 *
	 * @param paramName
	 * @param paramValue
	 */
	void addParam(String paramName, String paramValue);

	/**
	 *
	 * WebFlow Channel(JSP View일 경우)에서 사용하며, 출력될 페이지를 ViewName을 통하여 지정할 수 있다.
	 *
	 * 예) viewName이 home이면 {@link InternalResourceViewResolver} 설정에서의 prefix와 surfix에 의하여 WEB-INF/JSP/home.jsp를 찾게된다.
	 *
	 * @param viewName
	 */
	void setViewName(String viewName);

	/**
	 *
	 * 비대칭 채널 (상향 채널과 하향 채널이 다른 경우) 처리시 사용되며, 설정한 값으로  채널을 강제 변경한다.
	 *
	 * @param channel
	 */
	void setDownStreamChannel(SuaChannels channel);

	/**
	 *
	 * 다운로드할 파일을 설정한다.
	 *
	 * @param fileInfo
	 */
	void setDownloadFile(FileInfo fileInfo);

	/**
	 *
	 * 다운로드 처리 정책을 적용하여, 파일을 다운로드한다.
	 *
	 * @param eventHandler
	 * @param fileInfo
	 */
	void setDownloadFile(DownloadEventHandler eventHandler, FileInfo fileInfo);

	/**
	 *
	 * 스트리밍 처리
	 *
	 * @param <T>
	 * @param streamSource
	 * @param streamHandler
	 */
	@Deprecated
	<T> void stream(StreamSource<T> streamSource, AbstractDataSetStreamWriterStreamHandlerAdapter<T> streamHandler);

	/**
	 *
	 * 기본으로 제공하는 대용량 처리 채널을 반환한다.
	 *
	 * @return
	 */
	DataSetStreamWriter getStreamWriter();

	/**
	 *
	 * 사용자 정의 대용량 처리 채널을  생성하여 반환한다.
	 *
	 * @param factory
	 * @return
	 */
	DataSetStreamWriter getStreamWriter(DataSetStreamWriterFactory factory);
}
