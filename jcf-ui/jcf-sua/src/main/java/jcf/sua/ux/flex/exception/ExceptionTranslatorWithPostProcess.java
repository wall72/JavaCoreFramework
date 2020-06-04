package jcf.sua.ux.flex.exception;

import jcf.sua.ux.flex.exception.MciExceptionTranslator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.flex.core.ExceptionTranslator;

import flex.messaging.MessageException;

public class ExceptionTranslatorWithPostProcess implements ExceptionTranslator{

	@Autowired
	private MciExceptionTranslator exceptionTranslator;

	private static final Logger logger = LoggerFactory.getLogger(ExceptionTranslatorWithPostProcess.class);

	private ExceptionTranslatorEventHandler eventHandler = new ExceptionTranslatorEventHandler() {

		public void postProcess(MessageException e) {
			if( logger.isDebugEnabled() ){
				logger.debug("Exception Message : {}, code : {}",e.getMessage(),e.getCode());
			}
		}
	};

	public boolean handles(Class<?> clazz) {
		return exceptionTranslator.handles(clazz);
	}

	public MessageException translate(Throwable t) {
		MessageException messageException = exceptionTranslator.translate(t);
		eventHandler.postProcess(messageException);

		return messageException;
	}

	public void setEventHandler(ExceptionTranslatorEventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}
}
