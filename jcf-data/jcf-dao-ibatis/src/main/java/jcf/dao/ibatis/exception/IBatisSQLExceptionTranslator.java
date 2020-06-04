package jcf.dao.ibatis.exception;

import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

/**
 * iBATIS 행반환수 오류시 오류 변환기.
 * <p>
 * 반환된 데이터의 행이 기대치에 어긋나는 상황이 발생했을 때
 * iBATIS에서 임의로 SQLException을 만들어서 던지기 때문에
 * 해당 오류를 스프링의 데이터액세스 오류체계에 맞는 오류로 변경하여 던져준다.
 * <p>
 * 각각의 sqlMapClientTemplate와 jdbcTemplate에 등록해서 사용하도록 한다.  
 * 
 * @author setq
 *
 */
public class IBatisSQLExceptionTranslator extends
		SQLErrorCodeSQLExceptionTranslator {

	protected DataAccessException customTranslate(String task, String sql,
			SQLException sqlEx) {

		if (sqlEx.getSQLState() == null && sqlEx.getErrorCode() == 0) {
			if ("Error: executeQueryForObject returned too many results."
					.equals(sqlEx.getMessage())) {
				throw new IncorrectResultSizeDataAccessException(sqlEx.getMessage(), 1);
			}
		}

		return null;
	}
}
