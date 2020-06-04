package jcf.dao.ibatis.crud.util;


public interface SaveStatusMapping {

	String getStatusPropertyName();

	String getStatusMappedSqlId(String status);

	boolean isInsert(String status);

	boolean isUpdate(String status);

	boolean isDelete(String status);
	
	String getStatusInfo();
}
