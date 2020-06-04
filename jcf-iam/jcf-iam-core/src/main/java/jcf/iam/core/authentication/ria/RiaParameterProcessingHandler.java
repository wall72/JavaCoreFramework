package jcf.iam.core.authentication.ria;

import javax.servlet.http.HttpServletRequest;

import jcf.iam.core.filter.request.ExternalAuthenticationToken;

/**
 *
 * @author nolang
 *
 */
public interface RiaParameterProcessingHandler {

	/**
	 *
	 * @param request
	 * @return
	 */
	ExternalAuthenticationToken getAuthenticationToken(HttpServletRequest request);

}
