package jcf.sua.mvc;

import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * 시스템에서 발생한 예외에 대한 사용자 메세지를 정의한다.
 *
 * @author nolang
 *
 */
public interface MciExceptionMessageWriter {

	/**
	 *
	 * 처리가능한 예외인지..?
	 *
	 * @param exception
	 * @return
	 */
	boolean accept(Exception exception);

	/**
	 *
	 * 사용자 예외 메세지를 생성한다.
	 *
	 * @param messageSource (nullable)
	 * @param exception
	 * @return
	 */
	String buildExceptionMessage(MessageSourceAccessor messageSource, Exception exception);

}
