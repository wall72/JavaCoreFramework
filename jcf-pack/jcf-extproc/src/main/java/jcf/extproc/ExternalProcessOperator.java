package jcf.extproc;


import java.util.List;
import java.util.Map;

import jcf.extproc.process.ExternalProcess;
import jcf.extproc.process.JobInstanceFilter;
import jcf.extproc.process.JobInstanceInfo;

/**
 * 외부프로세스 실행기.
 * <p>
 * 각 외부 프로세스는 이름이 주어지며, 실행하는 동안 소유권을 갖고 있다.
 * 그러므로 동일한 이름을 가진 프로세스가 동시 실행은 되지 않는다.
 * 만약 동일한 프로그램을 동시 실행해야할 일이 있으면 다른 이름으로 등록하면 별도로 실행할 수 있다.
 * 
 * @author setq
 *
 */
public interface ExternalProcessOperator {
	
	/**
	 * 외부 프로세스를 등록한다.
	 * 
	 * @param externalProcess 외부 프로세스.
	 */
	void register(ExternalProcess externalProcess);

	/**
	 * 외부 프로세스를 등록에서 제거한다.
	 * 
	 * @param jobName; 외부 프로세스 이름.
	 * @return job이 삭제된 경우 true, 그 외의 경우 false (job이 없거나 삭제되지 않은 경우)
	 */
	boolean delete(String jobName);

	/**
	 * 외부 프로세스 실행 이력들을 (선택) 삭제한다.
	 * @param jobName
	 * @param jobInstanceFilter
	 */
	void deleteJobInstances(String jobName, JobInstanceFilter jobInstanceFilter);
	
	/**
	 * Job (외부 프로세스)을 실행시킨다. 스케쥴에 의한 실행이 아니라면 영구저장되지 않는 임의의 환경변수를 지정할 수도 있다.
	 * @param jobName 작업 이름.
	 * @param adhocEnvironment 영구저장하지 않는 임의 환경변수.
	 * @return long 인스턴스 아이디
	 */
	long execute(String jobName, Map<String, String> adhocEnvironment);

	/**
	 * Job (외부 프로세스)을 실행시킨다. 스케쥴에 의한 실행이 아니라면 영구저장되지 않는 임의의 환경변수를 지정할 수도 있다.
	 * @param jobName 작업 이름.
	 * @param adhocEnvironment 영구저장하지 않는 임의 환경변수.
	 * @param sourceJobInstanceInfo job을 수행한 사용자명 및 인스턴스 설명 정보. (나머지는 무시됨)
	 * @return long 인스턴스 아이디
	 */
	long execute(String jobName, Map<String, String> adhocEnvironment, JobInstanceInfo sourceJobInstanceInfo);
	
	/**
	 * Job이 현재 실행중인지 여부.
	 * 
	 * @param jobName
	 * @return
	 */
	boolean isRunning(String jobName);

	/**
	 * 현재 실행되고 있는 Job을 중단시킨다. 실행되고 있지 않은 작업에 대해 수행하면 아무 동작을 하지 않는다.
	 * 
	 * @param jobName
	 * @return  현재 실행되고 있는 작업이 없으면 false. 작업이 있고 중단시켰으면 true.
	 */
	boolean destroy(String jobName);

	/**
	 * 베이스 디렉토리에 등록되어있는 Job 목록을 가져온다.
	 * @return 외부 프로세스 이름의 목록.
	 */
	List<String> getJobNames();
	

	/**
	 * 마지막 실행 인스턴스 정보를 가져온다.
	 * @param jobName
	 * @return 인스턴스가 없으면 null
	 */
	JobInstanceInfo getLastInstanceInfo(String jobName);

	/**
	 * 외부 프로세스 등록정보를 가져온다.
	 * @param jobName
	 * @return
	 */
	ExternalProcess getJob(String jobName);
	
	/**
	 * 외부 프로세스 실행 인스턴스 아이디를 가져온다.
	 * @param jobName
	 * @param jobInstanceFilter
	 * @return
	 */
	List<Long> getJobInstanceIdList(String jobName, JobInstanceFilter jobInstanceFilter);
	
	/**
	 * 외부 프로세스 실행 인스턴스 정보를 가져온다.
	 * @param jobName
	 * @param jobInstanceFilter
	 * @return 없으면 null
	 */
	JobInstanceInfo getJobInstanceInfo(String jobName, long instanceId);

	/**
	 * 외부 프로세스 실행 인스턴스 정보를 수정한다.
	 * 
	 * @param jobInstanceInfo
	 */
	void updateJobInstanceInfo(JobInstanceInfo jobInstanceInfo);
	
}