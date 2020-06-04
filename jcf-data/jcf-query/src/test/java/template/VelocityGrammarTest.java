package template;

import java.io.StringWriter;

import jcf.query.domain.ProductType;

import junit.framework.Assert;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import statement.SampleStatement;

public class VelocityGrammarTest {

	@Before
	public void init() throws Exception {
		Velocity.init();
	}

	@Test
	public void templateEngineLoadTest() {
		String sql = (String) SampleStatement.selectProductsWithInterpolation;

		String expected = "#set( $tableName = \"PRODUCT\")SELECT PRODUCT_ID,PRODUCT_TYPE_ID,PRODUCT_NAME,PRODUCT_DESCRIPTION,UPDATED FROM ${tableName}";
		String actual = sql.replaceAll("[\t|\n]", "");

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void templateStringInterpolationParsingTest() throws Exception {
		StringWriter writer = new StringWriter();

		boolean result = Velocity.evaluate(new VelocityContext(), writer, "", (String) SampleStatement.selectProductsWithInterpolation);

		Assert.assertEquals(true, result);

		String expected = "SELECT PRODUCT_ID,PRODUCT_TYPE_ID,PRODUCT_NAME,PRODUCT_DESCRIPTION,UPDATED FROM PRODUCT";
		String actual = writer.toString().replaceAll("[\t|\n]", "");;

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void templateStringIfStatementParsingTest() throws Exception {
		VelocityContext context = new VelocityContext();
		context.put("productId", 1004);

		StringWriter writer = new StringWriter();

		boolean result = Velocity.evaluate(context, writer, "", (String) SampleStatement.selectProductsWithIfStatment);

		Assert.assertEquals(true, result);

		String expected = "SELECT PRODUCT_ID,PRODUCT_TYPE_ID,PRODUCT_NAME,PRODUCT_DESCRIPTION,UPDATED FROM PRODUCT WHERE PRODUCT_ID = :productId  AND PRODUCT_ID > 1000";
		String actual = writer.toString().replaceAll("[\t|\n]", "");;

		Assert.assertEquals(expected, actual);

	}

	@Test
	public void templateStringIfStatementParsingTest2() throws Exception {
		ProductType productType = new ProductType();
		productType.setProductTypeId(1004);

		VelocityContext context = new VelocityContext();
		context.put("productType", productType);

		StringWriter writer = new StringWriter();

		boolean result = Velocity.evaluate(context, writer, "", (String) SampleStatement.selectProductTypeWithIfStatment);

		Assert.assertEquals(true, result);

		String expected = "SELECT PRODUCT_TYPE_ID,PRODUCT_TYPE_NAME,PRODUCT_TYPE_DESCRIPTION,UPDATED FROM PRODUCT_TYPE WHERE PRODUCT_ID = :productType.productTypeId AND PRODUCT_ID > 1000";
		String actual = writer.toString().replaceAll("[\t|\n]", "");

		Assert.assertEquals(expected, actual);
	}

	@Test
	@Ignore
	public void testRegEx()	{
		String insertQuery = "INSERT INTO PRODUCT VALUES(:productId, :productTypeId, :productName, :productDescription, sysdate)";
		String str = insertQuery.replaceAll(":[_A-Za-z]+[A-Za-z0-9_.-]*", "?");
		System.out.println(str);

		String selectQUery = "SELECT PRODUCT_ID,     PRODUCT_TYPE_ID,     PRODUCT_NAME,     PRODUCT_DESCRIPTION,     UPDATED     FROM PRODUCT       WHERE           PRODUCT_ID = :productId        AND       PRODUCT_TYPE_ID = :productTypeId";
		String str2 = selectQUery.replaceAll(":[_A-Za-z]+[A-Za-z0-9_.-]*", "?");
		System.out.println(str2);
	}
}
