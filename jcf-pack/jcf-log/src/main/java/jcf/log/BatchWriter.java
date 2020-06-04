package jcf.log;

import java.util.List;

/**
 * 일정 수의 로그 이벤트를 모아서 한번에 보낸다.
 * 
 * @author setq
 *
 * @param <T>
 */
public interface BatchWriter<T> {

	/**
	 * 해당 로그 이벤트들을 기록장치에 보낸다.
	 * @param logEvents
	 */
	void write(List<T> logEvents);
	
}
