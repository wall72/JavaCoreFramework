package jcf.sua.ux;

public class UxConstants {
	public static final String DEFAULT_ROWSTATUS_PROPERTY_NAME = "rowStatus";
	public static final String MYBUILDER_ROWSTATUS_PROPERTY_NAME = "_status_";

	public static final String DEFAULT_CHARSET = "utf-8"; // 디폴트는 utf-8 이다.

	public static final char WEBPLUS_DELIMETER = '&'; // 웹플러스 요청시 사용되는 딜리미터

	public static final String ERROR_CODE_PARAMETER_NAME = "ErrorCode";  //에러 코드  이름  셋팅
	public static final String ERROR_CODE_SUCC_VALUE = "0";  // 기본 에러 코드  값  셋팅
	public static final String ERROR_CODE_FAIL_VALUE = "-1";  //에러  발생 시 에러 코드  값  셋팅

	public static final String ERROR_MSG_PARAMETER_NAME = "ErrorMsg"; // 에러 메시지 셋팅
	public static final String ERROR_MSG_DEFULT_VALUE = "SUCCESS"; // 에러 메시지 값  셋팅

	public class Gauce	{
		public static final String COLUMN_FILENAME = "fileName"; // 파일이름을 저장하는 컬럼이름
		public static final String COLUMN_FILEURL = "fileUrl";   // 파일스트림 데이터가 저장된 컬럼이름
	}

}
