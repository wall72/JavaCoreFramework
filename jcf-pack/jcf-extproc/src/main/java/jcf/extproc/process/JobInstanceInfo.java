package jcf.extproc.process;

import java.io.Serializable;
import java.util.Date;

public class JobInstanceInfo implements Serializable, Comparable<JobInstanceInfo> {
	
	private static final long serialVersionUID = -5584584129450059457L;
	
	private String jobName;
	private String user;
	private String description;
	/**
	 * 인스턴스 번호.
	 */
	private long jobInstance;
	private JobStatus result;
	private Date triggerDate;
	private long duration;
	/**
	 * 디렉토리로 나타나는 인스턴스 식별자. 날짜/시간으로 표기.
	 */
	private String jobInstanceName;
	private int exitValue;
	
	private String workDirectory;
	
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public long getJobInstance() {
		return jobInstance;
	}
	public void setJobInstance(long jobInstance) {
		this.jobInstance = jobInstance;
	}
	public JobStatus getResult() {
		return result;
	}
	public void setResult(JobStatus result) {
		this.result = result;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public void setTriggerDate(Date triggerDate) {
		this.triggerDate = triggerDate;
	}
	public Date getTriggerDate() {
		return triggerDate;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getUser() {
		return user;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDescription() {
		return description;
	}
	public void setJobInstanceName(String jobInstanceName) {
		this.jobInstanceName = jobInstanceName;
	}
	public String getJobInstanceName() {
		return jobInstanceName;
	}
	public void setExitValue(int exitValue) {
		this.exitValue = exitValue;
	}
	public int getExitValue() {
		return exitValue;
	}
	public void setWorkDirectory(String workDirectory) {
		this.workDirectory = workDirectory;
	}
	public String getWorkDirectory() {
		return workDirectory;
	}
	@Override
	public String toString() {
		return "JobInstanceInfo [jobName=" + jobName + ", user=" + user
				+ ", description=" + description + ", jobInstance="
				+ jobInstance + ", result=" + result + ", triggerDate="
				+ triggerDate + ", duration=" + duration + ", jobInstanceName="
				+ jobInstanceName + ", exitValue=" + exitValue
				+ ", workDirectory=" + workDirectory + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + (int) (duration ^ (duration >>> 32));
		result = prime * result + exitValue;
		result = prime * result + (int) (jobInstance ^ (jobInstance >>> 32));
		result = prime * result
				+ ((jobInstanceName == null) ? 0 : jobInstanceName.hashCode());
		result = prime * result + ((jobName == null) ? 0 : jobName.hashCode());
		result = prime * result
				+ ((this.result == null) ? 0 : this.result.hashCode());
		result = prime * result
				+ ((triggerDate == null) ? 0 : triggerDate.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		result = prime * result
				+ ((workDirectory == null) ? 0 : workDirectory.hashCode());
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
		JobInstanceInfo other = (JobInstanceInfo) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (duration != other.duration)
			return false;
		if (exitValue != other.exitValue)
			return false;
		if (jobInstance != other.jobInstance)
			return false;
		if (jobInstanceName == null) {
			if (other.jobInstanceName != null)
				return false;
		} else if (!jobInstanceName.equals(other.jobInstanceName))
			return false;
		if (jobName == null) {
			if (other.jobName != null)
				return false;
		} else if (!jobName.equals(other.jobName))
			return false;
		if (result != other.result)
			return false;
		if (triggerDate == null) {
			if (other.triggerDate != null)
				return false;
		} else if (!triggerDate.equals(other.triggerDate))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		if (workDirectory == null) {
			if (other.workDirectory != null)
				return false;
		} else if (!workDirectory.equals(other.workDirectory))
			return false;
		return true;
	}

	public int compareTo(JobInstanceInfo o) {
		if (jobInstance == o.getJobInstance()) {
			return 0;
		}
		return jobInstance < o.getJobInstance() ? -1: 1;
	}
	
}
