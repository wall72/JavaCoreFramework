package jcf.query.core.evaluator;

import jcf.query.TemplateEngineType;
import jcf.query.core.QueryExecutor;
import jcf.query.core.QueryExecutorDelegator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class DatabaseQueryEvaluatorTest {

	@Autowired
	private QueryExecutor executor;

	@Autowired
	private AbstractDatabaseQueryEvaluator evaluator;

	@Test
	public void 쿼리생성테스트()	{
		QueryMetaData metadata = evaluator.evaluate("SELECT", null);

		System.out.println(metadata.getStatement());
	}

	@Test
	public void 쿼리실행테스트() {
		QueryExecutorDelegator delegator = new QueryExecutorDelegator();
		delegator.setQueryExecutor(executor);

		String query = delegator.withUserTemplate(TemplateEngineType.CUSTOM).getQueryExecutor().queryForObject("SELECT", "SELECT", String.class);

		System.out.println(query);
	}

}
