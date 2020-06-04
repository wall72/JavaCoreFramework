package jcf.iam.core.filter.repository;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;

/**
 * is developing...
 * @author nolang
 *
 */
public class InMemorySecurityContextRepository implements SecurityContextRepository {

	private Map<String, SecurityContext> contextRepository = new HashMap<String, SecurityContext>();

	private String testKey = "_key";

	public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
		SecurityContext context = contextRepository.get(testKey);

		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
					"d:/object.txt"));

			context = (SecurityContext) ois.readObject();
			ois.close();
		} catch (Exception e) {
		}

		if(context == null){
			context = SecurityContextHolder.createEmptyContext();
		}

		return context;
	}

	public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
		contextRepository.put(testKey, context);
	}

	public boolean containsContext(HttpServletRequest request) {
		return contextRepository.containsKey(testKey);
	}

}
