package jcf.sua.support.trace;

import jcf.sua.support.trace.element.TraceElement;
import jcf.sua.support.trace.web.TraceInterceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 이력처리 정보를 처리하는 모델 정보를 유지. 정보 유형은 다음과 같다.
 * <ul>
 * <li>클라이언트 타입
 * <li>User Agent
 * <li>요청 추적 이력
 * </ul>
 *
 */
public class TraceContext {

	private final Logger traceLogger = LoggerFactory.getLogger(LogConstant.TRACE_LOGGER);

	private Trace trace;

	/**
	 * 트레이스 객체 설정. 일반적으로 {@link TraceInterceptor}를 통해 프레임워크 내부에서 설정되므로
	 * 개별 애플리케이션에서 직접 객체를 생성해 설정하지 않도록 한다.
	 *
	 * @param trace 저장될 트레이스 객체
	 */
	public void setTrace(Trace trace) {
		this.trace = trace;
	}

	/**
	 * {@link TraceInterceptor}를 통해 초기화한 이력 정보 관리 객체 조회. 일반적인 MVC 기반에서는 쓰레드 별로
	 * {@link Trace} 정보가 생성되어 사용됨
	 *
	 * @return {@link Trace} 객체 반환. 없을 시에는 null 반환
	 */
	public Trace getTrace() {
		return trace;
	}

	/**
	 * {@link Trace} 정보 초기화
	 */
	public void clear() {
		if (this.trace != null) {
			trace = null;
		}
	}

	/**
	 * 이력 정보를 남길 수 있는지 확인
	 *
	 * @return true 이력 정보 처리 활성화된 상태
	 */
	public boolean isTraceable() {
		return this.trace != null && this.trace.getElements() != null
				&& traceLogger.isDebugEnabled();
	}

	/**
	 * {@link TraceContext#isTraceable()}를 통해 이력 추적 기능이 활성상태인지 확인한 후 요청 추적 이력 정보 저장
	 *
	 * @param element 요청 추적 이력 정보
	 *
	 * @return 요청 추적 이력 추가가 성공하는 경우 true 반환
	 */
	public boolean addTraceElement(TraceElement element) {
		boolean result = false;

		if (isTraceable()) {
			getTrace().addElement(element);
			result = true;
		}

		return result;
	}
}
