package jcf.sua.mvc.file.operator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcf.upload.FileInfo;
import jcf.upload.MultiPartInfo;

/**
 * 멀티파트 업로드 및 다운로드 요청을 처리
 *
 * @author nolang
 *
 */
public interface FileOperator {

	/**
	 * 멀티파트 요청인지 판단한다.
	 *
	 * @param request
	 * @return
	 */
	boolean isMultiPartRequest(HttpServletRequest request);

	/**
	 * 멀티파트 파일 업로드 요청에 대하여 단순 필드는 attributes로, 파일은 UploadEventHandler에 의해
	 * 로컬 파일 시스템으로 저장하거나 하는 등의 작업을 함.
	 *
	 * @param request
	 * @return 멀티파트 폼 필드 및 파일 레퍼런스 정보.
	 */
	MultiPartInfo handleMultiPartRequest(HttpServletRequest request);

	/**
	 * 파일을 클라이언트로 전송한다.
	 *
	 * @author nolang
	 *
	 */
	void sendFileStream(HttpServletRequest request, HttpServletResponse response, FileInfo fileInfo);
}
