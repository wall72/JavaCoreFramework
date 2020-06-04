package jcf.iam.core;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/testApplicationContext-web.xml" })
public class AuthenticationHelperTest {

	@Autowired
	private AuthenticationHelper helper;

	@Test
	public void testInject() throws Exception {
		Assert.assertNotNull(helper);

		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();

		request.setMethod("POST");
		request.setSession(new MockHttpSession());
		request.setServletPath(null);

		String username = "nolang";
		String password = "nolang";

		SecurityContext context = helper.authenticate(request, response, username, password);

		Authentication authentication = context.getAuthentication();

		assertEquals("nolang", ((UserDetails) authentication.getPrincipal()).getUsername());
		assertEquals("nolang", ((UserDetails) authentication.getPrincipal()).getPassword());
	}
}
