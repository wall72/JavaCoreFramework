package jcf.sua.mvc;

import java.util.List;
import java.util.Map;

import jcf.data.GridData;
import jcf.sua.mvc.file.MciPersistenceManager;
import jcf.upload.FileInfo;
import jcf.upload.handler.UploadEventHandler;

/**
 *
 * 상향 채널을 사용하기 위한 편의 메소드를 정의한 인터페이스
 *
 * @author Jeado Ko
 *
 */
public interface MciRequest {

	/**
	 *
	 * Request URI를 반환한다.
	 *
	 * @return
	 */
	String getRequestURI();

	/**
	 *
	 * Dataset 아이디의 목록을 List<String>으로 반환한다.
	 *
	 * @return
	 */
	List<String> getDataSetIds();

	/**
	 *
	 * Dataset 아이디에 해당하는 데이터를  모델 형태로 변환하여 반환하며, 만약 clazz가 Map의 구현체이면 getMap 메소드와 같은 역할을 하게 된다.
	 *
	 * @param <E>
	 * @param datasetId DATASET ID
	 * @param clazz     Return Type
	 * @return UserDefinedModel
	 */
	<E> E get(String datasetId, Class<E> clazz);

	/**
	 *
	 * Dataset 아이디에 해당하는 데이터를  모델 형태로 변환하여 반환하며, 주어진 표현식에 의거한 유효성 체크를 수행한다.
	 *
	 * @param <E>
	 * @param datasetId DATASET ID
	 * @param clazz     Return Type
	 * @param filter    유효성 체크를 위한 Expression (ex. id+,name,description : id=required, name, description 조회)
	 * @return
	 */
	<E> E get(String datasetId, Class<E> clazz, String filter);

	/**
	 *
	 * Dataset 아이디에 해당하는 데이터를  모델 형태로 변환하여 반환
	 *
	 * @param <E>
	 * @param datasetId
	 * @param rowNum
	 * @param clazz
	 * @return UserDefinedModel
	 */
	<E> E get(String datasetId, int rowNum, Class<E> clazz);

	/**
	 *
	 * Dataset 아이디에 해당하는 데이터를  모델 형태로 변환하여 반환하며, 주어진 표현식에 의거한 유효성 체크를 수행한다.
	 *
	 * @param <E>
	 * @param datasetId DATASET ID
	 * @param rowNum    rowNum번째 행의 데이터를 반환
	 * @param clazz     Return Type
	 * @param filter    유효성 체크를 위한 Expression (ex. id+,name,description : id=required, name, description 조회)
	 * @return
	 */
	<E> E get(String datasetId, int rowNum, Class<E> clazz, String filter);

	/**
	 *
	 * 요청한 Dataset이 행의 상태정보 (rowStatus(insert, update, delete와 같은 정보))를 포함하고 있다면
	 * 각 행에 해당하는 상태정보를 포함하고 있는 GridData를 반환한다.
	 *
	 * @param <E>
	 * @param datasetId
	 * @param clazz
	 * @return Map
	 */
	<E> GridData<E> getGridData(String datasetId, Class<E> clazz);

	/**
	 *
	 * GridData를 생성하며, 주어진 표현식에 의거한 유효성 체크를 수행한다.
	 *
	 * @param <E>
	 * @param datasetId DATASET ID
	 * @param clazz     Return Type
	 * @param filter    유효성 체크를 위한 Expression (ex. id+,name,description : id=required, name, description 조회)
	 * @return
	 */
	<E> GridData<E> getGridData(String datasetId, Class<E> clazz, String filter);

	/**
	 *
	 * Dataset 아이디에 해당하는 데이터를  Map 형태로 변환하여 반환
	 *
	 * @param datasetId
	 * @return Map
	 */
	Map<String, ?> getMap(String datasetId);

	/**
	 *
	 * Dataset 아이디에 해당하는 데이터를  Map 형태로 변환하여 반환하며, 주어진 표현식에 의거한 유효성 체크를 수행한다.
	 *
	 * @param datasetId DATASET ID
	 * @param filter    유효성 체크를 위한 Expression (ex. id+,name,description : id=required, name, description 조회)
	 * @return
	 */
	Map<String, ?> getMap(String datasetId, String filter);

	/**
	 *
	 * Dataset 아이디에 해당하는 데이터를  Map 형태로 변환하여 반환
	 *
	 * @param datasetId
	 * @param rowNum
	 * @return Map
	 */
	@Deprecated
	Map<String, ?> getMap(String datasetId, int rowNum);

	/**
	 *
	 * Dataset 아이디에 해당하는 Dataset 을 Map을 담고 있는 리스트로 반환한다.
	 *
	 * @param datasetId
	 * @return
	 */
	List<Map<String, String>> getMapList(String datasetId);

	/**
	 *
	 * Dataset 아이디에 해당하는 Dataset 을 Map을 담고 있는 리스트로 반환하며, 주어진 표현식에 의거한 유효성 체크를 수행한다.
	 *
	 * @param datasetId DATASET ID
	 * @param filter    유효성 체크를 위한 Expression (ex. id+,name,description : id=required, name, description 조회)
	 * @return
	 */
	List<Map<String, String>> getMapList(String datasetId, String filter);

	/**
	 *
	 * 요청 받은 파라미터 MAP의 KEY값들을 List<String>형태로 반환하다.
	 *
	 * @return
	 */
	List<String> getParameterNames();

	/**
	 *
	 * 요청 받은 파라미터 MAP을 반환한다.
	 *
	 * @return Map
	 */
	Map<String, Object> getParam();

	/**
	 *
	 * 요청 받은 파라미터 MAP을 모델 클래스로 변환하여 반환한다.
	 *
	 * @param <T>
	 * @param type
	 * @return
	 */
	<T> T getParam(Class<T> type);

	/**
	 *
	 * 요청 받은 파라미터 MAP을 모델 클래스로 변환하여 반환하며, 주어진 표현식에 의거한 유효성 체크를 수행한다.
	 *
	 * @param <T>
	 * @param type   Return Type
	 * @param filter 유효성 체크를 위한 Expression (ex. id+,name,description : id=required, name, description 조회)
	 * @return
	 */
	<T> T getParam(Class<T> type, String filter);

	/**
	 *
	 * 요청 받은 특정 파라미터 값을 반환한다.
	 *
	 * @param paramName
	 * @return
	 */
	String getParam(String paramName);

	/**
	 *
	 * 요청 받은 특정 파라미터 값을 반환한다.
	 *
	 * @param paramName
	 * @param defaultValue paramName으로 조회된 값이 null 이거나 empty string인 경우 defaultValue로 치환되어 반환됨.
	 * @return
	 */
	String getParam(String paramName, String defaultValue);

	/**
	 *
	 * 요청 받은 특정 파라미터 값을 반환한다.
	 *
	 * @param paramName
	 * @return
	 */
	String[] getParamArray(String paramName);

	/**
	 *
	 * 요청에 포함된 첨부파일을 반환한다.
	 *
	 * @return
	 */
	@Deprecated
	List<FileInfo> getAttachments();

	/**
	 *
	 * 화면으로 부터 전송된 파일을 처리한다.
	 *
	 * @param dispatcher
	 * @param persistence
	 */
	void handleIfMultipart(UploadEventHandler dispatcher, MciPersistenceManager persistence);

}
