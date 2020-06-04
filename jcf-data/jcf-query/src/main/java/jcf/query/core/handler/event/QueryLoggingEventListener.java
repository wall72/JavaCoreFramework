package jcf.query.core.handler.event;

import java.util.Date;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nolang
 *
 */
public class QueryLoggingEventListener extends QueryEventListener {

	private static final Logger logger = LoggerFactory.getLogger(QueryLoggingEventListener.class);

	@Override
	protected void process(QueryEvent event) {
		logger.debug(
				"Statement : {}, Parameter : {}, 시작시간 : {}, 종료시간 : {}, 수행시간 : {}ms",
				new Object[] { event.getStatement(), event.getParameter(),
						new Date(event.getStartTime()), new Date(event.getEndTime()),
						new Date(event.getEndTime() - event.getStartTime()) });

		if(event.getException() != null)	{
			logger.debug(
					"Exception : {}", ExceptionUtils.getRootCauseMessage(event.getException()));
		}
	}

}
