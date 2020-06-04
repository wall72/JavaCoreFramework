package jcf.extproc.execution;

import java.util.List;

import jcf.extproc.process.ExternalProcess;

/**
 * @author setq
 *
 */
public interface ExternalProcessContextManager {
	
	/**
	 * 작업 등록정보 등록/갱신.
	 * <p>
	 * 등록된  외부 프로세스 컨텍스트가 있는 경우 작업 등록정보만 변경하고 실행 프로세스는 변경하지 않는다.
	 * 없는 경우 새로운 컨택스트를 만들어 등록. 
	 * 
	 * @param externalProcess
	 */
	void put(ExternalProcess externalProcess);

	/**
	 * 외부 프로세스 컨텍스트 제거.
	 * 작업 등록정보는 사라지고 실행 프로세스는 종료시까지 유지.
	 * 
	 * @param jobName
	 * @return
	 */
	boolean remove(String jobName);

	ExternalProcessContext get(String jobName);

	List<String> getJobNames();

}
