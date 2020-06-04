package jcf.iam.core;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/testApplicationContext-customizer.xml" })
public class CustomizerTest {

	@Autowired
	private DefaultCustomizer customizer;

	@Test
	public void 테스트() {
		Assert.assertNotNull(customizer);
	}
}
