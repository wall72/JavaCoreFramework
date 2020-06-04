package jcf.extproc.process.builder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import jcf.extproc.exception.ExternalProcessException;

public class ExternalProcessBuilder {

	private String[] commandLine;
	private File directory;
	private Map<String, String> envMap = new HashMap<String, String>();
	
	public ExternalProcessBuilder(String[] commandLine) {
		this.commandLine = commandLine;
	}

	public void directory(File directory) {
		this.directory = directory;
	}

	public Map<String, String> environment() {
		return envMap;
	}

	public Process start() {
		ProcessBuilder builder = new ProcessBuilder(commandLine);
		
		/*
		 * prevent environment from being set to NULL
		 */
		if (envMap.containsValue(null)) {
			for (Entry<String, String> entry : envMap.entrySet()) {
				if (entry.getValue() != null) {
					builder.environment().put(entry.getKey(), entry.getValue());
					
				} else {
					builder.environment().remove(entry.getKey());
				}
			}
			
		} else {
			builder.environment().putAll(envMap);
		}
		
		builder.directory(directory);
		builder.redirectErrorStream(true);
		
		try {
			return builder.start();

		} catch (IOException e) {
			throw new ExternalProcessException("error creating process : " + dump(commandLine));
		}
	}

	private String dump(String[] commandLine) {
		StringBuilder sb = new StringBuilder();
		
		for (String cmd : commandLine) {
			sb.append(cmd).append(" ");
		}
		
		return sb.toString();
	}

}
