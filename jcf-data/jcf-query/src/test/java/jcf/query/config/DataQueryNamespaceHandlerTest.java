package jcf.query.config;

import jcf.query.core.QueryExecutor;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-namespace.xml" })
public class DataQueryNamespaceHandlerTest {

	@Autowired
	private QueryExecutor queryExecutor;

	@Test
	public void testNamespaceHandler() {
		Assert.assertNotNull(queryExecutor);

		vararrays("COLUMN", new String[]{"1", "2", "3"});
	}


	private void vararrays(Object...objects)	{
		System.out.println(objects.length);
	}
}
