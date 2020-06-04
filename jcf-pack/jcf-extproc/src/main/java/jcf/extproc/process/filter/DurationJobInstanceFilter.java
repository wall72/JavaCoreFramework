package jcf.extproc.process.filter;

import jcf.extproc.exception.ExternalProcessException;
import jcf.extproc.process.JobInstanceInfo;

/**
 * 수행시간 범위에 의한 JobInstance 필터.
 * 
 * @author setq
 *
 */
public class DurationJobInstanceFilter extends AbstractJobInstanceFilter {

	private static final long serialVersionUID = -7582309921405144357L;
	private long min;
	private long max;
	
	public DurationJobInstanceFilter(long min, long max) {
		setDuration(min, max);
	}
	
	public void setDuration(long min, long max) {
		if (min == 0 && max == 0) throw new ExternalProcessException("range is null");
		this.min = min;
		this.max = max;
	}
	
	public boolean isIncluded(JobInstanceInfo jobInstance) {
		if (min == 0 && max == 0) throw new ExternalProcessException("range is null");

		if (min != 0 && max != 0) {
			return min <= jobInstance.getDuration() && jobInstance.getDuration() < max;
			
		} else if (min == 0) {
			return jobInstance.getDuration() < max;
			
		} else {
			return min <= jobInstance.getDuration();
		}
	}

}
