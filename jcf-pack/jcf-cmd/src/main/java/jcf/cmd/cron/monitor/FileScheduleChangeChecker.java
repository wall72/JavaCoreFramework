package jcf.cmd.cron.monitor;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileScheduleChangeChecker implements ScheduleChangeChecker {

	private static final Logger logger = LoggerFactory
			.getLogger(FileScheduleChangeChecker.class);
	
	private File fileToWatch;
	private long lastModified;
	
	public FileScheduleChangeChecker(File file) {
		this.fileToWatch = file;
		this.lastModified = fileToWatch.lastModified();
	}

	public boolean isModified() {
		long curlastModified = fileToWatch.lastModified();
		
		if (curlastModified > lastModified) {
			lastModified = curlastModified;
			logger.info("file modified : {}", fileToWatch);
			
			return true;
			
		} else {
			return false;
		}
	}
	
	public void load(ScheduleUpdater updater) {
		if (! fileToWatch.exists()) {
			createEmptyCrontabFile(fileToWatch);
		}
		
		loadCrontabFile(fileToWatch, updater);
	}


	private void createEmptyCrontabFile(File fileToWatch) {
		final PrintWriter writer;
		try {
			writer = new PrintWriter(fileToWatch);
			
		} catch (FileNotFoundException e) {
			throw new ScheduleChangeCheckerException("cannot create new crontab file " + fileToWatch, e);
		}
		
		try {
			writer.println("################################################");
			writer.println("# sltis-cmd java scheduler crontab file");
			writer.println("# ");
			writer.println("# expr format : ");
			writer.println("# <cron expression> <exclusive job name> <runnerName> [runner args..]");
			writer.println("# ");
			writer.println("# lines starting with # are ignored.");
			writer.println("# example : ");
			writer.println("# 0 0/5 * * * ?	TEST/INSTANCE1 sampleRunner arg0 arg1");
			writer.println("# 0 10 * * * MON-FRI TEST/INSTANCE2 weekDayRunner");
			writer.println("# ");
			writer.println();
			
		} finally {
			writer.close();
		}
	}

	private static Pattern pattern = Pattern.compile("(\\S+\\s+\\S+\\s+\\S+\\s+\\S+\\s+\\S+\\s+\\S+)\\s+(.+?)\\s+(.+)$");
	
	private void loadCrontabFile(File crontabFile, ScheduleUpdater updater) {
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(crontabFile)));

			Set<String> jobNames = new HashSet<String>();

			for (String line = reader.readLine(); line != null; line = reader
					.readLine()) {
				if (line.length() == 0 || line.startsWith("#"))
					continue;

				Matcher matcher = pattern.matcher(line);
				if (matcher.find()) {
					String cronExpression = matcher.group(1);
					String jobName = matcher.group(2);
					String cmdLine = matcher.group(3);

					if (jobNames.add(jobName)) {
						updater.updateSchedule(jobName, cmdLine, cronExpression);

					} else {
						logger.warn("duplicate jobNames found : {}", line);
					}

				} else {
					logger.warn("invalid line : " + line);
				}
			}
		} catch (IOException e) {
			throw new ScheduleChangeCheckerException("error reading " + crontabFile, e);

		} finally {
			if (reader != null) {
				try {
					reader.close();

				} catch (IOException e) {
					logger.warn("error closing file reader for " + crontabFile, e);
				}
			}
		}

	}


}
