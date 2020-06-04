package jcf.extproc.process.filter;

import java.util.Date;

import jcf.extproc.exception.ExternalProcessException;
import jcf.extproc.process.JobInstanceInfo;

/**
 * 날짜 범위로 필터링.
 * 시작 날짜 <= 검사대상 < 끝 날짜 이면 포함.
 * 시작 날짜나 끝 날짜를 주지 않으면 해당 조건 검사 안함.
 * 
 * @author setq
 *
 */
public class DateJobInstanceFilter extends AbstractJobInstanceFilter {

	private static final long serialVersionUID = -5927141222308090773L;
	private Date begin;
	private Date end;
	
	public DateJobInstanceFilter(Date begin, Date end) {
		setRange(begin, end);
	}
	
	public void setRange(Date begin, Date end) {
		if (begin == null && end == null) throw new ExternalProcessException("invalid argument : null range");
		this.begin = begin;
		this.end = end;
	}
	
	public boolean isIncluded(JobInstanceInfo jobInstance) {
		if (begin == null && end == null) throw new ExternalProcessException("invalid argument : null range");

		if (begin != null && end != null) {
			return jobInstance.getTriggerDate().compareTo(begin) >= 0 &&
			jobInstance.getTriggerDate().compareTo(end) < 0;
			
		} else if (begin == null) {
			return jobInstance.getTriggerDate().compareTo(end) < 0;
			
		} else {
			return jobInstance.getTriggerDate().compareTo(begin) >= 0;
		}
			
	}

}
