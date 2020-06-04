package jcf.query.core;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jcf.query.domain.Product;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import statement.SampleStatement;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-velocity.xml" })
public class VelocityQueryExecutorTest {

	@Autowired
	private QueryExecutor queryExecutor;

//	@Before
	public void setup() {
//		queryExecutor = new QueryExecutor();
//		queryExecutor.setDataSource(dataSource);
	}

	@Test
	public void testQueryForListWithBeanTypeParameter() {
		Product product = new Product();
		product.setProductId(1004);

		List<Product> list = queryExecutor.queryForList(SampleStatement.selectProductsWithIfStatment, product, Product.class);

		assertThat(1, is(list.size()));

		Product actual = list.get(0);

		assertThat(1004, is(actual.getProductId()));
		assertThat("CARRIERS", is(actual.getProductTypeId()));
		assertThat("FPSO선", is(actual.getProductName()));
		assertThat("Floating Production Storage Offloading, 부유식 원유생산저장 설비", is(actual.getProductDescription()));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testQueryForListWithMapTypeParameter() {
		Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("productId", 1004);

		List<HashMap> list = queryExecutor.queryForList(SampleStatement.selectProductsWithIfStatment, parameter, HashMap.class);

		assertThat(1, is(list.size()));

		Map<String, ?> actual = list.get(0);

		assertThat(1004, is((Integer) actual.get("productId")));
		assertThat("CARRIERS", is((String) actual.get("productTypeId")));
		assertThat("FPSO선", is((String) actual.get("productName")));
		assertThat("Floating Production Storage Offloading, 부유식 원유생산저장 설비", is((String) actual.get("productDescription")));
	}

	@Test
	public void testQueryForListWithMapInListTypeParameter() {
		Map<String, Object> parameter = new HashMap<String, Object>();

		List<Integer> productIdList = new ArrayList<Integer>();

		productIdList.add(1001);
		productIdList.add(1004);

		parameter.put("productIdList", productIdList);

		List<Product> list = queryExecutor.queryForList(SampleStatement.selectProductsWithForeachStatment, parameter, Product.class);

		assertThat(2, is(list.size()));

		Product actual = list.get(1);

		assertThat(1004, is(actual.getProductId()));
		assertThat("CARRIERS", is(actual.getProductTypeId()));
		assertThat("FPSO선", is(actual.getProductName()));
		assertThat("Floating Production Storage Offloading, 부유식 원유생산저장 설비", is(actual.getProductDescription()));
	}

	@Test
	public void testQueryForList() {
		Product product = new Product();
		product.setProductId(1004);

		List<Map<String, Object>> list = queryExecutor.queryForMapList(SampleStatement.selectProductsWithIfStatment, product);

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

		Product actual = queryExecutor.queryForObject(SampleStatement.selectProductsWithIfStatment, product, Product.class);

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

		Map<String, Object> actual = queryExecutor.queryForObject(SampleStatement.selectProductsWithIfStatment, product, HashMap.class);

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

		Map<String, Object> actual = queryExecutor.queryForMap(SampleStatement.selectProductsWithIfStatment, product);

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

		int productId = queryExecutor.queryForInt(SampleStatement.selectProductId, param);

		assertThat(1004, is(productId));
	}

	@Test
	@ExpectedException(EmptyResultDataAccessException.class)
	public void testQueryForIntReturnEmptyResultData()	{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("productId", 1005);

		/**
		 * TODO primitive type을 조회하는 쿼리의 결과가 없을때 예외처리 흐름을 검토.. 현재는 EmptyResultDataAccessException 발생
		 */
		int productId = queryExecutor.queryForInt(SampleStatement.selectProductId, param);

		assertThat(1005, is(productId));
	}

	@Test
	public void testQueryForLong()	{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("productId", 1004);

		long productId = queryExecutor.queryForLong(SampleStatement.selectProductId, param);

		assertThat((long) 1004, is(productId));
	}

	@Test
	@ExpectedException(EmptyResultDataAccessException.class)
	public void testQueryForLongReturnEmptyResultData()	{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("productId", 1005);

		long productId = queryExecutor.queryForLong(SampleStatement.selectProductId, param);

		assertThat((long) 1005, is(productId));
	}

	@Test
	public void testUpdate()	{
		Product insert = new Product();

		insert.setProductId(1005);
		insert.setProductTypeId("CARRIERS");
		insert.setProductName("비싼배");
		insert.setProductDescription("초 크다..");

//		Map<String, Object> insert = new HashMap<String, Object>();
//
//		insert.put("productId", new Integer[]{2011, 2012, 2013});
//		insert.put("productTypeId", "CARRIERS");
//		insert.put("productName", "비싼배");
//		insert.put("productDescription", "초 크다..");

		int insertCount = queryExecutor.update(SampleStatement.insertProduct, insert);

		assertThat(1, is(insertCount));

		Product product = queryExecutor.queryForObject(SampleStatement.selectProductsWithIfStatment, insert, Product.class);

		assertNotNull(product);
		assertThat(1005, is(product.getProductId()));
		assertThat("CARRIERS", is(product.getProductTypeId()));
		assertThat("비싼배", is(product.getProductName()));
		assertThat("초 크다..", is(product.getProductDescription()));

		Product update = new Product();

		update.setProductId(1005);
		update.setProductName("싼배");
		update.setProductDescription("사고났음");

		int updateCount = queryExecutor.update(SampleStatement.updateProduct, update);

		assertThat(1, is(updateCount));

		product = queryExecutor.queryForObject(SampleStatement.selectProductsWithIfStatment, update, Product.class);

		assertNotNull(product);
		assertThat(1005, is(product.getProductId()));
		assertThat("CARRIERS", is(product.getProductTypeId()));
		assertThat("싼배", is(product.getProductName()));
		assertThat("사고났음", is(product.getProductDescription()));

		Product delete = new Product();

		delete.setProductId(1005);

		int deleteCount = queryExecutor.update(SampleStatement.deleteProduct, delete);

		assertThat(1, is(deleteCount));

		product = queryExecutor.queryForObject(SampleStatement.selectProductsWithIfStatment, delete, Product.class);

		assertNull(product);
	}

	/**Stored Procedure 테스트를 위해 applicationContext-velocity.xml 의  DB  오라클로 변경 필요
	 */
	@Test
	@Ignore
	public void testProcedure() {
		Map<String,String> param = new HashMap<String,String>();
    	Map<String, Object> result = queryExecutor.executeProcedure("edu", null,"get_emp_rs", param);
    	assertNotNull(result.get("P_RECORDSET"));

	}

}
