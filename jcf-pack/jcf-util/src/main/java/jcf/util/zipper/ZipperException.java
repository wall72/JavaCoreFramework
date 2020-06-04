package jcf.util.zipper;

public class ZipperException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ZipperException(String message, Throwable t) {
		super(message, t);
	}

	public ZipperException(String message) {
		super(message);
	}

}
