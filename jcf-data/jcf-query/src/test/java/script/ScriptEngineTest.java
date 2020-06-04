package script;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import statement.SampleStatement;

public class ScriptEngineTest {

	@Test
	public void groovyVariableLoadTest() {
		String sql = (String) SampleStatement.selectProductsWithoutTemplate;

		String expected = "SELECT PRODUCT_ID, PRODUCT_TYPE_ID, PRODUCT_NAME, PRODUCT_DESCRIPTION, UPDATED FROM PRODUCT";
		String actual = sql.replaceAll("[\t|\n]", "");

		Assert.assertEquals(expected, actual);
	}

	@Test
	@Ignore
	public void testGroovyStringLength()	{
		StringVar.init();

		for(int i=0;i<StringVar.str.length();++i){
			System.out.println(StringVar.str.charAt(i));
		}

//		StringBuilder builder = new StringBuilder();
//
//		for(int i=0;i<65535;++i){
//			builder.append("A");
//		}
//
//		builder.append("B");
//
//		for(int i=0;i<builder.length();++i){
//			System.out.println(builder.toString().charAt(i));
//		}
	}
}
