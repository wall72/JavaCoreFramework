package jcf.data.web.context;

import org.springframework.core.NamedInheritableThreadLocal;

/**
*
* 컨텍스트를 조작하기 위한 편의 클래스
*
* @author nolang
*
*/
public class RequestContextHolder {

	private static final String CONTEXT_NAME = "JCF";
	private static final ThreadLocal<RequestContext> INHERIABLE_REQUEST_CONTEXT_HOLDER = new NamedInheritableThreadLocal<RequestContext>(CONTEXT_NAME);

	/**
	 *
	 * 컨텍스트의 집합인 RequestContext를 반환한다.
	 *
	 * @return
	 */
	public static RequestContext getContext()	{
		RequestContext context = INHERIABLE_REQUEST_CONTEXT_HOLDER.get();

		if(context == null)	{
			context = new RequestContext();
			INHERIABLE_REQUEST_CONTEXT_HOLDER.set(context);
		}

		return context;
	}

	/**
	 * RequestContext를 초기화한다.
	 */
	public static void clear()	{
		INHERIABLE_REQUEST_CONTEXT_HOLDER.remove();
	}

	public static void clear(String contextName) {
		INHERIABLE_REQUEST_CONTEXT_HOLDER.get().setContext(contextName, null);
	}
}
