package jcf.query.exception;

import org.springframework.core.NestedRuntimeException;

/**
 *
 * 템플릿 엔진 초기화 실패시 발생하는 예외
 *
 * @author nolang
 *
 */
@SuppressWarnings("serial")
public class TemplateEngineInitialingException extends NestedRuntimeException {

	public TemplateEngineInitialingException(String msg) {
		super(msg);
	}

	public TemplateEngineInitialingException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
