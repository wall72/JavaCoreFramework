package jcf.upload.handler;

import javax.servlet.http.HttpServletRequest;

import jcf.upload.FileInfo;
import jcf.upload.MultiPartInfo;
import jcf.upload.persistence.PersistenceManager;



/**
 * 업로드 이벤트에 대한 처리 인터페이스.
 * <p>
 * 예를 들어 게시판의 첨부파일 업로드 처리를 할 때의 첨부파일 묶음 크기 제한.
 * 
 * <p>
 * MultiPart 스트림을 처리한 후
 * <p>
 * 2. 첨부파일 이외의 내용들에 대하여 데이터베이스에 저장하는등의 동작을 처리
 * 
 */
public interface UploadEventHandler {

	/**
	 * 최대 업로드 사이즈 지정. (주 : 파일 단위가 아닌 멀티파트 요청 단위임)
	 * <p>
	 * 참고: 기본값은 receiver에서 설정 가능하지만, 여기서 0이 아닌 값으로 설정하면 이 값으로 오버라이드 됨.
	 * 
	 * @return 멀티파트 업로드 사이즈 in bytes. 
	 */
	long getMaxUploadSize();
	
	/**
	 * 어느 폴더로 업로드할 것인지 지정. 
	 * <p>
	 * 이 값은 PersistenceManager에서 folder 이름으로 사용된다.
	 * @return  PersistenceManager에서 사용할 folder 이름
	 */
	String getFolder(HttpServletRequest request);

	/**
	 * 업로드 후처리.
	 * <p>
	 * 
	 * @param folder
	 * @param info 폼 필드 및 파일 정보들.
	 * @param persistenceManager 받은 파일에 대한 관리자.
	 */
	void postprocess(String folder, MultiPartInfo info, PersistenceManager persistenceManager);

	/**
	 * 멀티파트 업로드 요청 안의 개별 파일에 대한 Persistence 이름을 결정. 
	 * <p> 
	 * 다운로드 서블릿 구현시 
	 * PersistenceManager에 저장할 때의 folder/callName과 불러올 때의 folder/callName은 동일해야 함.
	 * 
	 * @param folder
	 * @param fileInfo
	 * @return 멀티파트 업로드 요청 안의 개별 파일에 대한 Persistence 이름
	 */
	String createFileNameIfAccepted(String folder, FileInfo fileInfo);

	/**
	 * folder에 대한 스토리지 사용 준비.
	 * <p>
	 * 멀티파트 파일 요청이므로 여러 개의 파일을 한 요청에서 업로드할 수 있으므로 해당 folder에 여러 개의 파일을
	 * 중복해서 올리는 등의 예외 사항을 처리해야 함.
	 * <p> 
	 * 예) 디렉토리 생성 또는 기존 잔존 파일 삭제 등.  
	 * @param persistenceManager
	 * @param folder
	 */
	void prepareStorage(PersistenceManager persistenceManager, String folder);

}
