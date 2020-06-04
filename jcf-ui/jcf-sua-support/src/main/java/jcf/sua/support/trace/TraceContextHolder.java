package jcf.sua.support.trace;

import org.springframework.core.NamedInheritableThreadLocal;

/**
 * <p>
 * 애플리케이션 공통 모델인 {@link TraceContext} 객체를 쓰레드 기반으로 유지하며, 필요할 때는 어느
 * 계층에서나 {@link TraceContextHolder#get()} 호출을 통해 접근이 가능하다.
 * </p>
 */
public final class TraceContextHolder {

	private static final String APPICATION_COMMON_CONTEXT_NAME = "JCF-SUA-SUPPORT-TRACE";

	private static final ThreadLocal<TraceContext> INHERIABLE_APPLICATION_COMMON_CONTEXT_HOLDER = new NamedInheritableThreadLocal<TraceContext>(
			APPICATION_COMMON_CONTEXT_NAME);

	private TraceContextHolder() {
	}

	/**
	 * <pre>
	 * 현재 쓰레드에 {@link TraceContext}를 바인딩.
	 * 만약 현재 쓰레드에 이미 바인딩 되어 있는 객체가 있는 경우에는 덮어쓰게 되니 주의 요망.
	 * </pre>
	 *
	 * @param context
	 */
	public static void set(TraceContext context) {
		INHERIABLE_APPLICATION_COMMON_CONTEXT_HOLDER.set(context);
	}

	/**
	 * <pre>
	 * 현재 쓰레드에 바인딩 되어 있는 {@link TraceContext} 객체 반환.
	 * 만약 없을 경우에는 내부적으로 {@link TraceContext}을 생성하여 쓰레드에 바인딩 하고 객체를 반환 함
	 * </pre>
	 *
	 * @return 현재 쓰레드에 바인딩 되어 있는 {@link TraceContext} 객체
	 */
	public static TraceContext get() {
		TraceContext commonContext = INHERIABLE_APPLICATION_COMMON_CONTEXT_HOLDER.get();

		if (commonContext == null) {
			TraceContext createdContext = new TraceContext();

			INHERIABLE_APPLICATION_COMMON_CONTEXT_HOLDER.set(createdContext);

			return createdContext;
		}

		return commonContext;
	}

	public static void clear() {
		if (INHERIABLE_APPLICATION_COMMON_CONTEXT_HOLDER.get() != null) {
			INHERIABLE_APPLICATION_COMMON_CONTEXT_HOLDER.remove();
		}
	}
}
