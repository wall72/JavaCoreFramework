package jcf.util.security.cipher;



public class CipherException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CipherException(String message, Throwable t) {
		super(message, t);
	}

	public CipherException(Throwable t) {
		super(t);
	}

}
