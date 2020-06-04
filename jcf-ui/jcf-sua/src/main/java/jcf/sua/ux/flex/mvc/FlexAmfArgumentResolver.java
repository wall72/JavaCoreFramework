package jcf.sua.ux.flex.mvc;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import jcf.sua.exception.MciException;
import jcf.sua.mvc.MciDataSetAccessor;
import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.MciRequestContextHolder;
import jcf.sua.mvc.MciResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import flex.messaging.messages.RemotingMessage;

/**
 *
 * @author nolang
 *
 */
public final class FlexAmfArgumentResolver implements ApplicationContextAware {

	private static final Logger logger = LoggerFactory.getLogger(FlexAmfArgumentResolver.class);

	private ApplicationContext context;

	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		this.context = context;
	}

	/**
	 *
	 * @param flexMessage
	 */
	public void resolveArgument(RemotingMessage flexMessage)	{
		if( logger.isDebugEnabled() ){
			logger.debug("FlexAmfArgumentResolver - resolveArgument");
		}

		/*
		 * 요청을 처리할 Controller 클래스 조회
		 */
		Object handler = getHandler(flexMessage.getDestination());

		/*
		 * 요청을 처리할 Method 조회
		 */
		Method handlerMethod = getHandlerMethod(handler, flexMessage.getOperation());

		if(handlerMethod == null){
			throw new MciException(String.format(
					"Method not found. [Hanlder:{%s}, method:{%s}, parameter:{%s, %s}]",
					handler.getClass(), flexMessage.getOperation(), MciRequest.class.getName(), MciResponse.class.getName()));
		}

		MciRequest mciRequest = new FlexRequest(MciRequestContextHolder.get().getDataSetReader());
		MciResponse mciResponse = new FlexResponse();

		MciRequestContextHolder.get().setDataSetAccessor((MciDataSetAccessor) mciResponse);

		List<Object> resolvedParameters = new ArrayList<Object>();

		resolvedParameters.add(mciRequest);
		resolvedParameters.add(mciResponse);

		flexMessage.setParameters(resolvedParameters);
	}

	private Object getHandler(String handlerId) {
		Object handlerObject = context.getBean(handlerId);

		if (handlerObject == null) {
			throw new MciException(String.format("Handler Object not found. - HandlerId={%s}", handlerId));
		}

		return handlerObject;
	}

	private Method getHandlerMethod(Object handler, String methodName) {
		try {
			return handler.getClass().getMethod(methodName, MciRequest.class,
					MciResponse.class);
		} catch (Exception e) {
			throw new MciException(String.format(
					"Method not found. [Hanlder:{%s}, method:{%s}, parameter:{%s, %s}",
					handler.getClass(), methodName, MciRequest.class.getName(), MciResponse.class.getName()));
		}
	}
}
