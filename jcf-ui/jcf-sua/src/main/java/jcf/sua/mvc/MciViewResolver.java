package jcf.sua.mvc;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jcf.sua.SuaChannels;
import jcf.sua.SuaConstants;
import jcf.sua.exception.MciException;
import jcf.sua.mvc.view.MciView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

/**
*
* 요청 처리에 적합한 View를 검색하여 적용한다.
*
* @author nolang
*/
public class MciViewResolver implements ViewResolver, Ordered {

	private static final Logger logger = LoggerFactory.getLogger(MciViewResolver.class);

	private Map<String, View> viewMap;
	private View defaultView;
	private int order;
	
	private ViewResolver[] viewResolvers;

	public MciViewResolver() {
		this.viewMap = new HashMap<String, View>();
		this.viewMap.put(SuaConstants.STREAMING, new MciView());
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.web.servlet.ViewResolver#resolveViewName(java.lang.String, java.util.Locale)
	 */
	public View resolveViewName(String viewName, Locale locale) throws Exception {
		View view = null;

		if (viewMap != null) {
			view = viewMap.get(viewName);
		}
		
		MciRequestContext requestContext = MciRequestContextHolder.get();
		ModelAndView modelAndView = requestContext.getDataSetAccessor().getModelAndView();
		String mciViewName = modelAndView.getViewName();
		
		if(mciViewName != null && mciViewName.equals(SuaConstants.STREAMING)){
			view = defaultView;
		} else {
			if(mciViewName == null){
				if(SuaChannels.WEBFLOW.equals(requestContext.getMciChannelType())){
					throw new MciException("[JCF-SUA] : View name must not be null in native Web Channel");
				} 
				
				view = defaultView;
			} else {
				for(ViewResolver viewResolver : viewResolvers){
					View resolvedView = viewResolver.resolveViewName(mciViewName, locale);
					
					if(resolvedView != null){
						view = resolvedView;
						break;
					}
				}
			}
		}
		
		if(logger.isDebugEnabled()){
			logger.trace("[JCF-SUA] {} 뷰생성 작업을 수행합니다. : ViewClass={}, ViewName={}", new Object[]{MciRequestContextHolder.get().getMciChannelType(), view.getClass(), viewName});
		}

		return view;
	}

	/**
	 * View resolver가 여러 개일 때 적용순서를 return
	 */
	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public void setDefaultView(View defaultView) {
		this.defaultView = defaultView;
	}

	public void setViewMap(Map<String, View> viewMap) {
		this.viewMap = viewMap;
	}

	public void setViewResolvers(ViewResolver[] viewResolvers) {
		this.viewResolvers = viewResolvers;
	}

}
