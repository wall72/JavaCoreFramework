package jcf.sua.ux.flex.mvc.interceptor;

import jcf.sua.MessageHeaders;
import jcf.sua.dataset.DataSetReader;
import jcf.sua.mvc.MciDataSetAccessor;
import jcf.sua.mvc.MciRequestContextHolder;
import jcf.sua.ux.flex.FlexMessage;
import jcf.sua.ux.flex.FlexMessagePayload;
import jcf.sua.ux.flex.dataset.FlexAmfDataSetConverter;
import jcf.sua.ux.flex.dataset.FlexAmfDataSetReader;
import jcf.sua.ux.flex.mvc.FlexAmfArgumentResolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.flex.core.MessageInterceptor;
import org.springframework.flex.core.MessageProcessingContext;

import flex.messaging.FlexContext;
import flex.messaging.messages.Message;
import flex.messaging.messages.RemotingMessage;

/**
 *
 * @author nolang
 *
 */
public final class FlexAmfMessageBrokerInterceptor implements MessageInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(FlexAmfMessageBrokerInterceptor.class);

	private FlexAmfDataSetConverter converter = new FlexAmfDataSetConverter();
	private FlexAmfArgumentResolver argumentResolver;

	public void setArgumentResolver(FlexAmfArgumentResolver argumentResolver) {
		this.argumentResolver = argumentResolver;
	}

	public Message preProcess(MessageProcessingContext context,
			Message inputMessage) {
		if( logger.isDebugEnabled() ){
			logger.debug("FlexAmfMessageBrokerInterceptor - preProcess");
		}
		MciRequestContextHolder.get().setMciRequest(checkMciRequest(inputMessage));

		MciRequestContextHolder.get().setHttpServletRequest(FlexContext.getHttpRequest());
		MciRequestContextHolder.get().setHttpServletResponse(FlexContext.getHttpResponse());

		if(MciRequestContextHolder.get().isMciRequest()){
			MciRequestContextHolder.get().setDataSetReader(getDataSetReader((RemotingMessage) inputMessage));

			/*
			 * Arguments Resolving...
			 */
			argumentResolver.resolveArgument((RemotingMessage) inputMessage);
		}

		return inputMessage;
	}
	
	public Message postProcess(MessageProcessingContext context,
			Message inputMessage, Message outputMessage) {

		if( logger.isDebugEnabled() ){
			logger.debug("FlexAmfMessageBrokerInterceptor - postProcess");
		}

		if(MciRequestContextHolder.get().isMciRequest()){
			/*
			 * Builds output message
			 */
			outputMessage.setBody(buildFlexMessage(MciRequestContextHolder.get().getDataSetAccessor()));
		}

		MciRequestContextHolder.clear();

		return outputMessage;
	}

	private FlexMessage<FlexMessagePayload> buildFlexMessage(MciDataSetAccessor dataSetAccessor) {
		FlexMessage<FlexMessagePayload> flexMessage = new FlexMessage<FlexMessagePayload>();
		FlexMessagePayload flexMessagePayload = new FlexMessagePayload();

		flexMessagePayload.setDataSetMap(converter.toFlexAmfDataSet(dataSetAccessor.getDataSetMap()));

		MessageHeaders headers = new MessageHeaders();
		headers.setSuccesMessages(dataSetAccessor.getSuccessMessags());

		flexMessage.setMessageHeaders(headers);
		flexMessage.setPayload(flexMessagePayload);

		return flexMessage;
	}

	/**
	 *
	 * @return
	 */
	private  DataSetReader getDataSetReader(RemotingMessage message){
		return new FlexAmfDataSetReader(message);
	}

	/**
	 *
	 * @param request
	 * @return
	 */
	private boolean checkMciRequest(Message message) {
		return (message != null && message instanceof RemotingMessage);
	}
}
