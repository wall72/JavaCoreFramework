package jcf.query.web.interceptor;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jcf.query.web.CommonVariableHolder;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.HandlerInterceptor;


public class SessionParameterIntegrationInterceptorTest {

	@Test
	public void setCommonVarialblesTest() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpSession session = new MockHttpSession();

		session.setAttribute("userId", "testUser");
		session.setAttribute("dept", "testDept");

		request.setSession(session);

		HandlerInterceptor interceptor = new TestSessionParameterIntegrationInterceptor();

		interceptor.preHandle(request, null, null);

		assertThat("testUser", is((String) CommonVariableHolder.get("session.userId")));
		assertThat("testDept", is((String) CommonVariableHolder.get("session.dept")));
	}

	public static class TestSessionParameterIntegrationInterceptor extends SessionParameterIntegrationInterceptor {

		@Override
		public Map<String, Object> getSessionValues(HttpServletRequest request) {
			Map<String, Object> sessionValues = new HashMap<String, Object>();

			sessionValues.put("session.userId", request.getSession().getAttribute("userId"));
			sessionValues.put("session.dept", request.getSession().getAttribute("dept"));

			return sessionValues;
		}

	}
}
