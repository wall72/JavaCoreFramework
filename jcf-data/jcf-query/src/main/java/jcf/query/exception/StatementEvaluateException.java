package jcf.query.exception;

import org.springframework.core.NestedRuntimeException;

/**
 *
 * Statement 생성 실패시 발생하는 예외
 *
 * @author nolang
 *
 */
@SuppressWarnings("serial")
public class StatementEvaluateException extends NestedRuntimeException {

	public StatementEvaluateException(String msg) {
		super(msg);
	}

	public StatementEvaluateException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
