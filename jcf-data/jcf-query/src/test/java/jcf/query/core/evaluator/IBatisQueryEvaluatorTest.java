package jcf.query.core.evaluator;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import jcf.query.TemplateEngineType;
import jcf.query.core.QueryTemplate;
import jcf.query.domain.Product;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ibatis.sqlmap.client.SqlMapClient;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-ibatis.xml" })
public class IBatisQueryEvaluatorTest {

	@Autowired
	private SqlMapClient sqlMapClient;

	private QueryTemplate queryTemplate;

	@Before
	public void setup() {
		queryTemplate = new QueryTemplate();
		queryTemplate.setDefaultTemplate(TemplateEngineType.IBATIS);

		IBatisQueryEvaluator queryEvaluator = new IBatisQueryEvaluator();
		queryEvaluator.setSqlMapClient(sqlMapClient);

		Map<TemplateEngineType, QueryEvaluator> queryEvaluators = new HashMap<TemplateEngineType, QueryEvaluator>();

		queryEvaluators.put(TemplateEngineType.IBATIS, queryEvaluator);

		queryTemplate.setQueryEvaluators(queryEvaluators);
	}

	@Test
	public void testSelectQuery() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("productId", 1004);
		param.put("productTypeId", "CARRIERS");

		QueryMetaData metadata = queryTemplate.getQuery("PRODUCT.selectProductList", param);

		String expected = "SELECT PRODUCT_ID,     PRODUCT_TYPE_ID,     PRODUCT_NAME,     PRODUCT_DESCRIPTION,     UPDATED     FROM PRODUCT       WHERE           PRODUCT_ID = :productId        AND       PRODUCT_TYPE_ID = :productTypeId";
		String actual = metadata.getStatement().replaceAll("[\t\n\r]", " ").trim();

		assertThat(actual, is(expected));

	}

	@Test
	public void testInsertQuery() throws Exception {
		Product product = new Product();

		product.setProductId(1004);
		product.setProductTypeId("CARRIERS");
		product.setProductName("FPSO선");
		product.setProductDescription("Floating Production Storage Offloading, 부유식 원유생산저장 설비");

		QueryMetaData metadata = queryTemplate.getQuery("PRODUCT.insertProduct", product);

		String expected = "INSERT INTO PRODUCT VALUES(:productId, :productTypeId, :productName, :productDescription, sysdate)";
		String actual = metadata.getStatement().replaceAll("[\t\n\r]", " ").trim();

		assertThat(actual, is(expected));
	}

	@Test
	public void testUpdateQuery() throws Exception {
		Product product = new Product();

		product.setProductId(1004);
		product.setProductTypeId("CARRIERS");
		product.setProductName("FPSO선");
		product.setProductDescription("Floating Production Storage Offloading, 부유식 원유생산저장 설비");

		QueryMetaData metadata = queryTemplate.getQuery("PRODUCT.updateProduct", product);

		String expected = "UPDATE PRODUCT SET    PRODUCT_NAME = :productName,    PRODUCT_DESCRIPTION = :productDescription,    UPDATED = sysdate   WHERE PRODUCT_ID = :productId";
		String actual = metadata.getStatement().replaceAll("[\t\n\r]", " ").trim();

		assertThat(actual, is(expected));
	}

	@Test
	public void testDeleteQuery() throws Exception {
		Product product = new Product();

		product.setProductId(1004);
		product.setProductTypeId("CARRIERS");
		product.setProductName("FPSO선");
		product.setProductDescription("Floating Production Storage Offloading, 부유식 원유생산저장 설비");


		QueryMetaData metadata = queryTemplate.getQuery("PRODUCT.deleteProduct", product);

		String expected = "DELETE FROM PRODUCT WHERE PRODUCT_ID = :productId";
		String actual = metadata.getStatement().replaceAll("[\t\n\r]", " ").trim();

		assertThat(actual, is(expected));
	}

}
