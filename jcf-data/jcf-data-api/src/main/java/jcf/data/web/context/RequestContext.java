package jcf.data.web.context;

import java.util.HashMap;
import java.util.Map;

/**
*
* 프레임워크 내부에서 공용 파라미터 정보를 저장하기 위해 사용된다
*
* @author nolang
*
*/
public class RequestContext {

	private Map<String, Object> namedParameter = new HashMap<String, Object>();
	private Map<String, Object> contexts = new HashMap<String, Object>();

	/**
	 * 애플리케이션 공용 파라미터 정보를 이름을 붙여 저장
	 *
	 * @param name   공용 파라미터 이름
	 * @param value  공용 파라미터 값
	 *
	 * @return API 편의성을 목적으로 요청 받은 {@link RequestContext}의 객체를 재 반환.
	 */
	public RequestContext addNamedParameter(String name, Object value) {
		this.namedParameter.put(name, value);
		return this;
	}

	/**
	 * 이름을 이용하여 공통 파라미터 정보 반환
	 *
	 * @param name 공용 파라미터 이름
	 *
	 * @return 공용 파라미터
	 */
	public Object getNamedParameter(String name) {
		return this.namedParameter.get(name);
	}

	/**
	 *
	 * 공용 파라미터를 처리하기 위한 컨텍스트를 설정한다.
	 *
	 * @param contextName 컨텍스트 이름
	 * @param contextObject 컨텍스트 객체
	 */
	public void setContext(String contextName, Object contextObject) {
		contexts.put(contextName, contextObject);
	}

	/**
	 *
	 * 컨텍스트를 반환한다.
	 *
	 * @param <T>
	 * @param contextName 컨텍스트 이름
	 * @param clazz
	 * @return
	 */
	public <T> T getContext(String contextName, Class<T> clazz) {
		Object context = contexts.get(contextName);

		if (context != null) {
			return clazz.cast(contexts.get(contextName));
		}

		return null;
	}

	/**
	 *
	 * 컨텍스트를 초기화한다.
	 *
	 * @param contextName
	 */
	public void clearContext(String contextName) {
		contexts.remove(contextName);
	}
}
