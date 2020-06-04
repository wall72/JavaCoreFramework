package jcf.sua.exception;

/**
*
* 채널 기반 요청 처리중 발생하는 예외
*
* @author nolang
*
*/
public class MciException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 8737632253185272562L;

	public MciException() {
		super();
	}

	public MciException(String msg) {
		super(msg);
	}

	public MciException(Exception e) {
		super(e);
	}

	public MciException(String msg, Exception e) {
		super(msg, e);
	}
}
