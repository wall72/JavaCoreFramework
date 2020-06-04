package jcf.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

/**
 * 병렬처리기
 *
 * @author nolang
 *
 */
@Service
public class MultiTaskExecutor {

	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;

	public <T> List<T> execute(Collection<TaskWorker<T>> tasks, Class<T> clazz) {

		List<Future<T>> futureList = new ArrayList<Future<T>>();
		/*
		 * Task 비동기 실행
		 */
		for (TaskWorker<T> taskWorker : tasks) {
			futureList.add(executeInternal(taskWorker));
		}

		/*
		 * 결과수집
		 */
		List<T> retValueList = new ArrayList<T>();

		for (int i = 0; i < futureList.size(); ++i) {
			Future<T> future = futureList.get(i);

			try {
				retValueList.add(future.get());
			}
			  catch (Exception e) {
			}
		}

		return retValueList;
	}

	protected <T> Future<T> executeInternal(TaskWorker<T> taskWorker) {
		return taskExecutor.submit(taskWorker);
	}

	public void setTaskExecutor(ThreadPoolTaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}
}
