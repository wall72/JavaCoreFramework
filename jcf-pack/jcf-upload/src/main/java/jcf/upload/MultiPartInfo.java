package jcf.upload;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * HTTP 파일 업로드에서의 Multipart Form 해석 정보 저장.
 *
 * 업로드 후 결과 화면 렌더링 정보 - 모델&뷰 자료구조 모방
 * 다운로드 - 커스텀 핸들러는 커스터마이징 영역이므로 변경 사항 없음.
 * 	커스터마이징 영역에서는 커스텀 핸들러 리스트를 등록하고 해당 URL별 로직을 이용하도록 한다.
 * 	예를 들면 데이터베이스 조회 결과를 파일로 다운로드한다든지 하는 경우.
 * 파일 저장 구조 - URN 그리고 파일 카운트 말고 파일 정보 전체 전달.
 *
 * @author Administrator
 */
public final class MultiPartInfo {

	private final Map<String, Object> attributes;
	private final List<FileInfo> fileInfos;

	/**
	 * @param attributes map of attributes
	 * @param fileInfos list of {@link FileInfo}
	 */
	public MultiPartInfo(Map<String, Object> attributes, List<FileInfo> fileInfos) {
		this.attributes = Collections.unmodifiableMap(attributes);
		this.fileInfos = Collections.unmodifiableList(fileInfos);
	}

	/**
	 * @return list of {@link FileInfo}
	 */
	public List<FileInfo> getFileInfos() {
		return fileInfos;
	}

	/**
	 * @return map of attributes
	 */
	public Map<String, Object> getAttributes() {
		return attributes;
	}

}
