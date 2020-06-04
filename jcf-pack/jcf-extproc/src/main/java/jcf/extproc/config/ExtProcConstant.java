package jcf.extproc.config;

public class ExtProcConstant {

	public static final String DATE_FORMAT = "yyyy-MM-dd_HH-mm-ss";
	public static final String LOGFILE_NAME = "stdout.log";
	public static final String END_MARKER = "__END_PROCESS__";


	public static final boolean IS_AT_LEAST_JAVA5;
	
	/*
	 * externalProcessOperator에 실행되는 프로세스의 환경변수에 주입되는 환경변수.
	 */
	public static final String ENV_JOB_NAME = "JOB_NAME";
	public static final String ENV_JOB_INSTANCE = "JOB_INSTANCE";
	public static final String ENV_JOB_INSTANCE_NAME = "JOB_INSTANCE_NAME";
	public static final String ENV_USER_NAME = "JOB_USER";
	public static final String ENV_JOB_DESCRIPTION = "JOB_DESCRIPTION";
	
	static {
		String javaVersion = System.getProperty("java.version");
		// version String should look like "1.4.2_10"
		if (javaVersion.contains("1.7.") || 
				javaVersion.contains("1.6.") ||
				javaVersion.contains("1.5.")) {
			IS_AT_LEAST_JAVA5 = true;
			
		} else {
			IS_AT_LEAST_JAVA5 = false;
		}
	}
	
}
