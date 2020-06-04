package jcf.query.core.evaluator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jcf.query.core.QueryTemplate;
import jcf.query.domain.Product;
import jcf.query.domain.ProductType;
import jcf.query.web.CommonVariableHolder;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import statement.SampleStatement;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-velocity.xml" })
public class VeolcityQueryEvaluatorTest {

	@Autowired
	private QueryTemplate queryTemplate;

	@Test
	public void testGetQuery() {
		QueryMetaData metadata = queryTemplate.getQuery(SampleStatement.selectProductsWithInterpolation, null);

		String expected = "SELECT PRODUCT_ID,    PRODUCT_TYPE_ID,    PRODUCT_NAME,    PRODUCT_DESCRIPTION,    UPDATED    FROM PRODUCT";
		String actual = metadata.getStatement().replaceAll("[\t\n\r]", " ").trim();

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testGetQueryByParameterMap() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("productId", 1004);

		QueryMetaData metadata = queryTemplate.getQuery(SampleStatement.selectProductsWithIfStatment, params);

		String expected = "SELECT PRODUCT_ID,    PRODUCT_TYPE_ID,    PRODUCT_NAME,    PRODUCT_DESCRIPTION,    UPDATED    FROM PRODUCT       WHERE PRODUCT_ID = :productId         AND PRODUCT_ID > 1000";
		String actual = metadata.getStatement().replaceAll("[\t\n\r]", " ").trim();

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testGetQueryByParameterObject() {
		Product product = new Product();
		product.setProductId(1004);

		QueryMetaData metadata = queryTemplate.getQuery(SampleStatement.selectProductsWithIfStatment, product);

		String expected = "SELECT PRODUCT_ID,    PRODUCT_TYPE_ID,    PRODUCT_NAME,    PRODUCT_DESCRIPTION,    UPDATED    FROM PRODUCT       WHERE PRODUCT_ID = :productId         AND PRODUCT_ID > 1000";
		String actual = metadata.getStatement().replaceAll("[\t\n\r]", " ").trim();

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testGetQueryByNamespaceParameterObject() {
		ProductType productType = new ProductType();
		productType.setProductTypeId(1004);

		QueryMetaData metadata = queryTemplate.getQuery(SampleStatement.selectProductTypeWithIfStatment, productType);

		String expected = "SELECT PRODUCT_TYPE_ID,    PRODUCT_TYPE_NAME,    PRODUCT_TYPE_DESCRIPTION,    UPDATED    FROM PRODUCT_TYPE       WHERE PRODUCT_ID = :productType.productTypeId    AND PRODUCT_ID > 1000";
		String actual = metadata.getStatement().replaceAll("[\t\n\r]", " ").trim();

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testGetQueryByCommonVariables() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("productId", 1004);

		CommonVariableHolder.getVariables().put("session_productTypeId", "CARRIERS");

		QueryMetaData metadata = queryTemplate.getQuery(SampleStatement.selectProductsWithIfStatment, params);

		String expected = "SELECT PRODUCT_ID,    PRODUCT_TYPE_ID,    PRODUCT_NAME,    PRODUCT_DESCRIPTION,    UPDATED    FROM PRODUCT       WHERE PRODUCT_ID = :productId           AND PRODUCT_TYPE_ID = :session_productTypeId        AND PRODUCT_ID > 1000";
		String actual = metadata.getStatement().replaceAll("[\t\n\r]", " ").trim();

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testGetQueryForeach()	{
		Map<String, Object> param = new HashMap<String, Object>();

		List<Integer> productIdList = new ArrayList<Integer>();

		productIdList.add(1001);
		productIdList.add(1004);

		param.put("productIdList", productIdList);

		QueryMetaData metadata = queryTemplate.getQuery(SampleStatement.selectProductsWithForeachStatment, param);

		String expected = "SELECT PRODUCT_ID,    PRODUCT_TYPE_ID,    PRODUCT_NAME,    PRODUCT_DESCRIPTION,    UPDATED    FROM PRODUCT       WHERE PRODUCT_ID IN (             1001             ,          1004       )";
		String actual = metadata.getStatement().replaceAll("[\t\n\r]", " ").trim();

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testDynamicQuery()	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("productId", 1004);
		params.put("productTypeId", "CARRIERS");

		QueryMetaData metadata = queryTemplate.getQuery(SampleStatement.selectDynamicQuery, params);

		String expected = "SELECT PRODUCT_ID,    PRODUCT_TYPE_ID,    PRODUCT_NAME,    PRODUCT_DESCRIPTION,    UPDATED    FROM PRODUCT       WHERE  PRODUCT_ID = :productId          AND  PRODUCT_TYPE_ID = :productTypeId";
		String actual = metadata.getStatement().replaceAll("[\t\n\r]", " ").trim();

		Assert.assertEquals(expected, actual);
	}

//	@Test
	public void testDynamicQuery2()	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("productId", 1004);
		params.put("productTypeId", "");

		QueryMetaData metadata = queryTemplate.getQuery(SampleStatement.selectDynamicQuery, params);

		String expected = "SELECT PRODUCT_ID,    PRODUCT_TYPE_ID,    PRODUCT_NAME,    PRODUCT_DESCRIPTION,    UPDATED    FROM PRODUCT       WHERE  PRODUCT_ID = :productId         AND  PRODUCT_TYPE_ID = :productTypeId";
		String actual = metadata.getStatement().replaceAll("[\t\n\r]", " ").trim();

		Assert.assertEquals(expected, actual);
	}

	@Test
	@Ignore
	public void testSelectQueryWithInMacro()	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("productId", new Integer[]{1004, 1005, 1006});
		params.put("productTypeId", "CARRIERS");

		QueryMetaData metadata = queryTemplate.getQuery(SampleStatement.selectQueryWithInMacro, params);

		String expected = "SELECT PRODUCT_ID,    PRODUCT_TYPE_ID,    PRODUCT_NAME,    PRODUCT_DESCRIPTION,    UPDATED   FROM PRODUCT      WHERE      PRODUCT_ID IN (1004 ,1005 ,1006)             OR  PRODUCT_TYPE_ID = :productTypeId";
		String actual = metadata.getStatement().replaceAll("[\t\n\r]", " ").trim();

		System.out.println(actual);
		Assert.assertEquals(expected, actual);
	}
}
