package jcf.iam.core.authentication.pki;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcf.iam.core.filter.request.ExternalAuthenticationToken;

/**
 *
 * @author nolang
 *
 */
public abstract class PkiProcessingHandler {
	public ExternalAuthenticationToken authenticate(HttpServletRequest request, HttpServletResponse response) {
		ExternalAuthenticationToken token = null;

		if(isPkiAuthRequest(request))	{
			token = doAuthenticate(request);
		}

		return token;
	}

	public abstract boolean isPkiAuthRequest(HttpServletRequest request);

	protected abstract ExternalAuthenticationToken doAuthenticate(HttpServletRequest request);
}
