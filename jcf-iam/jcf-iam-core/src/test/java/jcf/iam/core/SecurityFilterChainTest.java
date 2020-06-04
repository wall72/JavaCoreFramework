package jcf.iam.core;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import jcf.iam.core.authentication.sso.SsoProcessingHandler;
import jcf.iam.core.filter.ExternalAuthenticationProcessingFilter;
import jcf.iam.core.filter.repository.ZooKeeperSecurityContextRepository;
import jcf.iam.core.filter.request.ExternalAuthenticationToken;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.NullRememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/testApplicationContext-namespace.xml" })
public class SecurityFilterChainTest {

	@Test
	@Ignore
	public void testSecurityContextPersistenceFilter() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		SecurityContextPersistenceFilter filter = new SecurityContextPersistenceFilter();
		SecurityContextRepository repository = new ZooKeeperSecurityContextRepository();
		filter.setSecurityContextRepository(repository);
		request.setSession(new MockHttpSession());
		request.setCookies(new Cookie("jcfiam", "jcfiam_0"));
		filter.doFilter(request, response, new FilterChain() {

			public void doFilter(ServletRequest request, ServletResponse response)
					throws IOException, ServletException {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream("d:/object.txt"));
				try {
					SecurityContextHolder.setContext((SecurityContext) ois.readObject());
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ois.close();
			}
		});

		assertEquals(true, repository.containsContext(request));
	}

	@Autowired
	private AuthenticationManager authenticationManager;

	@Test
	@Ignore
	public void testUsernamePasswordAuthenticationFilter() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/j_spring_security_check");
		MockHttpServletResponse response = new MockHttpServletResponse();

		request.setParameter("j_username", "nolang");
		request.setParameter("j_password", "nolang");
		request.setSession(new MockHttpSession());

		UsernamePasswordAuthenticationFilter filter = new UsernamePasswordAuthenticationFilter();
		filter.setAuthenticationManager(authenticationManager);
		filter.setRememberMeServices(new NullRememberMeServices());

		filter.doFilter(request, response, new MockFilterChain());

		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();

		assertEquals("nolang", ((UserDetails) authentication.getPrincipal()).getUsername());
		assertEquals("nolang", ((UserDetails) authentication.getPrincipal()).getPassword());

		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("d:/object.txt"));
		oos.writeObject(context);
	}

	@Test
	public void testExternalAuthenticationProcessingFilter() throws Exception	{
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/j_spring_security_check");
		MockHttpServletResponse response = new MockHttpServletResponse();

		request.setParameter("j_sso_token", UUID.randomUUID().toString());
		request.setSession(new MockHttpSession());

		final UsernamePasswordAuthenticationFilter usernamefilter = new UsernamePasswordAuthenticationFilter();
		usernamefilter.setAuthenticationManager(authenticationManager);
		usernamefilter.setRememberMeServices(new NullRememberMeServices());

		ExternalAuthenticationProcessingFilter filter = new ExternalAuthenticationProcessingFilter();

		filter.setAuthenticationFilter(usernamefilter);
		filter.setSsoProcessingHandler(new MockSsoProcessingHandler());

		filter.doFilter(request, response, new FilterChain() {

			public void doFilter(ServletRequest request, ServletResponse response)
					throws IOException, ServletException {
				usernamefilter.doFilter(request, response, new MockFilterChain());
			}
		});

		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();

		assertEquals("nolang", ((UserDetails) authentication.getPrincipal()).getUsername());
		assertEquals("nolang", ((UserDetails) authentication.getPrincipal()).getPassword());

	}

	public static class MockSsoProcessingHandler extends SsoProcessingHandler {
		@Override
		public boolean isSsoAuthRequest(HttpServletRequest request) {
			return true;
		}

		@Override
		protected ExternalAuthenticationToken doAuthenticate(HttpServletRequest request) {
			return new ExternalAuthenticationToken("nolang", "nolang");
		}
	}
}
