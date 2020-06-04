package jcf.extproc.process.policy;

import jcf.extproc.process.JobInstanceInfo;
import jcf.extproc.process.LogFileKeepingPolicy;

public class CountBasedLogFileKeepingPolicy implements LogFileKeepingPolicy {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (amount ^ (amount >>> 32));
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
		CountBasedLogFileKeepingPolicy other = (CountBasedLogFileKeepingPolicy) obj;
		if (amount != other.amount)
			return false;
		return true;
	}

	private static final long serialVersionUID = 1L;
	private long amount;

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	public CountBasedLogFileKeepingPolicy(long amount) {
		this.amount = amount;
	}
	
	public boolean isOutdated(JobInstanceInfo jobInstanceInfo,
			JobInstanceInfo currentJobInstanceInfo) {
		
		long gap = currentJobInstanceInfo.getJobInstance() - jobInstanceInfo.getJobInstance();

		return amount < gap;
	}

}
