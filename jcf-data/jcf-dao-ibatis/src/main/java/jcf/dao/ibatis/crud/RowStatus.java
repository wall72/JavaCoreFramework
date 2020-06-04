package jcf.dao.ibatis.crud;

/**
 * 마이플랫폼 그리드 처리시 각 행의 업데이트 상태를 나타내기 위한 enumeration.
 * @author setq
 *
 */
public class RowStatus {
	
	// hide constructor
	private RowStatus() {}

	// 공통 상수
	public static final String DEFAULT_ROWSTATUS_PROPERTY_NAME = "rowStatus";
	public static final String DEFAULT_DATE_FORMAT = "yyyy/MM/dd";
	public static final String DEFAULT_ROW_STATUS_INSERT = "INSERT";
	public static final String DEFAULT_ROW_STATUS_UPDATE = "UPDATE";
	public static final String DEFAULT_ROW_STATUS_DELETE = "DELETE";
	public static final String DEFAULT_ROW_STATUS_UNKNOWN = "UNKNOWN";
}
