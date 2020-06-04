package jcf.extproc.execution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jcf.extproc.env.EnvMapPopulator;
import jcf.extproc.exception.ExternalProcessException;
import jcf.extproc.process.ExternalProcess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author setq
 * 
 */
public class ExternalProcessContextManagerImpl implements
		ExternalProcessContextManager {

	private static final Logger logger = LoggerFactory
			.getLogger(ExternalProcessContextManager.class);
	/**
	 * jobName - externalProcess
	 */
	private Map<String, UpdatableExternalProcessContext> contextMap = new HashMap<String, UpdatableExternalProcessContext>();
	private Object lock = new Object();
	private ExternalProcessRepository repository;
	private EnvMapPopulator envMapPopulator;

	public ExternalProcessContextManagerImpl(ExternalProcessRepository repository, EnvMapPopulator envMapPopulator) {
		this.repository = repository;
		this.envMapPopulator = envMapPopulator;
		
		loadJobs(repository);
	}

	private void loadJobs(ExternalProcessRepository repository) {
		for (ExternalProcess externalProcess : repository.loadExternalProcesses()) {
			contextMap.put(
					externalProcess.getName(), 
					new ExternalProcessContextImpl(externalProcess, repository, envMapPopulator)
					
			);
		}
	}

	public void put(ExternalProcess externalProcess) {
		String jobName = externalProcess.getName();

		checkIfNestedName(jobName);

		synchronized (lock) {
			UpdatableExternalProcessContext existingContext = contextMap.get(jobName);

			if (existingContext != null) {
				modifyExistingContext(externalProcess, existingContext);

			} else {
				createNewContext(externalProcess, jobName);
			}
			
			repository.saveExternalProcess(externalProcess);
		}
	}

	private void modifyExistingContext(ExternalProcess externalProcess,
			UpdatableExternalProcessContext existingContext) {
		existingContext.setExternalProcess(externalProcess);
	}

	private void createNewContext(ExternalProcess externalProcess,
			String jobName) {
		contextMap.put(jobName, new ExternalProcessContextImpl(externalProcess, repository, envMapPopulator));
	}

	public boolean remove(String jobName) {
		synchronized (lock) {
			UpdatableExternalProcessContext externalProcessContext = contextMap.get(jobName);
			if (externalProcessContext == null) {
				logger.warn("no such job in process map : " + jobName);
				return false;
			}
			if (externalProcessContext.isRunning()) {
				throw new ExternalProcessException("stop " + jobName + " before removing it.");
			}
			contextMap.remove(jobName);
			repository.deleteExternalProcess(jobName);
		}
		return true;
	}

	public ExternalProcessContext get(String jobName) {
		if (jobName.contains("\\")) {
			throw new IllegalArgumentException("illegal character '\\' in jobName : " + jobName);
		}
		final ExternalProcessContext externalProcessContext;
		
		synchronized (lock) {
			externalProcessContext = contextMap.get(jobName);
		}
		if (externalProcessContext == null) {
			throw new ExternalProcessException("no such job " + jobName);
		}

		return externalProcessContext;
	}

	/**
	 * 신규 등록할 작업 디렉토리가 기존 작업디렉토리들과 종속(포함)관계가 형성되는지 체크. 포함관계 발견시 예외 발생
	 * 
	 * @param name
	 */
	private void checkIfNestedName(String name) {
		for (String existingName : this.contextMap.keySet()) {
			checkIfDependentDirectory(name, existingName);
		}

	}

	/**
	 * 두 작업 디렉토리 간에 종속관계 (포함 관계)가 형성되는 것을 방지하기 위한 체크.
	 * <p>
	 * 총 길이는 다르면서, 앞에서부터 갈은 길이만큼의 문자열은 같고 긴 쪽의 길어지기 시작한 문자열이 디렉토리 분리자면 에러 발생시킨다.
	 * 
	 * @param name
	 * @param existingName
	 */
	private void checkIfDependentDirectory(String name, String existingName) {
		int length = name.length();
		int length2 = existingName.length();
		int minLength = length;

		if (length > length2) {
			if (name.charAt(length2) != '/')
				return;
			minLength = length2;

		} else if (length < length2) {
			if (existingName.charAt(length) != '/')
				return;

		} else {
			return;
		}

		for (int i = 0; i < minLength; i++) {
			if (name.charAt(i) != existingName.charAt(i)) {
				return;
			}
		}

		throw new ExternalProcessException(
				"suggested job name makes nested directory : " + name + ", "
						+ existingName);
	}

	public List<String> getJobNames() {
		return new ArrayList<String>(contextMap.keySet());
	}

}
