package jcf.extproc.process;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class AntProcess implements ExternalProcess {

	private static final long serialVersionUID = 1L;

	public boolean checkForFailure(String line) {
		return line.startsWith("BUILD FAILED");
	}
	private String name;
	private String description;
	private String[] commandLines;
	private String workDirectory;
	private HashMap<String, String> envMap = new HashMap<String, String>();
	private String user;
	private LogFileKeepingPolicy logFileKeepingPolicy;
	
	@Override
	public String toString() {
		return "AntProcess [name=" + name + ", description=" + description
				+ ", commandLines=" + Arrays.toString(commandLines)
				+ ", workDirectory=" + workDirectory + ", envMap=" + envMap
				+ ", user=" + user + ", logFileKeepingPolicy="
				+ logFileKeepingPolicy + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(commandLines);
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((envMap == null) ? 0 : envMap.hashCode());
		result = prime
				* result
				+ ((logFileKeepingPolicy == null) ? 0 : logFileKeepingPolicy
						.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		AntProcess other = (AntProcess) obj;
		if (!Arrays.equals(commandLines, other.commandLines))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (envMap == null) {
			if (other.envMap != null)
				return false;
		} else if (!envMap.equals(other.envMap))
			return false;
		if (logFileKeepingPolicy == null) {
			if (other.logFileKeepingPolicy != null)
				return false;
		} else if (!logFileKeepingPolicy.equals(other.logFileKeepingPolicy))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
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

	public AntProcess(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setDescription(String description) {
		this.description =description;
	}

	public Map<String, String> getEnvMap() {
		return envMap;
	}

	public String getDescription() {
		return this.description;
	}

	public void setCommandLine(String ... commandLines) {
		this.commandLines = commandLines;
	}

	public String[] getCommandLine() {
		return commandLines;
	}

	public void setWorkDirectory(String workingDirectory) {
		this.workDirectory = workingDirectory;
	}

	public String getWorkDirectory() {
		return workDirectory;
	}

	public void accept(ExternalProcessVisitor externalProcessVisitor) {
		externalProcessVisitor.visit(this);
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}

	public void setLogFileKeepingPolicy(
			LogFileKeepingPolicy logFileKeepingPolicy) {
		this.logFileKeepingPolicy = logFileKeepingPolicy;
	}
	
	public LogFileKeepingPolicy getLogFileKeepingPolicy() {
		return logFileKeepingPolicy;
	}
	
}
