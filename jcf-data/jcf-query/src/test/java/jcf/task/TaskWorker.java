package jcf.task;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 *
 * 서비스 빈으로 부터 작업을 위임받아 처리하며, 연산 결과는 반환되는 Future의 get 메소드를 사용하여 조회한다.
 *
 *  - TaskWorker task = TaskWorkFactory.getTaskWorker(...)
 *
 * @author nolang
 *
 * @param <T>
 */
public class TaskWorker<T> implements Callable<T> {
	private Object target;
	private String methodName;
	private Class<?>[] parameterTypes;
	private Object[] args;

	public TaskWorker(Object target, String methodName, Class<?>[] parameterTypes, Object[] args) {
		this.target = target;
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
		this.args = args;
	}

	@SuppressWarnings("unchecked")
	public T call() throws Exception {
		Method method = target.getClass().getMethod(methodName,
				parameterTypes);
		return (T) method.invoke(target, args);
	}

	public String getWokerId(){
		return methodName;
	}
}
