package jcf.query.core.handler.event;

import org.springframework.context.ApplicationListener;

/**
 *
 * @author nolang
 *
 */
public abstract class QueryEventListener implements ApplicationListener<QueryEvent> {

	public void onApplicationEvent(QueryEvent event) {
		process(event);
	}

	protected abstract void process(QueryEvent event);
}
