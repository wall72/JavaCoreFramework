package jcf.query.core.handler.event;

import jcf.query.core.QueryExecutor;
import jcf.query.core.QueryExecutorDelegator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author nolang
 *
 */
public class QueryEventRecordListener extends QueryEventListener implements InitializingBean {

	@Autowired
	private QueryExecutor queryExecutor;
	private QueryExecutorDelegator delegator;

	private QueryEventRepository repository;

	@Override
	protected void process(QueryEvent event) {
		if(repository != null){
			delegator.withoutQueryEvent().getQueryExecutor().update(repository.getStatement(), event);
		}
	}

	public void afterPropertiesSet() throws Exception {
		delegator = new QueryExecutorDelegator();
		delegator.setQueryExecutor(queryExecutor);
	}

	public void setRepository(QueryEventRepository repository) {
		this.repository = repository;
	}

}
