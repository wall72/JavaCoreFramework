package jcf.query.core.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

/**
 *
 * @author nolang
 *
 */
public class CamelCaseMapRowMapper implements RowMapper<Map<String, Object>>	{

	private static final Logger logger = LoggerFactory.getLogger(CamelCaseMapRowMapper.class);

	public Map<String, Object> mapRow(ResultSet rs, int rowNum)	throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		Map<String, Object> mapOfColValues = new HashMap<String, Object>(columnCount);

		for (int i = 1; i <= columnCount; i++) {
			String key = getColumnKey(JdbcUtils.lookupColumnName(rsmd, i));
			Object obj = getColumnValue(rs, i);
			mapOfColValues.put(key, obj);
		}
		return mapOfColValues;
	}

	protected String getColumnKey(String columnName) {
		return JdbcUtils.convertUnderscoreNameToPropertyName(columnName);
	}

	protected Object getColumnValue(ResultSet rs, int index) throws SQLException {
		return JdbcUtils.getResultSetValue(rs, index);
	}

}