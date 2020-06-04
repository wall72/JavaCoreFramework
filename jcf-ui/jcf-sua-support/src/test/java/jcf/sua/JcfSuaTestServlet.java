package jcf.sua;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcf.sua.mvc.MciViewResolver;
import jcf.sua.ux.gauce.context.DummyGauceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import com.gauce.filter.HttpGauceRequestWrapper;
import com.gauce.filter.HttpGauceResponseWrapper;

public class JcfSuaTestServlet {
	@Autowired
	private HandlerMapping handlerMapping;
	@Autowired
	private HandlerAdapter handlerAdapter;
	@Autowired
	private MciViewResolver viewResolver;
	@Autowired(required = false)
	private HandlerExceptionResolver[] exceptionResolver;

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

	/**
	 * 가우스 요청을 처리
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	public String doDispatchForGauce(MockHttpServletRequest request, MockHttpServletResponse response) {
		request.addParameter("X-UIClient", "G40");
		request.addParameter("SP-IO", "XML");

		request.addHeader("Content-Type", "multipart/form-data; boundary=sunny418&imagodei&comnik&chaser&batman&19981231");
		request.addHeader("X-UIClient", "G40/T1,2,1,45");

		HttpGauceRequestWrapper requestWrapper = new HttpGauceRequestWrapper(request, new DummyGauceContext());
		HttpGauceResponseWrapper responseWrapper = new HttpGauceResponseWrapper(response, new DummyGauceContext());

		try {
			return doDispatchInternal(requestWrapper, responseWrapper);
		} catch (Exception e) {
			throw new RuntimeException("JcfSuaTestServlet - doDispatchInternal", e);
		}
	}

	/**
	 * Webplus 요청을 처리
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String doDispatchForWebplus(MockHttpServletRequest request, MockHttpServletResponse response) {
		request.addHeader("Accept", "text/xml");

		try {
			return doDispatchInternal(request, response);
		} catch (Exception e) {
			throw new RuntimeException("JcfSuaTestServlet - doDispatchInternal", e);
		}
	}

	/**
	 * Xplatform 요청을 처리
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public String doDispatchForXplatform(MockHttpServletRequest request, MockHttpServletResponse response) {

		request.addHeader("User-Agent", "XPLATFORM");
		try {
			return doDispatchInternal(request, response);
		} catch (Exception e) {
			throw new RuntimeException("JcfSuaTestServlet - doDispatchInternal", e);
		}
	}



	/**
	 * Xplatform 요청을 처리
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public String doDispatchForMiplatform(MockHttpServletRequest request, MockHttpServletResponse response) {

		request.addHeader("User-Agent", "MiPlatform 3.2");
		try {
			return doDispatchInternal(request, response);
		} catch (Exception e) {
			throw new RuntimeException("JcfSuaTestServlet - doDispatchInternal", e);
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


		ModelAndView model = null;
		Exception ex = null;

		try	{
			/*
			 * HandlerInterceptor.preHandler 실행
			 */
			for (int i = 0; i < interceptors.length; ++i) {
				interceptors[i].preHandle(request, response, handler);
			}

			model = handlerAdapter.handle(request, response, handler);

			/*
			 * HandlerInterceptor.postHandler 실행
			 */
			for (int i = interceptors.length - 1; i >= 0; --i) {
				interceptors[i].postHandle(request, response,	handler, model);
			}
		}catch(Exception e){
			ex = e;
			model = processHandlerException(request, response, handler, e);
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
			interceptors[i].afterCompletion(request, response, handler, ex);
		}

		return getMockHttpServletResponse(response).getContentAsString();
	}

	protected ModelAndView processHandlerException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		ModelAndView exMv = null;

		for (int i = this.exceptionResolver.length - 1; i >= 0; --i) {
			HandlerExceptionResolver handlerExceptionResolver = exceptionResolver[i];

			exMv = handlerExceptionResolver.resolveException(request, response,	handler, ex);

			if (exMv != null) {
				break;
			}
		}

		if (exMv != null) {
			if (exMv.isEmpty()) {
				return null;
			}

			if (!exMv.hasView()) {
				exMv.setViewName("");
			}

			return exMv;
		}

		throw ex;
	}

	private MockHttpServletResponse getMockHttpServletResponse(HttpServletResponse response)	{
		if(response instanceof HttpGauceResponseWrapper)	{
			return (MockHttpServletResponse) ((HttpGauceResponseWrapper) response).getResponse();
		} else {
			return (MockHttpServletResponse) response;
		}
	}
}
