package jcf.query.web.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcf.query.web.CommonVariableHolder;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author nolang
 *
 */
public abstract class SessionParameterIntegrationInterceptor implements HandlerInterceptor {

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		Map<String, Object> sessionValues = getSessionValues(request);

		if(sessionValues != null){
			CommonVariableHolder.addVariables(sessionValues);
		}

		return true;
	}

	public void postHandle(HttpServletRequest request, 	HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

	}

	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)	throws Exception {
		CommonVariableHolder.clear();
	}

	public abstract Map<String, Object> getSessionValues(HttpServletRequest request);

}
