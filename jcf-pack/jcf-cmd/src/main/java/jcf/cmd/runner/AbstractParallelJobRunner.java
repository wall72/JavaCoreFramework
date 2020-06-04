package jcf.cmd.runner;

import jcf.cmd.progress.builder.ProgressWriterBuilder;
import jcf.cmd.progress.parallel.LogicContainer;
import jcf.cmd.progress.parallel.impl.SimpleParallelLogicContainer;
import jcf.cmd.progress.writer.ProgressWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

/**
 * 쓰레드에 의한 병렬 처리 기능을 하는 CommondLineRunner로, 각 작업의 진행률 처리가 가능하다.
 * 
 * @author setq
 *
 */
/**
 * @author setq
 *
 */
public abstract class AbstractParallelJobRunner extends AbstractProgressiveRunner {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private SimpleParallelLogicContainer logicContainer;

	@Autowired
	public void setTaskExecutor(TaskExecutor executor) {
		logicContainer = new SimpleParallelLogicContainer(executor);
	}
	
	protected final ProgressWriter prepare(ProgressWriterBuilder builder) {
		setUpContainer(logicContainer);
		
		return logicContainer.prepare(builder);
	}
	
	protected final void run(ProgressWriter progressWriter) {
		before();
		try {
			logicContainer.run(progressWriter);
		
		} catch (RuntimeException e) {
			logger.warn("afterThrowing", e);
			afterThrowing(e);
			throw e;
		}
		after();
	}

	/**
	 * 병렬처리 작업들에서 예외가 발생한 경우의 처리.
	 * <p>
	 * 기본 구현은 after()를 호출.
	 * @param e 
	 */
	protected void afterThrowing(RuntimeException e) {
		after();
	}

	/**
	 * 병렬처리 작업들을 시작하기 위한 사전 작업.
	 * <p>
	 * 기본 구현은 아무 것도 하지 않음.
	 */
	protected void before() {}
	
	/**
	 * 병렬처리 작업들을 마친 후 뒷정리 작업.
	 * <p>
	 * 기본 구현은 아무 것도 하지 않음.
	 */
	protected void after() {}

	/**
	 * 여기서 모든 병렬 작업 대상들을 컨테이너에 담도록 한다.
	 * 
	 * @param logicContainer
	 */
	protected abstract void setUpContainer(LogicContainer logicContainer);
	
};