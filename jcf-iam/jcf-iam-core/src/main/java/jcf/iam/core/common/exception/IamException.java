package jcf.iam.core.common.exception;

/**
 * <pre>
 * JCF IAM 시스템 정의 Exception
 * <pre>
 *
 * @author nolang
 *
 */
public class IamException extends RuntimeException {

	public IamException() {
		super();
	}

	public IamException(String message) {
		super(message);
	}

	public IamException(Exception e) {
		super(e);
	}

	public IamException(String message, Exception e) {
		super(message, e);
	}

}
