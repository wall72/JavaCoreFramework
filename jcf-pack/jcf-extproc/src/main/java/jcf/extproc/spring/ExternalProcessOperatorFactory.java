package jcf.extproc.spring;

import java.io.File;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;

import jcf.extproc.ExternalProcessOperator;
import jcf.extproc.ExternalProcessOperatorImpl;
import jcf.extproc.env.EnvMapPopulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.ExecutorServiceAdapter;

/**
 * 외부 프로세스 실행기를 초기화하기 위한 팩토리.
 * <p>
 * 스케쥴러, 외부 프로세스 문자셋, 후처리기 등을 세팅하여 외부 프로세스 실행기를 반환한다. 
 * 
 * @author setq
 *
 */
public class ExternalProcessOperatorFactory implements FactoryBean<ExternalProcessOperator>, InitializingBean, DisposableBean {

	private final Logger logger = LoggerFactory.getLogger(ExternalProcessOperatorFactory.class);

	private ExternalProcessOperatorImpl operator;
	private File baseDirectory;
	private String charset;
	private ExecutorService executorService;

	private EnvMapPopulator envMapPopulator;

	/**
	 * 외부 프로세스 실행기의 환경이 되는 기본 디렉토리 지정.
	 * 
	 * @param baseDirectory
	 */
	public void setBaseDirectory(String baseDirectory) {
		this.baseDirectory = new File(baseDirectory);
	}

	/**
	 * 외부 프로세스가 OS에서 실행될 때 입/출력 문자셋 지정. (한글 윈도우에서는 MS949)
	 * 
	 * @param charset
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}

	/**
	 * 외부 프로세스에 적용할 환경변수 세터를 지정한다.
	 * 환경변수 세터는 Persist되지 않으므로 
	 * 외부 프로세스에 최종 적용되는 환경변수는
	 * Operator 인스턴스 및 외부 프로세스 작업명에 따라 다를 수 있다.
	 * 
	 * @param jobName
	 * @param envMapPopulator
	 */
	public void setEnvMapPopulator(EnvMapPopulator envMapPopulator) {
		this.envMapPopulator = envMapPopulator;
	}
	

	/**
	 * executorService 지정.
	 * <p>
	 * spring의 taskExecutor를 지정할 때는 이것 대신 {@link #setTaskExecutor(TaskExecutor)} 를 사용한다.
	 * 
	 * @see setTaskExecutor
	 * @param executorService
	 */
	public void setExecutorService(ExecutorService executorService) {
		if (this.executorService != null) {
			throw new IllegalStateException("executorService is already set.");
		}
		this.executorService = executorService;
	}
	
	/**
	 * taskExecutor를 지정하여 executorService로 어댑팅 함.
	 * 
	 * @param taskExecutor
	 */
	public void setTaskExecutor(TaskExecutor taskExecutor) {
		if (executorService != null) {
			throw new IllegalStateException("executorService is already set.");
		}
		this.executorService = new ExecutorServiceAdapter(taskExecutor);
	}

	public ExternalProcessOperator getObject() throws Exception {
		if (operator == null) {
			operator = getOperator();
		}

		return operator;
	}

	private ExternalProcessOperatorImpl getOperator() {
		return new ExternalProcessOperatorImpl(executorService, Charset.forName(charset), baseDirectory, envMapPopulator);
	}

	public Class<ExternalProcessOperator> getObjectType() {
		return ExternalProcessOperator.class;
	}

	public boolean isSingleton() {
		return true;
	}

	public void afterPropertiesSet() throws Exception {
		if (baseDirectory == null) {
			baseDirectory = new File(System.getProperty("java.io.tmpdir"));
		}
		
		if (charset == null) {
			charset = Charset.defaultCharset().name();
		}
		
		if (executorService == null) {
			setTaskExecutor(new SimpleAsyncTaskExecutor("EXTPROC-"));
		}
	}

	public void destroy() throws Exception {
		logger.info("external process operator shutdown started.");
		executorService.shutdown();
		logger.info("external process operator shutdown complete.");
	}

}
