package jcf.query.core;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import jcf.query.core.decorator.aspect.CacheAspect;
import jcf.query.domain.Product;
import net.sf.ehcache.CacheManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import statement.SampleStatement;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-namespace.xml" })
public class QueryExecutorWrapperTest {

	@Autowired
	private QueryExecutor queryExecutor;
	private QueryExecutorDelegator queryDelegator;
	private QueryExecutorWrapper queryWrapper;
	private QueryExecutorWrapperDelegator queryWrapperDelegator;

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private CacheAspect cacheAspect;

	@Before
	public void setup()	{
		queryWrapper = new QueryExecutorWrapper();
		queryWrapper.setQueryExecutor(queryExecutor);
		queryWrapper.setCacheManager(cacheManager);
		queryWrapperDelegator = new QueryExecutorWrapperDelegator();
		queryWrapperDelegator.setQueryExecutorWrapper(queryWrapper);

		queryDelegator = new QueryExecutorDelegator();
		queryDelegator.setQueryExecutor(queryExecutor);
	}

	@After
	public void teardown()	{
		cacheManager.getCache("testCache").removeAll();
	}

	@Test
	public void testGenerateCacheKey() {
		Product product1 = new Product();
		product1.setProductId(1004);

		Product product2 = new Product();
		product2.setProductId(1004);

		assertEquals(queryWrapper.generateKey(SampleStatement.selectProductsWithIfStatment, product1, Product.class),
				queryWrapper.generateKey(SampleStatement.selectProductsWithIfStatment, product2, Product.class));
	}

	@Test
	public void testCacheControl() {
		Product product = new Product();
		product.setProductId(1004);

		List<Product> expected = queryWrapperDelegator.withCache("PRODUCT_CACHE").getQueryExecutorWrapper().queryForList(SampleStatement.selectProductsWithIfStatment, product, Product.class);

		Object cacheKey = queryWrapper.generateKey(SampleStatement.selectProductsWithIfStatment, product, Product.class);

		List<Product> actual = (List<Product>) cacheManager.getCache("testCache").get(cacheKey).getObjectValue();

		assertThat(expected, is(actual));
	}

	@Test
	public void testCacheAspect() {
		Product product = new Product();
		product.setProductId(1004);

		List<Product> expected = queryDelegator.withCache("PRODUCT_CACHE").getQueryExecutor().queryForList(SampleStatement.selectProductsWithIfStatment, product, Product.class);

		Object cacheKey = cacheAspect.generateKey(SampleStatement.selectProductsWithIfStatment, product, Product.class);
		List<Product> actual = (List<Product>) cacheManager.getCache("testCache").get(cacheKey).getObjectValue();

		assertThat(expected, is(actual));

	}
}
