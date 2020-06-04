package jcf.query.core.handler.event;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-event.xml" })
public class QueryEventEventTest {

	@Autowired
	private QueryEventPublisher publisher;

	@Test
	public void testEventPublish() throws Exception	{
		publisher.publishEvent("select sysdate from dual", null, System.currentTimeMillis(), System.currentTimeMillis(), null);
	}
}
