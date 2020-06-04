package jcf.log;

/**
 * <p>로그 이벤트를 받아서 기록 서비스로 전달.
 * 
 * @author setq
 * 
 */
public interface LogService<T> {
	
	/**
	 * 로그이벤트를 보낸다.
	 * 
	 * @param logEvent
	 *            로그이벤트
	 */
	void sendEvent(T logEvent);
}
