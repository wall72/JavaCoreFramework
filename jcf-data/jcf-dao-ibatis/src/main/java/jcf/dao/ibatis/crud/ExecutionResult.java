package jcf.dao.ibatis.crud;

public interface ExecutionResult {

	int getAffectedRows();

	Exception getException();

	String getOperation();
}
