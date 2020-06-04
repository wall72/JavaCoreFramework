package jcf.iam.admin.authentication.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jcf.sua.JcfSuaTestServlet;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/testApplicationContext-sua.xml" })
public class UserControllerTest {

	@Autowired
	private JcfSuaTestServlet servlet;

	private ObjectMapper mapper = new ObjectMapper();

	private String contentType = "application/json+sua";

	@Test
	public void 사용자리스트조회() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.GET.name(), "/jcfiam/admin/users/selectUsers");
		MockHttpServletResponse response = new MockHttpServletResponse();

		request.addHeader("Content-Type", contentType);
		request.setContent("{\"user\":{\"username\":\"nolang\"}}".getBytes());

		String contentString = servlet.doDispatch(request, response);

		assertUser(contentString, "userList", "nolang", "nolang", "Y");
	}

	@Test
	public void 사용자추가수정삭제() throws Exception {
		/*
		 * 추가
		 */
		MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.POST.name(), "/jcfiam/admin/users/insert");
		MockHttpServletResponse response = new MockHttpServletResponse();

		request.addHeader("Content-Type", contentType);
		request.setContent("{\"user\":{\"username\":\"testuser\", \"password\":\"testpassword\", \"name\":\"testname\", \"enabled\":\"Y\", \"createUserId\":\"admin\", \"lastModifyUserId\":\"admin\"}}".getBytes());

		String contentString = servlet.doDispatch(request, response);

		/*
		 * 조회
		 */
		request = new MockHttpServletRequest(HttpMethod.GET.name(), "/jcfiam/admin/users/select");
		response = new MockHttpServletResponse();

		request.addHeader("Content-Type", contentType);
		request.addParameter("username", "testuser");

		contentString = servlet.doDispatch(request, response);

		assertUser(contentString, "user", "testuser", "testpassword", "Y");

		/*
		 * 수정
		 */
		request = new MockHttpServletRequest(HttpMethod.PUT.name(), "/jcfiam/admin/users/update");
		response = new MockHttpServletResponse();

		request.addHeader("Content-Type", contentType);
		request.setContent("{\"user\":{\"username\":\"testuser\", \"password\":\"1111\", \"name\":\"testname\", \"enabled\":\"N\", \"createUserId\":\"admin\", \"lastModifyUserId\":\"admin\"}}".getBytes());

		contentString = servlet.doDispatch(request, response);

		/*
		 * 조회
		 */
		request = new MockHttpServletRequest(HttpMethod.GET.name(), "/jcfiam/admin/users/select");
		response = new MockHttpServletResponse();

		request.addHeader("Content-Type", contentType);
		request.addParameter("username", "testuser");

		contentString = servlet.doDispatch(request, response);

		assertUser(contentString, "user", "testuser", "1111", "N");

		/*
		 * 삭제
		 */
		request = new MockHttpServletRequest(HttpMethod.DELETE.name(), "/jcfiam/admin/users/delete");
		response = new MockHttpServletResponse();

		request.addHeader("Content-Type", contentType);
		request.addParameter("username", "testuser");

		contentString = servlet.doDispatch(request, response);

		/*
		 * 조회
		 */
		request = new MockHttpServletRequest(HttpMethod.GET.name(), "/jcfiam/admin/users/select");
		response = new MockHttpServletResponse();

		request.addHeader("Content-Type", contentType);
		request.addParameter("username", "testuser");

		contentString = servlet.doDispatch(request, response);

		System.out.println(contentString);
	}

	@SuppressWarnings("unchecked")
	private void assertUser(String contentString, String root, String username, String password, String enabled) throws Exception {
		Map<String, List<Map<String, Object>>> resultMap = mapper.readValue(contentString, HashMap.class);

		List<Map<String, Object>> userList = resultMap.get(root);

		Assert.assertNotNull(userList);

		Assert.assertEquals(username, userList.get(0).get("username"));
		Assert.assertEquals(password, userList.get(0).get("password"));
		Assert.assertEquals(enabled, userList.get(0).get("enabled"));
	}
}
