package jcf.extproc.execution;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import jcf.extproc.config.JobConfig;
import jcf.extproc.config.JobConfigVisitor;
import jcf.extproc.config.job.JobXmlConfig;
import jcf.extproc.config.jobinstance.JobInstanceXmlConfig;
import jcf.extproc.exception.ExternalProcessException;
import jcf.extproc.process.ExternalProcess;
import jcf.extproc.process.JobInstanceInfo;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStreamException;

public class FileBasedExternalProcessRepository implements ExternalProcessRepository {

	private static final Logger logger = LoggerFactory
			.getLogger(FileBasedExternalProcessRepository.class);
	
	private static final String JOB_CONFIG_XML = "job-config.xml";
	private static final String JOB_INSTANCE_STATUS_XML = "status.xml";

	private File baseDirectory;

	public FileBasedExternalProcessRepository(File baseDirectory) {
		this.baseDirectory = baseDirectory;
	}

	public File getInstanceDirectory(JobInstanceInfo jobInstanceInfo) {
		return new File(getJobDirectory(jobInstanceInfo.getJobName()), jobInstanceInfo.getJobInstanceName());
	}

	public SortedSet<JobInstanceInfo> loadInstances(ExternalProcess externalProcess) {
		return loadInstancesForJobName(externalProcess.getName());
	}

	private SortedSet<JobInstanceInfo> loadInstancesForJobName(String name) {
		File jobDirectory = getJobDirectory(name);
		SortedSet<JobInstanceInfo> set = new TreeSet<JobInstanceInfo>();
		
		File[] listFiles = jobDirectory.listFiles(getDirectoryFileFilter());
		if (listFiles != null) {
			for (File instanceDirectory : listFiles) {
				JobInstanceInfo jobInstanceInfo = loadJobInstanceStatus(instanceDirectory);
				if (jobInstanceInfo != null) {
					set.add(jobInstanceInfo);
				}
			}
		}
		return set;
	}

	public void saveInstance(JobInstanceInfo jobInstanceInfo) {
		saveJobInstanceStatus(getInstanceDirectory(jobInstanceInfo), jobInstanceInfo);
	}
	
	private JobInstanceInfo loadJobInstanceStatus(File jobInstanceDirectory) {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(new File(jobInstanceDirectory, JOB_INSTANCE_STATUS_XML));

			return new JobInstanceXmlConfig().read(inputStream );
			
		} catch (FileNotFoundException e) {
			logger.warn("no jobInstance status file in {}", jobInstanceDirectory);
			return null;

		} catch (XStreamException e) {
			logger.warn("corrupt jobInstance status file in {}", jobInstanceDirectory);
			return null;
			
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
					
				} catch (IOException e) {
					logger.warn("cannot close jobInstance status file", e);
				}
			}
		}
	}
	
	private void saveJobInstanceStatus(File jobInstanceDirectory, JobInstanceInfo info) {
		jobInstanceDirectory.mkdirs();
		
		OutputStream outputStream = null;
		File jobInstanceStatusFile = new File(jobInstanceDirectory, JOB_INSTANCE_STATUS_XML);
		try {
			outputStream = new FileOutputStream(jobInstanceStatusFile);
			
			new JobInstanceXmlConfig().write(info , outputStream);
			
		} catch (IOException e) {
			throw new ExternalProcessException("error writing jobInstanceStatus file " + jobInstanceStatusFile, e);
			
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
					
				} catch (IOException e) {
					logger.warn("error closing jobInstance status file.", e);
				}
			}
		}
	}
	

	public void deleteInstance(JobInstanceInfo jobInstanceInfo) {
		File instanceDirectory = getInstanceDirectory(jobInstanceInfo);

		try {
			FileUtils.deleteDirectory(instanceDirectory);

		} catch (IOException e) {
			throw new ExternalProcessException("error deleting instance directory " + instanceDirectory, e);
		}
		
	}

	public long getNextInstanceId(String jobName) {
		final long instanceId;

		File nextInstanceIdFile = new File(getJobDirectory(jobName), "nextInstanceId");
		
		instanceId = readNextInstanceId(nextInstanceIdFile);
		
		if (instanceId == 0) {
			findFallbackInstanceId(jobName);
		}

		writeInstanceId(instanceId + 1, nextInstanceIdFile);

		return instanceId;
	}

	/**
	 * 다음 인스턴스 ID를 얻어온다. 기존 값이 없으면 없으면 1을 반환한다.
	 * @param nextInstanceIdFile
	 * @return
	 */
	private long readNextInstanceId(File nextInstanceIdFile) {
		final long nextInstanceId;
		if (nextInstanceIdFile.exists()) {
			String line;
			BufferedReader lineReader = null;
			try {
				lineReader = new BufferedReader(new FileReader(nextInstanceIdFile));
				line = lineReader.readLine();

			} catch (FileNotFoundException e) {
				throw new ExternalProcessException("file not found : " + nextInstanceIdFile, e);
			} catch (IOException e) {
				throw new ExternalProcessException("error reading file : " + nextInstanceIdFile, e);
			} finally {
				if (lineReader != null) {
					try {
						lineReader.close();

					} catch (IOException e) {
						logger.warn("cannot close reader for file " + nextInstanceIdFile, e);
					}
				}
			}
			
			long readNumber;
			try {
				readNumber = Long.parseLong(line);
				
			} catch (NumberFormatException e) {
				readNumber = 0;
			}
			
			nextInstanceId = readNumber;

		} else {
			nextInstanceId = 0;
		}
		return nextInstanceId;
	}

	/**
	 * nextInstanceId 파일이 없거나 손상된 경우 jobInstanceDirectory를 스캔하여 값을 유추하고,
	 * 스캔할 대상이 없는 경우 기본 1을 반환한다.
	 * @return
	 */
	private long findFallbackInstanceId(String jobName) {
		SortedSet<JobInstanceInfo> instances = loadInstancesForJobName(jobName);
		if (instances.isEmpty()) {
			return 1;
		}
		
		return instances.last().getJobInstance() + 1;
	}

	private void writeInstanceId(final long nextInstanceId, File nextInstanceIdFile) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(nextInstanceIdFile);
			writer.write("" + (nextInstanceId));

		} catch (IOException e) {
			throw new ExternalProcessException("error writing file : " + nextInstanceIdFile, e);

		} finally {
			if (writer != null) {
				try {
					writer.close();

				} catch (IOException e) {
					logger.warn("cannot close writer for file " + nextInstanceIdFile, e);
				}
			}
		}
	}

	
	
	public void saveExternalProcess(ExternalProcess externalProcess) {
		JobConfigVisitor jobConfigVisitor = new JobConfigVisitor();
		externalProcess.accept(jobConfigVisitor);
		
		JobConfig jobConfig = jobConfigVisitor.getJobConfig();
		
		String jobName = externalProcess.getName();
		writeJobConfig(getJobDirectory(jobName), jobConfig);
	}

	private File getJobDirectory(String jobName) {
		return new File(baseDirectory, jobName);
	}

	public void deleteExternalProcess(String jobName) {
		try {
			FileUtils.deleteDirectory(getJobDirectory(jobName));

		} catch (IOException e) {
			throw new ExternalProcessException("error deleting job directory " + jobName, e);
		}
	}

	public Set<ExternalProcess> loadExternalProcesses() {
		Set<ExternalProcess> jobSet = new LinkedHashSet<ExternalProcess>();
		
		scanForJob(jobSet, baseDirectory);
		
		return jobSet;
	}

	/**
	 * 재귀 호출.
	 * 작업 디렉토리가 nesting 되어 있으면 못찾음. (안찾음. 잘못된 디렉토리 구조)
	 * 
	 * @param jobSet
	 * @param scanBaseDirectory
	 */
	private void scanForJob(Set<ExternalProcess> jobSet, File scanBaseDirectory) {
		File[] listFiles = scanBaseDirectory.listFiles(getDirectoryFileFilter());
		if (listFiles == null) return;
		
		for (File jobDirectory : listFiles) {
			ExternalProcess externalProcess = loadJob(jobDirectory);
			
			if (externalProcess == null) {
				scanForJob(jobSet, jobDirectory);
				
			} else {
				if (new File(baseDirectory, externalProcess.getName()).equals(jobDirectory)) {
					jobSet.add(externalProcess);
					logger.debug("job config file loaded. {}", externalProcess);
					
				} else {
					logger.warn("job {} on invalid directory {}. correct the file system.", externalProcess.getName(), jobDirectory);
				}
			}
		}
	}

	private FileFilter getDirectoryFileFilter() {
		return new FileFilter() {
			
			public boolean accept(File file) {
				return file.isDirectory();
			}
		};
	}

	private JobConfig readJobConfigFile(File jobDirectory) {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(new File(jobDirectory, JOB_CONFIG_XML));
			return new JobXmlConfig().read(inputStream );
			
		} catch (FileNotFoundException e) {
			logger.debug("no job config file found in {}", jobDirectory);
			return null;
			
		} catch (XStreamException e) {
			logger.warn("corrupt job config file in {}", jobDirectory);
			return null;
			
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
					
				} catch (IOException e) {
					logger.warn("cannot close job config file", e);
				}
			}
		}
	}
	
	private void writeJobConfig(File jobDirectory, JobConfig jobConfig) {
		jobDirectory.mkdirs();

		File jobConfigFile = new File(jobDirectory, JOB_CONFIG_XML);
		OutputStream outputStream;
		try {
			outputStream = new FileOutputStream(jobConfigFile);
			
		} catch (FileNotFoundException e) {
			throw new ExternalProcessException("cannot open file : " + JOB_CONFIG_XML, e);
		}
		try {
			new JobXmlConfig().write(jobConfig, outputStream );
			
		} catch (IOException e) {
			throw new ExternalProcessException("error writing jobConfig file " + jobConfigFile, e);
			
		} finally {
			try {
				outputStream.close();
				
			} catch (IOException e) {
				logger.warn("error closing file " + JOB_CONFIG_XML, e);
			}
		}
	}
	
	private ExternalProcess loadJob(File jobDirectory) {
		JobConfig jobConfig = readJobConfigFile(jobDirectory);
		
		final ExternalProcess externalProcess;
		
		if (jobConfig == null) {
			externalProcess = null;
			
		} else if (jobConfig.getAntProcess() != null) {
			externalProcess = jobConfig.getAntProcess();
			
		} else if (jobConfig.getCommandLineProcess() != null) {
			externalProcess = jobConfig.getCommandLineProcess();
			
		} else {
			externalProcess = null;
		}
		
		return externalProcess; 
	}

}
