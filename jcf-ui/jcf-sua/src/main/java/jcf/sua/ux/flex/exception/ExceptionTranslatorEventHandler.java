package jcf.sua.ux.flex.exception;

import flex.messaging.MessageException;

public interface ExceptionTranslatorEventHandler {

	void postProcess(MessageException e);
}
