package jcf.sua.support.trace.web;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcf.sua.support.trace.Trace;
import jcf.sua.support.trace.TraceContext;
import jcf.sua.support.trace.TraceContextHolder;
import jcf.sua.support.trace.recorder.TraceRecorder;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 이력 처리를 지원하는 인터셉터. 초기 이력 정보를 생성해 쓰레드 범위로 유지하며,
 * 컨트롤러에서의 요청 처리 후 발생한 이력 정보를 {@link TraceRecorder}를 이용해 처리한다.
 *
 * @since 1.0
 *
 */
public class TraceInterceptor extends HandlerInterceptorAdapter {

	public static final String BUSINESS_TRANSACTION_ID = "Business-Transaction-Id";

	private TraceRecorder traceRecorder;
	private String businessTransactionIdPrefix;

	/**
	 * 이력 처리를 담당하는 레코더 설정
	 *
	 * @param traceRecorder
	 */
	public void setTraceRecorder(TraceRecorder traceRecorder) {
		this.traceRecorder = traceRecorder;
	}

	/**
	 * 비즈니스 트랜잭션 ID 생성 시 사용할 분류가 있을 경우 설정. 애플리케이션 별로 트랜잭션 ID를 구분하고 싶은 경우 설정
	 *
	 * @param businessTransactionIdPrefix
	 */
	public void setBusinessTransactionIdPrefix(String businessTransactionIdPrefix) {
		this.businessTransactionIdPrefix = businessTransactionIdPrefix;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		TraceContextHolder.set(new TraceContext());
		TraceContextHolder.get().setTrace(new Trace(newTransactionId(), currentTime()));

		return super.preHandle(request, response, handler);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		Trace t = (Trace) TraceContextHolder.get().getTrace();

		// 요청 처리 종료 시간 설정
		t.setEndTime(currentTime());

		// 트레이스 정보 이력 남기기
		recordTrace(response, ex, t);

		TraceContextHolder.get().clear();
	}

	// TODO 남기는 정보, 위치 개선 필요
	protected void recordTrace(HttpServletResponse response, Exception ex, Trace t) {
		// 응답 헤더에 트랜잭션 ID 저장
		response.addHeader(BUSINESS_TRANSACTION_ID, t.getTransactionId());

		// Element 정보 로깅
		traceRecorder.recordTraceElement(t);

		// 에러 정보 로깅
		traceRecorder.recorErrors(ex);
	}

	/**
	 * 요청 처리 시작 시간
	 *
	 * @return
	 */
	protected long currentTime() {
		return System.currentTimeMillis();
	}

	/**
	 * 트랜잭션 ID 체번.
	 *
	 * @return
	 */
	protected String newTransactionId() {
		String txId = UUID.randomUUID().toString();

		if (StringUtils.hasText(businessTransactionIdPrefix)) {
			txId = businessTransactionIdPrefix + txId;
		}

		return txId;
	}
}
