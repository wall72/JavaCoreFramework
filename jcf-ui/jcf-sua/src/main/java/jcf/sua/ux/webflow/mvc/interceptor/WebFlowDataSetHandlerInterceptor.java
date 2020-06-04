package jcf.sua.ux.webflow.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcf.sua.SuaChannels;
import jcf.sua.dataset.DataSetReader;
import jcf.sua.dataset.DataSetStreamWriter;
import jcf.sua.dataset.DataSetWriter;
import jcf.sua.mvc.MciDataSetAccessor;
import jcf.sua.mvc.MciRequestContextHolder;
import jcf.sua.mvc.interceptor.MciDataSetHandlerInterceptor;
import jcf.sua.ux.webflow.dataset.WebFlowDataSetReader;
import jcf.sua.ux.webflow.dataset.WebFlowDataSetWriter;

import org.springframework.web.servlet.ModelAndView;

/**
 *
 * {@link MciDataSetHandlerInterceptor} 의 표준웹 구현체
 *
 * @author nolang
 *
 */
public class WebFlowDataSetHandlerInterceptor extends MciDataSetHandlerInterceptor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		if(MciRequestContextHolder.get().getMciChannelType() == getChannelType())	{
			ModelAndView webflow =  getDataSetAccessor(request).getModelAndView();

			/*
			 * webflow의 경우에는 SpringMVC의 기본 flow를 어기면서 modelAndView를 사용하기 때문에
			 * 특수 케이스 (viewName null 등)에 대한 관련 로직이 안맞을 수가 있음.
			 * SpringMVC의 처리 흐름을 매번 모방해서 재구현해야 함.
			 *  
			 * viewName이 null인 경우는 일단 modelAndView를 clear하여 DispatcherServlet.render할 때
			 * 오류가 발생하지 않도록 막음
			 */
			String viewName = webflow.getViewName();
			if (viewName != null) {
				modelAndView.setViewName(viewName);
				modelAndView.addAllObjects(webflow.getModelMap());
				
			} else {
				modelAndView.clear();
			}
			
			if(getDataSetAccessor(request).isFileProcessing()){
				super.postHandle(request, response, handler, modelAndView);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SuaChannels getChannelType() {
		return SuaChannels.WEBFLOW;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSetReader getDataSetReader(HttpServletRequest request, HttpServletResponse response) {
		return new WebFlowDataSetReader(request, getFileOperator());
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSetWriter getDataSetWriter(HttpServletRequest request, HttpServletResponse response, MciDataSetAccessor accessor) {
		return new WebFlowDataSetWriter(request, response, getFileOperator(), accessor);
	}

	/**
	 * {@inheritDoc}
	 */
	protected DataSetStreamWriter getDataSetStreamWriter(HttpServletRequest request, HttpServletResponse response) {
		return null;
	}

	/**
	 * 이놈을 뭘로 구분해야할지 모르겠다 ㅜㅡ .. 일단, 채널의 가장 마지막에 위치시켜 ajax 요청이 아닌 놈들을 처리하는 것으로 함..
	 */
	@Override
	protected boolean checkMciRequest(HttpServletRequest request) {
		return true;
	}
}
