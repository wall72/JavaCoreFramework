package jcf.context.spring;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author purple
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SpringContextHolderTest {


	@Test
	public void testGetBean(){
		Printer printer = (Printer) SpringContextHolder.getBean("printer");		
		Assert.assertEquals("ok", printer.print());
	}	
	
	/**
	 * 
	 * @author purple
	 *
	 */
	public static class Printer{		
		public String print(){ return "ok"; }		
	}
}
