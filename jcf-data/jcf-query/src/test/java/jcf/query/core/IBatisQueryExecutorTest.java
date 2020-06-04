package jcf.query.core;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import jcf.query.domain.Product;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-ibatis.xml")
public class IBatisQueryExecutorTest {

	@Autowired
	private QueryExecutor queryExecutor;

	@Autowired
	private DataSource dataSource;

	@Test
	public void testConfigQueryExecutor() {
		assertNotNull(queryExecutor);
	}

	@Test
//	@Ignore
	public void testProcedure() {
		Map<String,String> param = new HashMap<String,String>();
		param.put("IN_TABL_NM", "Test");

//		int i = queryExecutor.update("PRODUCT.proc01", param);
//		BigDecimal i = queryExecutor.executeProcedure("SP_ADM0001_SRNUM_TEST1", param, BigDecimal.class);
		Map<String, Object> result = queryExecutor.executeCallStatement("PRODUCT.proc01", param);
		System.out.println(result);
		result = queryExecutor.executeCallStatement("PRODUCT.proc01", param);
		System.out.println(result);
	}

	@Test
	public void testProcedure2() {
		Map<String, Object> stdRentCalc = new HashMap<String, Object>();

		stdRentCalc.put("RESOURCECODE", "1");
		stdRentCalc.put("resourceType", "2");

		Map <String, Object> result = queryExecutor.executeCallStatement("PRODUCT.rentCalc", stdRentCalc);

		System.out.print("result : " + result);
	}


	@Test
	public void testQueryForListUsingResultMap() {
		Product product = new Product();
		product.setProductId(1004);

		List<Object> list = queryExecutor.queryForList("PRODUCT.selectProductList", product);

		assertThat(1, is(list.size()));

		Product actual = (Product) list.get(0);

		assertThat(1004, is(actual.getProductId()));
		assertThat("CARRIERS", is(actual.getProductTypeId()));
		assertThat("FPSO선", is(actual.getProductName()));
		assertThat("Floating Production Storage Offloading, 부유식 원유생산저장 설비", is(actual.getProductDescription()));
	}

	@Test
	public void testForPaging() {

		List<Product> queryForList = queryExecutor.queryForList("PRODUCT.selectProducts", null, 0, 4, Product.class);

		int i = 0;

		for (Product actual : queryForList) {
			assertThat(1001+i, is(actual.getProductId()));
			i++;
		}
	}

	@Test
	public void testForPaging2() {

		@SuppressWarnings("deprecation")
		List<Object> queryForList = queryExecutor.queryForList("PRODUCT.selectProducts", null, 0, 4);

		int i = 0;

		for (Object actual : queryForList) {
			assertThat(1001+i, is(((Product) actual).getProductId()));
			i++;
		}
	}

	@Test
	public void testQueryForListWithBeanTypeParameter() {
		Product product = new Product();
		product.setProductId(1004);

		List<Product> list = queryExecutor.queryForList("PRODUCT.selectProducts", product, Product.class);

		assertThat(1, is(list.size()));

		Product actual = list.get(0);

		assertThat(1004, is(actual.getProductId()));
		assertThat("CARRIERS", is(actual.getProductTypeId()));
		assertThat("FPSO선", is(actual.getProductName()));
		assertThat("Floating Production Storage Offloading, 부유식 원유생산저장 설비", is(actual.getProductDescription()));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testQueryForListWithMapTypeParameter() {
		Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("productId", 1004);

		List<HashMap> list = queryExecutor.queryForList("PRODUCT.selectProducts", parameter, HashMap.class);

		assertThat(1, is(list.size()));

		Map<String, ?> actual = list.get(0);

		assertThat(1004, is((Integer) actual.get("productId")));
		assertThat("CARRIERS", is((String) actual.get("productTypeId")));
		assertThat("FPSO선", is((String) actual.get("productName")));
		assertThat("Floating Production Storage Offloading, 부유식 원유생산저장 설비", is((String) actual.get("productDescription")));
	}

	@Test
	public void testQueryForObjectWithBeanTypeParameter() {
		Product product = new Product();
		product.setProductId(1004);

		Product actual = queryExecutor.queryForObject("PRODUCT.selectProducts", product, Product.class);

		assertNotNull(actual);
		assertThat(1004, is(actual.getProductId()));
		assertThat("CARRIERS", is(actual.getProductTypeId()));
		assertThat("FPSO선", is(actual.getProductName()));
		assertThat("Floating Production Storage Offloading, 부유식 원유생산저장 설비", is(actual.getProductDescription()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testQueryForObjectWithMapTypeParameter() {
		Product product = new Product();
		product.setProductId(1004);

		Map<String, Object> actual = queryExecutor.queryForObject("PRODUCT.selectProducts", product, HashMap.class);

		assertNotNull(actual);
		assertThat(1004, is((Integer) actual.get("productId")));
		assertThat("CARRIERS", is((String) actual.get("productTypeId")));
		assertThat("FPSO선", is((String) actual.get("productName")));
		assertThat("Floating Production Storage Offloading, 부유식 원유생산저장 설비", is((String) actual.get("productDescription")));
	}

	@Test
	public void testQueryForMap() {
		Product product = new Product();
		product.setProductId(1004);

		Map<String, Object> actual = queryExecutor.queryForMap("PRODUCT.selectProducts", product);

		assertNotNull(actual);
		assertThat(1004, is((Integer) actual.get("productId")));
		assertThat("CARRIERS", is((String) actual.get("productTypeId")));
		assertThat("FPSO선", is((String) actual.get("productName")));
		assertThat("Floating Production Storage Offloading, 부유식 원유생산저장 설비", is((String) actual.get("productDescription")));
	}

	@Test
	public void testQueryForInt()	{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("productId", 1004);

		String productId = (String) queryExecutor.queryForObject("PRODUCT.selectProductId", param);

		assertThat("1004", is(productId));
	}

	@Test
	@ExpectedException(EmptyResultDataAccessException.class)
	public void testQueryForIntReturnEmptyResultData()	{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("productId", 1005);

		/**
		 * TODO primitive type을 조회하는 쿼리의 결과가 없을때 예외처리 흐름을 검토.. 현재는 EmptyResultDataAccessException 발생
		 */
		int productId = queryExecutor.queryForInt("PRODUCT.selectProductId", param);

		assertThat(1005, is(productId));
	}

	@Test
	public void testQueryForLong()	{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("productId", 1004);

		long productId = queryExecutor.queryForLong("PRODUCT.selectProductId", param);

		assertThat((long) 1004, is(productId));
	}

	@Test
	@ExpectedException(EmptyResultDataAccessException.class)
	public void testQueryForLongReturnEmptyResultData()	{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("productId", 1005);

		long productId = queryExecutor.queryForLong("PRODUCT.selectProductId", param);

		assertThat((long) 1005, is(productId));
	}

	@Test
	public void testUpdate()	{
		Product insert = new Product();

		insert.setProductId(1005);
		insert.setProductTypeId("CARRIERS");
		insert.setProductName("비싼배");
		insert.setProductDescription("초 크다..");

		int insertCount = queryExecutor.update("PRODUCT.insertProduct1", insert);
		System.out.println(insertCount);
		//assertThat(1, is(insertCount));

		Product product = queryExecutor.queryForObject("PRODUCT.selectProducts", insert, Product.class);

		assertNotNull(product);
		assertThat(1005, is(product.getProductId()));
		assertThat("CARRIERS", is(product.getProductTypeId()));
		assertThat("비싼배", is(product.getProductName()));
		assertThat("초 크다..", is(product.getProductDescription()));

		Product update = new Product();

		update.setProductId(1005);
		update.setProductName("싼배");
		update.setProductDescription("사고났음");

		int updateCount = queryExecutor.update("PRODUCT.updateProduct", update);

		assertThat(1, is(updateCount));

		product = queryExecutor.queryForObject("PRODUCT.selectProducts", update, Product.class);

		assertNotNull(product);
		assertThat(1005, is(product.getProductId()));
		assertThat("CARRIERS", is(product.getProductTypeId()));
		assertThat("싼배", is(product.getProductName()));
		assertThat("사고났음", is(product.getProductDescription()));

		Product delete = new Product();

		delete.setProductId(1005);

		int deleteCount = queryExecutor.update("PRODUCT.deleteProduct", delete);

		assertThat(1, is(deleteCount));

		product = queryExecutor.queryForObject("PRODUCT.selectProducts", delete, Product.class);

		assertNull(product);
	}
}
