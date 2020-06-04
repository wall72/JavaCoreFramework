package jcf.iam.admin.oauth2.controller;

import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientAuthenticationToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.verification.ClientAuthenticationCache;
import org.springframework.security.oauth2.provider.verification.DefaultClientAuthenticationCache;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class OAuth2AccessController {

	private ClientAuthenticationCache authenticationCache = new DefaultClientAuthenticationCache();

	@Autowired(required = false)
	private ClientDetailsService clientDetailsService;

	@RequestMapping("/oauth/confirm_access")
	public ModelAndView confirmAccess(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ClientAuthenticationToken clientAuth = authenticationCache
				.getAuthentication(request, response);
		if (clientAuth == null) {
			throw new IllegalStateException(
					"No client authentication request to authorize.");
		}

		ClientDetails client = clientDetailsService
				.loadClientByClientId(clientAuth.getClientId());
		TreeMap<String, Object> model = new TreeMap<String, Object>();
		model.put("auth_request", clientAuth);
		model.put("client", client);

		return new ModelAndView("access_confirmation", model);
	}
}
