package jcf.sua.ux.gauce.exception;

import jcf.sua.exception.MciException;

/**
 *
 * 가우스 채널 처리중 발생하는 예외
 *
 * @author nolang
 *
 */
public class GauceParserException extends MciException {

	/**
	 *
	 */
	private static final long serialVersionUID = -4096148328021624812L;

	public GauceParserException(String msg, Exception e) {
		super(msg, e);
	}

}
