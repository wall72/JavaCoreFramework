package jcf.iam.core.filter.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

/**
 *
 * @author nolang
 *
 */
public abstract class AbstractIamAccessDeniedHandler implements AccessDeniedHandler {

	private static final Logger logger = LoggerFactory.getLogger(AbstractIamAccessDeniedHandler.class);

	private AccessDeniedHandler handler = new AccessDeniedHandlerImpl();

	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

		if (isMciRequest(request)) {
			handleMciRequest(request, response, accessDeniedException);
		} else {
			handler.handle(request, response, accessDeniedException);
		}

	}


	protected abstract boolean isMciRequest(HttpServletRequest request);

	protected abstract void handleMciRequest(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException);

    /**
     * The error page to use. Must begin with a "/" and is interpreted relative to the current context root.
     *
     * @param errorPage the dispatcher path to display
     *
     * @throws IllegalArgumentException if the argument doesn't comply with the above limitations
     */
    public void setErrorPage(String errorPage) {
    	((AccessDeniedHandlerImpl) handler).setErrorPage(errorPage);
    }
}
