package jcf.extproc.fileaccess;

import java.io.File;

import jcf.extproc.process.JobInstanceInfo;

/**
 * 각 프로세스 인스턴스의 working directory에 발생한 산출 파일들의 목록 조회 및 스트림 복사. 
 * 
 * @author setq
 *
 */
public interface FileAccess {

	File get(JobInstanceInfo instanceInfo, String fileName);

	/**
	 * working directory 내의 파일의 상대경로 목록 반환.
	 * @param instanceInfo
	 * @return
	 */
	Iterable<FileInfo> list(JobInstanceInfo instanceInfo);
	
	/**
	 * 로그 파일은 항상 default workDirectory에 존재하므로 Job 및 실행된 JobInstance의 workDirectory 지정과는
	 * 무관하게 일관된 규칙에 의해 위치가 지정되므로 로그파일을 가져오는 메소드는 별도로 취급.
	 *  
	 * @param instanceInfo
	 * @return
	 */
	File getLogFile(JobInstanceInfo instanceInfo);
	
}
