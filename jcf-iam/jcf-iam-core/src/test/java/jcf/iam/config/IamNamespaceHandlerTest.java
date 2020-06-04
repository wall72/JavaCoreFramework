package jcf.iam.config;

import jcf.iam.core.Customizer;
import jcf.iam.core.IamCustomizerFactory;
import jcf.iam.core.authentication.userdetails.UserAccessControlService;
import jcf.iam.core.filter.AuthenticationParameterIntegrationFilter;
import jcf.query.core.QueryExecutorWrapper;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/testApplicationContext-namespace.xml" })
public class IamNamespaceHandlerTest {

	@Autowired
	private Customizer customizer;

	@Autowired
	private IamCustomizerFactory customizerFactory;

	@Autowired
	private QueryExecutorWrapper queryExecutor;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private UserAccessControlService userAccessControlService;

	@Autowired
	private AffirmativeBased accessDecisionManager;

	@Autowired
	private ProviderManager authenticationManager;

	@Autowired
	private FilterSecurityInterceptor filterSecurityInterceptor;

	@Autowired
	private AuthenticationParameterIntegrationFilter parameterIntegrationFilter;


	@Test
	public void testAutoConfiguration() throws Exception {
		Assert.assertNotNull(customizer);
		Assert.assertNotNull(customizerFactory);
		Assert.assertNotNull(queryExecutor);
		Assert.assertNotNull(userDetailsService);
		Assert.assertNotNull(userAccessControlService);
		Assert.assertNotNull(accessDecisionManager);
		Assert.assertNotNull(authenticationManager);
		Assert.assertNotNull(filterSecurityInterceptor);
		Assert.assertNotNull(parameterIntegrationFilter);


		System.out.println(customizer.getUserClass());
	}

}
