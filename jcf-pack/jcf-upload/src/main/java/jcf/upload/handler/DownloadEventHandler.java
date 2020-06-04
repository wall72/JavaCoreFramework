package jcf.upload.handler;

import jcf.upload.FileInfo;


/**
 * 다운로드 이벤트에 대한 처리 인터페이스.
 * <p>
 * 예를 들어 파일 다운로드 처리를 할 때의 파일 크기 제한 및 관리 정책 적용
 * <p>
 *
 */
public interface DownloadEventHandler {

	/**
	 *
	 * 파일 다운로드 전처리.
	 *
	 * @param fileInfo
	 */
	void preprocess(FileInfo fileInfo);

	/**
	 * 클라이언트에 디폴트로 제공될 파일이름 생성.
	 *
	 * @param fileInfo
	 * @return 클라이언트에 디폴트로 제공될 파일이름
	 */
	String createFileName(FileInfo fileInfo);

}
