package jcf.upload;

import javax.servlet.http.HttpServletRequest;

import jcf.upload.handler.UploadEventHandler;

/**
 * 멀티파트 파일 업로드 요청 처리.
 * <p>
 * 업무 로직은 UploadEventHandler 를 받아서 처리하도록 하여 업로드와 업무로직 서비스 인스턴스 분리함.
 *
 */
public interface MultiPartReceiver {

	/**
	 * 멀티파트 파일 업로드 요청에 대하여 단순 필드는 attributes로, 파일은 UploadEventHandler에 의해
	 * 로컬 파일 시스템으로 저장하거나 하는 등의 작업을 함.
	 * 
	 * @param request
	 * @param handler
	 * @return 멀티파트 폼 필드 및 파일 레퍼런스 정보.
	 */
	MultiPartInfo receive(HttpServletRequest request, UploadEventHandler handler);
	
}
