package jcf.sua;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcf.sua.mvc.MciViewResolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;


public class JcfSuaTestServlet {
	@Autowired
	private HandlerMapping handlerMapping;
	@Autowired
	private HandlerAdapter handlerAdapter;
	@Autowired
	private MciViewResolver viewResolver;

	/**
	 * 일반적인 요청을 처리
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	public String doDispatch(MockHttpServletRequest request, MockHttpServletResponse response) {
		try {
			return doDispatchInternal(request, response);
		} catch (Exception e) {
			throw new RuntimeException("JcfSuaTestServlet - doDispatch", e);
		}
	}




	protected String doDispatchInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		/*
		 * HandlerMapping에 정의된 HandlerInterceptor를 조회
		 */
		HandlerInterceptor[] interceptors = handlerMapping.getHandler(request).getInterceptors();

		/*
		 * 주어진 요청 URI를 처리하는 컨트롤러를 HandlerMapping에서 조회
		 */
		Object handler = handlerMapping.getHandler(request).getHandler();

		/*
		 * HandlerInterceptor.preHandler 실행
		 */
		for (int i = 0; i < interceptors.length; ++i) {
			interceptors[i].preHandle(request, response, handler);
		}

		ModelAndView model = handlerAdapter.handle(request, response, handler);

		/*
		 * HandlerInterceptor.postHandler 실행
		 */
		for (int i = interceptors.length - 1; i >= 0; --i) {
			interceptors[i].postHandle(request, response,	handler, model);
		}

		/*
		 *  Data Rendering
		 */
		if (model != null) {
			viewResolver.resolveViewName(model.getViewName(), new Locale("ko")).render(model.getModel(), request, response);
		}

		/*
		 * HandlerInterceptor.afterCompletion 실행
		 */
		for (int i = interceptors.length - 1; i >= 0; --i) {
			interceptors[i].afterCompletion(request, response,
					handler, null);
		}

		return getMockHttpServletResponse(response).getContentAsString();
	}

	private MockHttpServletResponse getMockHttpServletResponse(HttpServletResponse response)	{
			return (MockHttpServletResponse) response;
	}
}
