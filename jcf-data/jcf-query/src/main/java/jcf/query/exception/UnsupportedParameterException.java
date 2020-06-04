package jcf.query.exception;

import org.springframework.core.NestedRuntimeException;

/**
 *
 * 허용되지 않는 타입이 Parameter로 입력된 경우 발생하는 예외
 *
 * @author nolang
 *
 */
@SuppressWarnings("serial")
public class UnsupportedParameterException extends NestedRuntimeException {

	public UnsupportedParameterException(String msg) {
		super(msg);
	}

	public UnsupportedParameterException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
