package jcf.extproc.execution;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Map;

import jcf.extproc.config.ExtProcConstant;
import jcf.extproc.env.EnvMapPopulator;
import jcf.extproc.exception.ExternalProcessException;
import jcf.extproc.process.ExternalProcess;
import jcf.extproc.process.JobInstanceInfo;
import jcf.extproc.process.JobStatus;
import jcf.extproc.process.builder.ExternalProcessBuilder;
import jcf.extproc.process.builder.ExternalProcessBuilderForJdk15Visitor;
import jcf.extproc.process.builder.ExternalProcessBuilderVisitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExternalProcessExecution {

	private static final Logger logger = LoggerFactory
			.getLogger(ExternalProcessExecution.class);
	
	private Process process;
	private JobInstanceInfo jobInstanceInfo;
	private ExternalProcessBuilder processBuilder;

	private Charset cs;

	private PrintStream printStream;

	private ExternalProcess externalProcess;

	private boolean isUserStopped = false;
	
	public ExternalProcessExecution(JobInstanceInfo jobInstanceInfo, File instanceDirectory, ExternalProcess externalProcess,
			EnvMapPopulator envMapPopulator,
			Map<String, String> adhocEnvironment, Charset cs
			) {
		this.jobInstanceInfo = jobInstanceInfo;
		this.externalProcess = externalProcess;

		processBuilder = getProcessBuilder(instanceDirectory, adhocEnvironment, externalProcess, envMapPopulator);
		
		engraveJobInstanceInfo(jobInstanceInfo);
		
		this.cs = cs;
		
		try {
			printStream = new PrintStream(new File(instanceDirectory, ExtProcConstant.LOGFILE_NAME));
			
		} catch (FileNotFoundException e) {
			throw new ExternalProcessException("error creating logFile : " + new File(instanceDirectory, ExtProcConstant.LOGFILE_NAME), e);
		}
		
	}

	/**
	 * 외부 프로세스로 외부프로세스 실행 측면에서의 작업 이름과 인스턴스 정보를 전달.
	 * @param jobInstanceInfo
	 */
	private void engraveJobInstanceInfo(JobInstanceInfo jobInstanceInfo) {
		processBuilder.environment().put(ExtProcConstant.ENV_JOB_NAME, jobInstanceInfo.getJobName());
		processBuilder.environment().put(ExtProcConstant.ENV_JOB_INSTANCE, Long.toString(jobInstanceInfo.getJobInstance()));
		processBuilder.environment().put(ExtProcConstant.ENV_JOB_INSTANCE_NAME, jobInstanceInfo.getJobInstanceName());
		processBuilder.environment().put(ExtProcConstant.ENV_USER_NAME, jobInstanceInfo.getUser());
		processBuilder.environment().put(ExtProcConstant.ENV_JOB_DESCRIPTION, jobInstanceInfo.getDescription());
	}

	/**
	 * 자바 플랫폼별 프로세스 빌더 생성.
	 * <p>
	 * 환경변수 처리는 아래 순서로 적용, 오버라이트.
	 * <ul>
	 * <li>작업 정의에 명시된 것</li>
	 * <li>오퍼레이터에서 정의한 작업별 환경변수 채우미 적용</li>
	 * <li>실행 명령에서 지정한 환경변수</li>
	 * </ul>
	 * 
	 * @return
	 */
	private ExternalProcessBuilder getProcessBuilder(File logDirectory, Map<String, String> adhocEnvironment, ExternalProcess externalProcess, EnvMapPopulator envMapPopulator
) {
		ExternalProcessBuilderVisitor processBuilderVisitor = new ExternalProcessBuilderForJdk15Visitor(logDirectory);
		
		externalProcess.accept(processBuilderVisitor);
		ExternalProcessBuilder processBuilder = processBuilderVisitor.getExternalProcessBuilder();
		
		if (envMapPopulator != null) {
			envMapPopulator.populate(processBuilder.environment());
		}
		if (adhocEnvironment != null) {
			processBuilder.environment().putAll(adhocEnvironment);
		}
		return processBuilder;
	}

	/**
	 * 프로세스의 실행 시작 및 로그 파일 쓰기, instanceInfo 업데이트
	 */
	public void run() {
		
		jobInstanceInfo.setTriggerDate(new Date());
		long start = System.currentTimeMillis();
		
		int exitValue = -1;

		writeHeader(jobInstanceInfo);
		
		try {
			process = processBuilder.start();
			
			boolean isFailure = writeBodyChekingFailureString(jobInstanceInfo, process.getInputStream());
			
			exitValue = process.waitFor();
			
			jobInstanceInfo.setResult(determineResult(exitValue, isFailure));

		} catch (InterruptedException e) {
			jobInstanceInfo.setResult(JobStatus.FAIL);			
			writeError(jobInstanceInfo, e);
			
			throw new ExternalProcessException("interrupted", e);
			
		} catch (RuntimeException e) {
			jobInstanceInfo.setResult(JobStatus.FAIL);			
			writeError(jobInstanceInfo, e);
			
			throw e;
			
		} finally {
			jobInstanceInfo.setDuration(System.currentTimeMillis() - start);
			jobInstanceInfo.setExitValue(exitValue);

			writeFooterAndClose(jobInstanceInfo);
		}
		
	}
	
	private JobStatus determineResult(int exitValue, boolean isFailure) {
		if (isUserStopped) 
			return JobStatus.STOPPED;
		
		return (exitValue != 0 || isFailure) ? JobStatus.FAIL : JobStatus.SUCCESS;
	}

	public boolean destroy() {
		if (process == null) {
			logger.info("job execution has no process yet : {} [{}] {} - {}'",
					new Object[]{jobInstanceInfo.getJobName(), jobInstanceInfo.getJobInstance(), jobInstanceInfo.getJobInstanceName(), jobInstanceInfo.getDescription()});

			return false;
		}

		isUserStopped = true;
		
		process.destroy();
		
		logger.info("job execution destroyed : {} [{}] {} - {}'",
				new Object[]{jobInstanceInfo.getJobName(), jobInstanceInfo.getJobInstance(), jobInstanceInfo.getJobInstanceName(), jobInstanceInfo.getDescription()});
		
		return true;
	}

	private boolean writeBodyChekingFailureString(JobInstanceInfo jobInstanceInfo, InputStream inputStream) {
		/*
		 * 외부 프로세스의 입력스트림 문자 셋은 자바 가상머신의 기본 문자 셋과 다를 수 있으므로 명시해야 함.
		 */
		BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(inputStream, cs));
		
		boolean failure = false;
		
		try {
			for (String line = processOutputReader.readLine(); line != null; line = processOutputReader
					.readLine()) {
				printStream.println(line);
				logger.debug(line);
				if (externalProcess.checkForFailure(line)) {
					failure = true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace(printStream);
			logger.warn("error reading inputStream from process of job '{} [{}] {}'",
					new Object[]{jobInstanceInfo.getJobName(), jobInstanceInfo.getJobInstance(), jobInstanceInfo.getJobInstanceName(), e});
		
		} finally {
			try {
				processOutputReader.close();
		
			} catch (IOException e) {
				logger.warn("cannot close lineNumberReader.", e);
			}
		}
		
		return failure;
	}


	private void writeHeader(JobInstanceInfo jobInstanceInfo) {
		printStream.print("EXECUTING JOB ");
		printStream.print(jobInstanceInfo.getJobName());
		printStream.print(" [");
		printStream.print(jobInstanceInfo.getJobInstance());
		printStream.print("] ");
		printStream.print(jobInstanceInfo.getJobInstanceName());
		printStream.print(" BY ");
		printStream.println(jobInstanceInfo.getUser());
	}

	private void writeError(JobInstanceInfo jobInstanceInfo, Throwable t) {
		printStream.print("ERROR EXECUTING JOB '");
		printStream.print(jobInstanceInfo.getJobName());
		printStream.print(" [");
		printStream.print(jobInstanceInfo.getJobInstance());
		printStream.print("] ");
		printStream.print(jobInstanceInfo.getJobInstanceName());
		printStream.println("'");
		
		t.printStackTrace(printStream);
	}

	private void writeFooterAndClose(JobInstanceInfo jobInstanceInfo) {
		printStream.print("PROCESS EXIT VALUE : ");
		printStream.print(jobInstanceInfo.getExitValue());
		
		if (isUserStopped) {
			printStream.println(" (TERMINATED AT USER REQUEST)");
			
		} else {
			printStream.println();
		}
		
		printStream.print("EXECUTION DURATION(ms) : ");
		printStream.println(jobInstanceInfo.getDuration());
		
		
		printStream.println(ExtProcConstant.END_MARKER);
		printStream.close();
	}


}
