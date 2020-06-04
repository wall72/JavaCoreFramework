package jcf.sua.exception;

/**
*
* 시스템에서 발생하는 Biz예외를 UI Message로 전달하기 위한 예외
*
* @author nolang
*
*/
@SuppressWarnings("serial")
public class SuaBusinessException extends RuntimeException {

	protected String code;
	protected String defaultMessage;
	protected Object[] arguments;
	protected Exception exception;

	public SuaBusinessException(String code) {
		this(code, "", null, null);
	}

	public SuaBusinessException(String code, Exception ex) {
		this(code, "", null, ex);
	}

	public SuaBusinessException(String code, Object[] arguments) {
		this(code, "", arguments, null);
	}

	public SuaBusinessException(String code, Object[] arguments, Exception ex) {
		this(code, "", arguments, ex);
	}

	public SuaBusinessException(String code, String defaultMessage, Object[] arguments) {
		this(code, defaultMessage, arguments, null);
	}

	public SuaBusinessException(String code, String defaultMessage, Object[] arguments, Exception ex) {
		super(code, ex);

		this.code = code;
		this.defaultMessage = defaultMessage;
		this.arguments = arguments;
		this.exception = ex;
	}

	/**
	 *
	 * @return
	 */
	public String getCode() {
		return code;
	}

	/**
	 *
	 * @return
	 */
	public String getDefaultMessage() {
		return defaultMessage;
	}

	/**
	 *
	 * @return
	 */
	public Object[] getArguments() {
		return arguments;
	}

	/**
	 *
	 * @return
	 */
	public Exception getException() {
		return exception;
	}
}
