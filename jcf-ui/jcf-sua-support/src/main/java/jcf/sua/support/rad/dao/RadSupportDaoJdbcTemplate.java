package jcf.sua.support.rad.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jcf.data.handler.StreamHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

/**
 *
 * @author nolang
 *
 */
public class RadSupportDaoJdbcTemplate implements RadSupportDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private RowMapper<Map<String, Object>> rowMapper = new CamelCaseMapRowMapper();

	public List<?> selectList(String sqlId, Object object) throws DataAccessException {
		return jdbcTemplate.query(sqlId, rowMapper);
	}

	public void selectListByStream(String sqlId, Object object, final StreamHandler<Object> streamHandler) throws DataAccessException {

		try {
			streamHandler.open();

			jdbcTemplate.query(sqlId, new Object[] { object },	new RowCallbackHandler() {
						public void processRow(ResultSet rs) throws SQLException {
							streamHandler.handleRow(rowMapper.mapRow(rs, -1));
						}
					});
		} finally {
			streamHandler.close();
		}

	}

	public List<?> selectList(String sqlId, Object object, final int skipRows, final int maxRows) {

		final List<Map<String, ?>> list = new ArrayList<Map<String, ?>>();

		jdbcTemplate.query(sqlId, new Object[] { object },	new RowCallbackHandler() {
					int rowCount = 0;

					public void processRow(ResultSet rs) throws SQLException {
						if (rowCount > skipRows	&& rowCount++ - skipRows < maxRows) {
							list.add(rowMapper.mapRow(rs, rowCount));
						}
					}
				});

		return list;
	}

	public Object insertList(String sqlId, List<Map<String, String>> parameterList) {
		int count = 0;

		for (Map<String, String> map : parameterList) {
			count += jdbcTemplate.update(sqlId, map);
		}

		return count;
	}

	public Object updateList(String sqlId, List<Map<String, String>> parameterList) {
		int count = 0;

		for (Map<String, String> map : parameterList) {
			count += jdbcTemplate.update(sqlId, map);
		}

		return count;
	}

	public Object deleteList(String sqlId, List<Map<String, String>> parameterList) {
		int count = 0;

		for (Map<String, String> map : parameterList) {
			count += jdbcTemplate.update(sqlId, map);
		}

		return count;
	}

	static class CamelCaseMapRowMapper extends ColumnMapRowMapper	{
		@Override
		protected String getColumnKey(String columnName) {
			return JdbcUtils.convertUnderscoreNameToPropertyName(columnName);
		}
	}
}
