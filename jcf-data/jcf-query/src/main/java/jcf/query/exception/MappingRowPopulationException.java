package jcf.query.exception;

import org.springframework.core.NestedRuntimeException;

/**
 *
 * 결과 매핑 실패시 발생하는 예외
 *
 * @author nolang
 *
 */
@SuppressWarnings("serial")
public class MappingRowPopulationException extends NestedRuntimeException {

	public MappingRowPopulationException(String msg) {
		super(msg);
	}

	public MappingRowPopulationException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
