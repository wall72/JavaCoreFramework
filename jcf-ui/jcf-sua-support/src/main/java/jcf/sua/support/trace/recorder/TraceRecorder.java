package jcf.sua.support.trace.recorder;

import jcf.sua.support.trace.Trace;

/**
 * 이력 정보 처리를 추상화한 인터페이스
 *
 * @since 1.0
 *
 */
public interface TraceRecorder {

	/**
	 *
	 * 이력 정보 처리시 구현
	 *
	 * @param t
	 */
	void recordTraceElement(Trace t);

	/**
	 * 애플리케이션에서 발생한 예외 정보 처리
	 *
	 * @param ex
	 */
	void recorErrors(Exception ex);

}
