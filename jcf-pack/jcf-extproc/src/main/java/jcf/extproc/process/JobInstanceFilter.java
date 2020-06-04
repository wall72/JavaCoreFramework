package jcf.extproc.process;


/**
 * Job instance 필터.
 * <p>
 * 디렉토리 삭제시 특정 job instance id를 삭제한다거나, 특정 날짜 이후에 완료된 것을 한다거나,
 * 실패한 것을 한다거나, 보존 처리되지 않은 것을 삭제한다거나 하는 로직을 지정할 수 있다.
 * 
 * @author setq
 *
 */
public interface JobInstanceFilter {

	boolean isIncluded(JobInstanceInfo jobInstance);

}
