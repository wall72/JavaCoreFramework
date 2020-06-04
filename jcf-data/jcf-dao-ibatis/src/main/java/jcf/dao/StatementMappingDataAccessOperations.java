package jcf.dao;

import java.util.List;
import java.util.Map;

import jcf.dao.ibatis.crud.ExecutionResult;

import org.springframework.dao.DataAccessException;

@SuppressWarnings("rawtypes")
public interface StatementMappingDataAccessOperations {

	/**
	 * <p>입력된 sqlId에 매핑되는 insert SQL을 실행시켜준다. 실행될 SQL 문의 파라미터로 object를 사용한다.
	 *
	 * @param sqlId 실행시킬 SQL 문의 아이디
	 * @param object SQL문에서 파라미터로 사용할 객체
	 * @return 0, 내부 구현과 설정에 따라 다른 값을 반환할 수 있다.
	 */
	Object insert(String sqlId, Object object);


	/**
	 * <p>입력된 sqlId에 매핑되는 update SQL을 실행시켜준다. 실행될 SQL 문의 파라미터로 object를 사용한다.
	 *
	 * @param sqlId 실행시킬 SQL 문의 아이디
	 * @param object SQL문에서 파라미터로 사용할 객체
	 * @return affected rows, 업데이트 된 레코드의 수를 반환한다.
	 */
	Object update(String sqlId, Object object);
	/**
	 * <p>입력된 sqlId에 매핑되는 update SQL을 실행시켜준다. 실행될 SQL 문의 파라미터로 object를 사용한다.
	 *
	 * @param sqlId 실행시킬 SQL 문의 아이디
	 * @param object SQL문에서 파라미터로 사용할 객체
	 * @Parem 업데이트 되어야 하는 데이터의 건수
	 * @return affected rows, 업데이트 된 레코드의 수를 반환한다.
	 */
	void update(String sqlId, Object object, int requiredRowsAffected);

	/**
	 * <p>입력된 sqlId에 매핑되는 delete SQL을 실행시켜준다. 실행될 SQL 문의 파라미터로 object를 사용한다.
	 *
	 * @param sqlId 실행시킬 SQL 문의 아이디
	 * @param object SQL문에서 파라미터로 사용할 객체
	 * @return affected rows, 삭제된 레코드의 수를 반환한다.
	 */
	Object delete(String sqlId, Object object);


	/**
	 * <p>입력된 sqlId에 매핑되는 delete SQL을 실행시켜준다. 실행될 SQL 문의 파라미터로 object를 사용한다.
	 *
	 * @param sqlId 실행시킬 SQL 문의 아이디
	 * @param object SQL문에서 파라미터로 사용할 객체
	 * @Parem 삭제되어야 하는 데이터의 건수
	 * @return affected rows, 삭제된 레코드의 수를 반환한다.
	 */
	void delete(String sqlId, Object object, int requiredRowsAffected);

	/**
	 * <p>sqlId에 매핑되는 조회문을 실행하여 결과를 반환한다. 파라미터 object를 조회 파라미터로 사용한다.
	 *
	 * @param sqlId 실행시킬 SQL 문의 아이디
	 * @param object SQL문에서 파라미터로 사용할 객체
	 * @return object, 조회된 한건의 데이터 객체를 반환한다. 조회 결과가 없는 경우 null을 반환.
	 */
	Object select(String sqlId, Object object);

	/**
	 * <p>입력된 object 파라미터를 저장(입력/수정/삭제)한다.
	 *
	 * <p>statusWildcardQueryMapping는 와일드 카드를 갖는 매핑 스트링을 받는다.
	 * 파라미터 statusWildcardQueryMapping는은 객체가 가지는 입력/수정/삭제의 상태값에 따라 수행될 적절한 sqlId를 매핑하기 위한 정보이다.
	 * <ul>
	 * <li>"user.*User"를 입력한 경우, user.insertUser, user.updateUser, user.deleteUser에 매핑
	 * </ul>
	 *
	 * @param statusToStatementMapping 입력/수정/삭제 SQL과 상태값을 매핑하는 매핑 객체.
	 * @param object 저장할 파라미터 객체
	 * @return 0, 구현에 따라 다른 값을 반환할 수 있다.
	 */
	Object save(String statusWildcardQueryMapping, Object object);


	/**
	 * 대용량 조회 시 Stream 방식을 사용하여  한건 한건 조회 시 화면에 바로 쏴주는 형태의 조회방식이다.
	 * 가이드 참고 http://wiki.expertvill.net/confluence/pages/viewpage.action?pageId=33256
	 * 사용법( Controller 에서 호출하기를 권장)
	 *
	@Autowired private CommonDAOImpl dao;
	@RequestMapping
	public void streamCodes(HttpCache cache, VariableList variableList, OutputStream outputStream) {
	   dao.selectListbyStream("CODE.selectCode", toMap(variableList),  new AbstractCsvStreamingRowHandler(outputStream, "DS_ABEE906") {
	   @Override
	    protected String getCommaSeparatedColumnNames() {
	   return "ID, PARENTID, NAME, DESCRIPTION, INUSE, ALIAS, TYPE, CODELEVEL, ENGLISHNAME, DISPLAYVALUE";
	}
	});
	}
	 * @param statementName
	 * @param param
	 * @param streamingRowHandler
	 */
	 void selectListbyStream(String statementName, Object param,	StreamingRowHandler streamingRowHandler);

	/**
	 * <p>입력된 sqlId에 매핑되는 insert SQL을 배치로 실행시켜준다. 실행될 SQL 문의 파라미터로 list를 사용한다.
	 *
	 * @param sqlId 실행시킬 SQL 문의 아이디
	 * @param list SQL문에서 파라미터로 사용할 객체
	 * @return 0, 내부 구현과 설정에 따라 다른 값을 반환할 수 있다.
	 */
	Object insertList(String sqlId, List list);

	/**
	 * <p>입력된 sqlId에 매핑되는 update SQL을 배치로 실행시켜준다. 실행될 SQL 문의 파라미터로 list를 사용한다.
	 *
	 * @param sqlId 실행시킬 SQL 문의 아이디
	 * @param list SQL문에서 파라미터로 사용할 객체
	 * @return 0, 내부 구현과 설정에 따라 다른 값을 반환할 수 있다.
	 */
	Object updateList(String sqlId, List list);

	/**
	 * <p>입력된 sqlId에 매핑되는 delete SQL을 배치로 실행시켜준다. 실행될 SQL 문의 파라미터로 list를 사용한다.
	 *
	 * @param sqlId 실행시킬 SQL 문의 아이디
	 * @param list SQL문에서 파라미터로 사용할 객체
	 * @return 0, 내부 구현과 설정에 따라 다른 값을 반환할 수 있다.
	 */
	Object deleteList(String sqlId, List list);

	/**
	 * <p>sqlId에 매핑되는 조회문을 실행하여 결과를 반환한다. 파라미터 object를 조회 파라미터로 사용한다.
	 *
	 * @param sqlId 실행시킬 SQL 문의 아이디
	 * @param object SQL문에서 파라미터로 사용할 객체
	 * @return List, 조회된 데이터 객체리스트를 반환한다. 조회 결과가 없는 경우 size가 0 인 List 객체를 반환.
	 */
	List selectList(String sqlId, Object object);

	/**
	 * <p>입력된 객체 List 파라미터를 배치로 저장(입력/수정/삭제)한다.
	 *
	 * <p>statusWildcardQueryMapping는 와일드 카드를 갖는 매핑 스트링을 받는다.
	 * 파라미터 statusWildcardQueryMapping은 객체가 가지는 입력/수정/삭제의 상태값에 따라 수행될 적절한 sqlId를 매핑하기 위한 정보이다.
	 * <ul>
	 * <li>"user.*User"를 입력한 경우, user.insertUser, user.updateUser, user.deleteUser에 매핑
	 * </ul>
	 *
	 *
	 * @param statusWildcardQueryMapping 입력/수정/삭제 SQL과 상태값을 매핑하는 매핑 스트링 정보.
	 * @param list 저장할 객체 리스트
	 * @return 0, 구현에 따라 다른 값을 반환할 수 있다.
	 */
	Object saveList(String statusWildcardQueryMapping, List list);

	/**
	 * <p>입력된 객체 List 파라미터를 배치로 저장(입력/수정/삭제)한다.
	 *
	 * <p>statusWildcardQueryMapping는 와일드 카드를 갖는 매핑 스트링을 받는다.
	 * 파라미터 statusWildcardQueryMapping은 객체가 가지는 입력/수정/삭제의 상태값에 따라 수행될 적절한 sqlId를 매핑하기 위한 정보이다.
	 * 입력/수정/삭제 시 발생한 에러를 ExecutionResult에 담에 리턴한다.
	 * <ul>
	 * <li>"user.*User"를 입력한 경우, user.insertUser, user.updateUser, user.deleteUser에 매핑
	 * </ul>
	 *
	 *
	 * @param statusWildcardQueryMapping 입력/수정/삭제 SQL과 상태값을 매핑하는 매핑 스트링 정보.
	 * @param list 저장할 객체 리스트
	 * @return 0, 구현에 따라 다른 값을 반환할 수 있다.
	 */

	List<ExecutionResult>   saveListWithoutRollback(String statusWildcardQueryMapping, List list);

	/**
	 *  <p> 맵형태로 데이터 조회한다.  keyProperty는 맵아이디로 지정할  컬럼값 또는 리절트맵에 바인딩된 값이다.
	 * 예를 들어  keyProperty를 id 로 지정하면 해당 데이터는 맵에 담기고  id에 바인딩된 값이 맵 아이디가 된다.
	 * 참고 주소 http://openframework.or.kr/Wiki.jsp?page=QueryForMapExample
	 * @param sqlId
	 * @param object
	 * @param keyProperty
	 * @param valueProperty
	 * @return
	 * @throws DataAccessException
	 */

	 Map selectMap(String sqlId, Object object, String keyProperty);


		/**
		 * <p> 맵형태로 데이터 조회함   keyProperty는  컬럼값 또는 리절트맵에 바인딩된 값이고  valueProperty 특정 컬럼만 조회할 때 지정한다.
		 * 예를 들어  keyProperty를 id 로 지정하고 valueProperty는 password로 지정하면
		 *  password만  맵에 담기고 맵 아이디는  id에 바인딩된 값이 된다.
		 * 참고 주소 http://openframework.or.kr/Wiki.jsp?page=QueryForMapExample
		 * @param sqlId
		 * @param object
		 * @param keyProperty
		 * @param valueProperty
		 * @return
		 * @throws DataAccessException
		 */
	  Map selectMap(String sqlId, Object object, String keyProperty,String valueProperty );

		/**
		 * <p>
		 * 데이터를 page 단위로 조회함
		 *
		 * @param sqlId
		 * @param object
		 * @param skipRows
		 * @param maxRows
		 * @return
		 */
	  List selectList(String sqlId, Object object, int skipRows, int maxRows);
}
