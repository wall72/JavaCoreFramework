package jcf.dao;

import jcf.data.handler.StreamHandler;



/**
 * 행 단위 스트리밍을 위한 핸들러.
 * <p>
 * @see jcf.dao.ibatis.BasicSqlMapClientOperations
 * @see StatementMappingDataAccessOperations
 *
 * @author setq
 *
 */
public interface StreamingRowHandler extends StreamHandler<Object> {

//	/**
//	 * 스트리밍 헤더 쓰기.
//	 */
//	void open() ;
//
//	/**
//	 * 스트리밍 마무리.
//	 */
//	void close();
//
//	/**
//	 * 행 단위 처리.
//	 * @param valueObject 행 데이터.
//	 */
//	void handleRow(Object valueObject);

}
