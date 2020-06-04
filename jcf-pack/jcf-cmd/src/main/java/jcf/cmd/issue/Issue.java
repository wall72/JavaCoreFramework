package jcf.cmd.issue;

public class Issue {

	private static final String DEFAULT_USER = "NONAME";
	private String name;
	private String message;
	private Throwable t;
	private String user;

	@Override
	public String toString() {
		return "[" + name + " by " + user + "] " + message + "\n" + t.getMessage();
	}
	
	public Issue(String name, String message, Throwable t) {
		this(name, message, t, DEFAULT_USER);
	}

	public Issue(String name, String message, Throwable t, String user) {
		this.name = name;
		this.message = message;
		this.t = t;
		this.user = user;
	}

	public String getName() {
		return name;
	}
	
	public String getMessage() {
		return message;
	}
	
	public Throwable getThrowable() {
		return t;
	}

	public String getUser() {
		return user;
	}
	
}
