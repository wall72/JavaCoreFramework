package jcf.iam.core.authentication.sso;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcf.iam.core.filter.request.ExternalAuthenticationToken;

/**
 *
 * @author nolang
 *
 */
public abstract class SsoProcessingHandler {

	public ExternalAuthenticationToken authenticate(HttpServletRequest request, HttpServletResponse response) {
		ExternalAuthenticationToken token = null;

		if(isSsoAuthRequest(request))	{
			token = doAuthenticate(request);
		}

		return token;
	}

	public abstract boolean isSsoAuthRequest(HttpServletRequest request);

	protected abstract ExternalAuthenticationToken doAuthenticate(HttpServletRequest request);
}
