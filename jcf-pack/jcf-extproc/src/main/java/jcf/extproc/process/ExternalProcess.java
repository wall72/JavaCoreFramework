package jcf.extproc.process;


import java.io.Serializable;



/**
 * 
 * 외부 프로세스 정의
 * @author setq
 *
 */
public interface ExternalProcess extends Serializable {

	/**
	 * 작업 명. (파일시스템 디렉토리 이름).
	 * <p>
	 * 작업 명이 같은 것은 동시 수행이 되지 않음.
	 * <p>
	 * 디렉토리 구조로 여러 계층을 둘 수 있지만, 일부 계층을 공유하는 작업들 끼리는 계층 수준이 같아야 한다.
	 * 예를 들어  a/b/c 와 a/b/d 는 허용되지만 a/b는 계층이 다르기 때문에 디렉토리 관리상의 이유로 (삭제 할 경우 타 작업 훼손 등) 허용되지 않는다. 
	 * @return
	 */
	String getName();

	/**
	 * 작업 설명
	 * @return
	 */
	String getDescription();

	void accept(ExternalProcessVisitor visitor);

	/**
	 * 프로세스 유형에 따라 exit value가 아닌 콘솔 출력 내용으로 성공/실패 여부를 갈라야할 경우가 있다.
	 * 이 경우 특정 라인 검출시 실패로 해야하는 경우의 로직 제공.  
	 * @param line
	 * @return
	 */
	boolean checkForFailure(String line);

	/**
	 * 프로세스 등록자. 프로세스 실행시 사용자 명이 지정되지 않은 경우 등록자 값으로 들어가게 된다. (스케쥴러에 의한 트리거 등)
	 * @return
	 */
	String getUser();

	LogFileKeepingPolicy getLogFileKeepingPolicy();
	
	String getWorkDirectory();
}
