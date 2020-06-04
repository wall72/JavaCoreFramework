package jcf.dao.ibatis;

import jcf.dao.DataStreamingException;
import jcf.dao.StreamingRowHandler;

import org.springframework.dao.DataAccessException;

/**
 * iBATIS sqlMap에 접근하기 위한 인터페이스.
 * <ul>
 * <li>대용량 데이터 조회 기능 추가.</li>
 * <li>일반적인 JDBC 배치 인서트/업데이트 처리 기능 추가.</li>
 * </ul>
 * 
 * @author setq
 * 
 */
public interface BasicSqlMapClientOperations extends GenericSqlMapClientOperations {

	/**
	 * iBATIS sqlMap을 이용한 조회 결과를 {@link StreamingRowHandler} 를 통하여
	 * OutputStream으로 보낸다.
	 * <p>
	 * 마이플랫폼 CSV 예제코드 :
	 * <PRE>
	 * CodeController.java
	 * 
	 * &#64;RequestMapping
	 * public void findCodes(VariableList variableList, OutputStream outputStream) {
	 * 
	 * 	codeService.streamFindCodes(toMap(variableList),  new AbstractCsvStreamingRowHandler(outputStream, "DS_ABEE906") {
	 * 
	 *		&#64;Override
	 *		protected void writeRow(EntityWriter entityWriter, Object valueObject, int row) {
	 *			entityWriter.write(valueObject);
	 *		}
	 *
	 *		&#64;Override
	 *		protected String getCommaSeparatedColumnNames() {
	 *			return "ID, PARENTID, NAME, DESCRIPTION, INUSE, ALIAS, TYPE, CODELEVEL, ENGLISHNAME, DISPLAYVALUE";
	 *		}
	 *	});
	 * 
	 * CodeService.java
	 * 
	 * public void streamFindCodes(Map model, StreamingRowHandler rowHandler) {
	 * 	dao.stream("code.findCode", model, rowHandler);
	 * }
	 * </PRE>
	 * 
	 * @see AbstractCsvStreamingRowHandler
	 * 
	 * @param statementName
	 *            sqlMap의 id
	 * @param param
	 *            sqlMap에 보낼 파라미터
	 * @param streamingRowHandler
	 */
	void stream(String statementName, Object param, StreamingRowHandler streamingRowHandler) throws DataAccessException, DataStreamingException;

	/**
	 * iBATIS 일괄 sql insert.
	 * 
	 * @param statementId
	 *            sqlmap statement id
	 * @param listOfParameterObject
	 *            iBATIS sqlMap 파라미터 객체들의 리스트
	 * @param batchSize 한번에 JDBC BATCH로 보낼 행 수.
	 * 
	 * @return 인서트 실행한 구문 건 수
	 */
	int batchInsert(final String statementId, Iterable<?> listOfParameterObject, int batchSize) throws DataAccessException;
	

	/**
	 * iBATIS 일괄 sql insert.
	 * 
	 * @param statementId
	 *            sqlmap statement id
	 * @param listOfParameterObject
	 *            iBATIS sqlMap 파라미터 객체들의 리스트
	 * @return 인서트 실행한 구문 건 수
	 */
	 int batchInsert(final String statementId, Iterable<?> listOfParameterObject) throws DataAccessException;
	
	/**
	 * iBATIS 일괄 sql update.
	 * 
	 * @param statementId
	 *            sqlmap statement id
	 * @param listOfParameterObject
	 *            iBATIS sqlMap 파라미터 객체들의리스트
	 * @param batchSize 한번에 JDBC BATCH로 보낼 행 수.
	 * 
	 * @return 업데이트된 건 수 (주: 실행한 구문 수와 다를 수 있다.)
	 */
	int batchUpdate(final String statementId, Iterable<?> listOfParameterObject, int batchSize) throws DataAccessException;
	
	/**
	 * iBATIS 일괄 sql update.
	 * 
	 * @param statementId
	 *            sqlmap statement id
	 * @param listOfParameterObject
	 *            iBATIS sqlMap 파라미터 객체들의리스트
	 * @return 업데이트된 건 수 (주: 실행한 구문 수와 다를 수 있다.)
	 */
	int batchUpdate(final String statementId, Iterable<?> listOfParameterObject) throws DataAccessException;
	
}
