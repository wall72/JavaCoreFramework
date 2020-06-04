package jcf.dao;


public class DataStreamingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DataStreamingException(Exception e) {
		super(e);
	}

	public DataStreamingException(String msg, Exception e) {
		super(msg, e);
	}

	public DataStreamingException(String msg) {
		super(msg);
	}

}
