package jcf.sua.dataset;

/**
*
* 스트리밍 채널의 각 메소드를 정의한 인터페이스
*
* @author nolang
*
*/
public interface DataSetStreamWriter {

	/**
	 *
	 * 대용량 데이터 전송을 위한 채널을 열고, 데이터 전송에 필요한 초기 작업을 수행한다.
	 *
	 * @param dataSetId
	 * @param bufferSize
	 */
	void startStream(String dataSetId, int bufferSize);

	/**
	 *
	 * 데이터를 스트리밍 채널로 전송한다.
	 *
	 * @param data
	 */
	void addStreamData(Object data);

	/**
	 * 스트리밍 채널을 닫는다.
	 */
	void endStream();

}
