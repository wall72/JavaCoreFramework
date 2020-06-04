package jcf.sua.ux.flex.exception;

import jcf.sua.mvc.MciRequestContextHolder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.flex.core.ExceptionTranslator;
import org.springframework.util.StringUtils;

import flex.messaging.MessageException;

public class MciExceptionTranslator implements ExceptionTranslator {

	@Autowired(required = false)
	private MessageSourceAccessor messageSourceAccessor;

	public boolean handles(Class<?> arg0) {
		return true;
	}

	public MessageException translate(Throwable t) {
		if(!MciRequestContextHolder.get().isMciRequest()){
			return null;
		}
		String message = t.getMessage();
		MessageException messageException = null;

		if(messageSourceAccessor != null){
			try{
				String remoteMessage = messageSourceAccessor.getMessage(message);
				messageException = buildRemoteMassage(message,remoteMessage,t);
			}catch (Exception NoSuchMessageException) {
				if (StringUtils.hasText(message)) {
					messageException = buildRemoteMassage("",message,t);
				}else{
					messageException = buildRemoteMassage("","",t);
				}
			}
		}

		return messageException;
	}

	private MessageException buildRemoteMassage(String code, String message, Throwable t){
		MessageException messageException = new MessageException(message, t);
		messageException.setCode(code);
		messageException.setDetails(message);
		return messageException;
	}

}
