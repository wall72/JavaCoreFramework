package jcf.query.core.evaluator;

import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public class DummyDatabaseQueryEvaluator extends AbstractDatabaseQueryEvaluator {

	@Override
	protected String getStatement(SimpleJdbcTemplate jdbcTemplate, Object statementTemplate, Object param) {
		String query = jdbcTemplate.queryForObject("select query from jcf_sql where query_id = :queryId", String.class, (String) statementTemplate);
		return query;
	}

}
