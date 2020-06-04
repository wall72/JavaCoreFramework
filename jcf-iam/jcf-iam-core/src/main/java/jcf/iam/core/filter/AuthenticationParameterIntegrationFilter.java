package jcf.iam.core.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jcf.iam.core.IamCustomizerFactory;
import jcf.query.web.CommonVariableHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

/**
 *
 * @author nolang
 *
 */
public class AuthenticationParameterIntegrationFilter extends GenericFilterBean {

	private static final Logger logger = LoggerFactory.getLogger(AuthenticationParameterIntegrationFilter.class);

	@Autowired
	private IamCustomizerFactory customizerFactory;

	private static final String prefix = "_jcfiam_authentication_intergration_";

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		List<String> customParameterList = customizerFactory.getCustomizer().getCustomParameterList();

		if (customParameterList != null && customParameterList.size() > 0) {
			retrieveRequestParameter((HttpServletRequest) request, customParameterList);
		}

		chain.doFilter(request, response);

		CommonVariableHolder.clear();
	}

	private void retrieveRequestParameter(HttpServletRequest request, List<String> customParameterList) {
		HttpSession session = request.getSession(false);

		if(session != null)	{
			for(String parameter : customParameterList)	{
				if(StringUtils.hasText(request.getParameter(parameter)))	{
					session.setAttribute(prefix + parameter, request.getParameter(parameter));
				}

				if(logger.isDebugEnabled()){
					logger.debug("[AuthenticationParameterIntegrationFilter] (JCFIAM) Custom Parameter - key={}, value={}", new Object[]{parameter, session.getAttribute(prefix + parameter)});
				}

				CommonVariableHolder.addVariable(parameter, (String) session.getAttribute(prefix + parameter));
			}
		}
	}

	public void setCustomizerFactory(IamCustomizerFactory customizerFactory) {
		this.customizerFactory = customizerFactory;
	}
}
