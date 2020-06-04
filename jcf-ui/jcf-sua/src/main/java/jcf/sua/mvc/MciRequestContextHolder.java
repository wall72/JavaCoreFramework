package jcf.sua.mvc;


import org.springframework.core.NamedInheritableThreadLocal;

/**
*
* 채널 기반 요청 처리를 위해 사용되는 임시 저장소에 접근하기 위한 편의 클래스
*
* @author nolang
*/
public class MciRequestContextHolder {
	private static final String MCI_CONTEXT_NAME = "JCF-MCI Request Context";

	private static final ThreadLocal<MciRequestContext> INHERIABLE_MCI_CONTEXT_HOLDER = new NamedInheritableThreadLocal<MciRequestContext>(
			MCI_CONTEXT_NAME);

	private MciRequestContextHolder() {
	}

	/**
	 * <pre>
	 * 현재 쓰레드에 {@link MciRequestContext}를 바인딩.
	 * 만약 현재 쓰레드에 이미 바인딩 되어 있는 객체가 있는 경우에는 덮어쓰게 되니 주의 요망.
	 * </pre>
	 *
	 * @param context
	 */
	public static void set(MciRequestContext context) {
		INHERIABLE_MCI_CONTEXT_HOLDER.set(context);
	}

	/**
	 * <pre>
	 * 현재 쓰레드에 바인딩 되어 있는 {@link MciRequestContext} 객체 반환.
	 * 만약 없을 경우에는 내부적으로 {@link MciRequestContext}을 생성하여 쓰레드에 바인딩 하고 객체를 반환 함
	 * </pre>
	 *
	 * @return 현재 쓰레드에 바인딩 되어 있는 {@link MciRequestContext} 객체
	 */
	public static MciRequestContext get() {
		MciRequestContext mciRequestContext = INHERIABLE_MCI_CONTEXT_HOLDER.get();

		if (mciRequestContext == null) {
			mciRequestContext = new MciRequestContext();
			INHERIABLE_MCI_CONTEXT_HOLDER.set(mciRequestContext);
		}

		return mciRequestContext;
	}

	/**
	 * ThreadLocal 리소스를 반환
	 */
	public static void clear() {
		if (INHERIABLE_MCI_CONTEXT_HOLDER.get() != null) {
			INHERIABLE_MCI_CONTEXT_HOLDER.remove();
		}
	}
}
