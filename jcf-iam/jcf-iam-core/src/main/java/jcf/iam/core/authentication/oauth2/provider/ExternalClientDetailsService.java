package jcf.iam.core.authentication.oauth2.provider;

import java.util.LinkedHashMap;

import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;

/**
 *
 * @author nolang
 *
 */
public class ExternalClientDetailsService implements ClientDetailsService {

	private LinkedHashMap<String, ClientDetails> externalClientMapping;

	public ExternalClientDetailsService(LinkedHashMap<String, ClientDetails> externalClientMapping) {
		this.externalClientMapping = externalClientMapping;
	}

	public ClientDetails loadClientByClientId(String clientId) {
		ClientDetails details = externalClientMapping.get(clientId);

		if (details == null) {
			throw new InvalidClientException("Client not found: " + clientId);
		}

		return details;
	}

}
