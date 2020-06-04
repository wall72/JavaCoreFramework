package jcf.extproc.execution;

import java.util.List;

import jcf.extproc.process.JobInstanceFilter;
import jcf.extproc.process.JobInstanceInfo;

/**
 * 작업 인스턴스 관리.
 * 
 * 인스턴스가 없는 경우에 대한 조회는 NULL을 반환.
 * 
 * @author setq
 *
 */
public interface JobInstanceManager {
	
	/**
	 * 인스턴스 필터에 의해 인스턴스 기록 삭제. (단 실행중인 인스턴스는 제외.)
	 * @param jobInstanceFilter
	 */
	void deleteInstances(JobInstanceFilter jobInstanceFilter);

	/**
	 * 
	 * @param jobInstanceFilter null이면 모든 인스턴스를 가져온다.
	 * @return
	 */
	List<JobInstanceInfo> getList(JobInstanceFilter jobInstanceFilter);

	/**
	 * 
	 * @param instanceId
	 * @return 없으면 null
	 */
	JobInstanceInfo get(long instanceId);

	void update(JobInstanceInfo jobInstanceInfo);

	/**
	 * @return 없으면 null
	 */
	JobInstanceInfo getLatest();

	JobInstanceInfo create(JobInstanceInfo sourceJobInstanceInfo);

}