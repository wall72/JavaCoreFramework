package jcf.task;

/**
 *
 * TaskWorker 를 만들기 위한 Factory Class
 *
 * @author nolang
 *
 */
public class TaskWorkerFactory {

	/**
	 * TaskWorker 생성
	 *
	 * @param target - 위임받은 서비스빈
	 * @param methodName - 실행할 메소드 이름
	 * @param parameterTypes - 실행할 메소드의 ParameterType
	 * @param args - 연산인자
	 * @param returnType - 메소드의 리턴타입
	 * @return
	 */
	public static <T> TaskWorker<T> getTaskWorker(Object target,
			String methodName, Class<?>[] parameterTypes, Object[] args,
			Class<T> returnType) {
		return new TaskWorker<T>(target, methodName, parameterTypes, args);
	}
}
