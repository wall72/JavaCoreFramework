package jcf.extproc.process.policy;

import java.util.Calendar;

import jcf.extproc.process.JobInstanceInfo;
import jcf.extproc.process.LogFileKeepingPolicy;

public abstract class AbstractCalendarBasedLogFileKeepingPolicy implements LogFileKeepingPolicy {

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + amount;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractCalendarBasedLogFileKeepingPolicy other = (AbstractCalendarBasedLogFileKeepingPolicy) obj;
		if (amount != other.amount)
			return false;
		return true;
	}


	private static final long serialVersionUID = 1L;
	
	private int amount;

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public AbstractCalendarBasedLogFileKeepingPolicy(int amount) {
		this.amount = amount;
	}
	
	public boolean isOutdated(JobInstanceInfo jobInstanceInfo,
			JobInstanceInfo currentJobInstanceInfo) {
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentJobInstanceInfo.getTriggerDate());
		
		return jobInstanceInfo.getTriggerDate().before(subtract(calendar, amount).getTime());
	}
	
	protected abstract Calendar subtract(Calendar calendar, int amount);


	@Override
	public String toString() {
		return this.getClass().getName() + " [" + amount + "]";
	}
}
