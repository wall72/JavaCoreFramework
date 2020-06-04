package jcf.query.core;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import jcf.query.core.evaluator.SimpleORMQueryType;
import jcf.query.domain.ProductRelationMapping;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-orm.xml" })
public class SimpleORMQueryExecutorTest {

	@Autowired
	private QueryExecutor queryExecutor;

	@Test
	public void testSelectStatement() {
		ProductRelationMapping product = new ProductRelationMapping();
		product.setProductId(1004);

		List<ProductRelationMapping> list = queryExecutor.queryForList(SimpleORMQueryType.SELECT, product, ProductRelationMapping.class);

		assertThat(1, is(list.size()));

		ProductRelationMapping actual = list.get(0);

		assertThat(1004, is(actual.getProductId()));
		assertThat("CARRIERS", is(actual.getProductTypeId()));
		assertThat("FPSO선", is(actual.getProductName()));
		assertThat("Floating Production Storage Offloading, 부유식 원유생산저장 설비", is(actual.getProductDescription()));
	}

	@Test
	public void testUpdate()	{
		ProductRelationMapping insert = new ProductRelationMapping();

		insert.setProductId(1005);
		insert.setProductTypeId("CARRIERS");
		insert.setProductName("비싼배");
		insert.setProductDescription("초 크다..");

		int insertCount = queryExecutor.update(SimpleORMQueryType.INSERT, insert);

		assertThat(1, is(insertCount));

		ProductRelationMapping product = queryExecutor.queryForObject(SimpleORMQueryType.SELECT, insert, ProductRelationMapping.class);

		assertNotNull(product);
		assertThat(1005, is(product.getProductId()));
		assertThat("CARRIERS", is(product.getProductTypeId()));
		assertThat("비싼배", is(product.getProductName()));
		assertThat("초 크다..", is(product.getProductDescription()));

		ProductRelationMapping update = new ProductRelationMapping();

		update.setProductId(1005);
		update.setProductTypeId("CARRIERS");
		update.setProductName("싼배");
		update.setProductDescription("사고났음");

		int updateCount = queryExecutor.update(SimpleORMQueryType.UPDATE, update);

		assertThat(1, is(updateCount));

		product = queryExecutor.queryForObject(SimpleORMQueryType.SELECT, update, ProductRelationMapping.class);

		assertNotNull(product);
		assertThat(1005, is(product.getProductId()));
		assertThat("CARRIERS", is(product.getProductTypeId()));
		assertThat("싼배", is(product.getProductName()));
		assertThat("사고났음", is(product.getProductDescription()));

		ProductRelationMapping delete = new ProductRelationMapping();

		delete.setProductId(1005);

		int deleteCount = queryExecutor.update(SimpleORMQueryType.DELETE, delete);

		assertThat(1, is(deleteCount));

		product = queryExecutor.queryForObject(SimpleORMQueryType.SELECT, delete, ProductRelationMapping.class);

		assertNull(product);
	}
}
