package jcf.sua.dataset;

import java.util.List;
import java.util.Map;

import jcf.upload.FileInfo;

/**
*
* 상향 채널의 메소드를 정의한 인터페이스
*
* @author nolang
*/
public interface DataSetReader {

	/**
	 *
	 * 파라미터(QueryString or Variable)를 조회한다.
	 *
	 * @return
	 */
	public Map<String, Object> getParamMap();

	/**
	 *
	 * 요청에서 조회한 각 데이터셋의 아이디를 반환한다.
	 *
	 * @return
	 */
	List<String> getDataSetIdList();

	/**
	 *
	 * 지정한 데이터셋의 모든 행을 반환한다.
	 *
	 * @param dataSetId
	 * @return
	 */
	List<DataSetRow> getDataSetRows(String dataSetId);

	/**
	 *
	 * 지정한 데이터 셋의 컬럼 정보를 반환한다.
	 *
	 * @param dataSetId
	 * @return
	 */
	List<DataSetColumn> getDataSetColumns(String dataSetId);

	/**
	 * 첨부파일 조회
	 *
	 * @return
	 */
	List<FileInfo> getAttachments();

}