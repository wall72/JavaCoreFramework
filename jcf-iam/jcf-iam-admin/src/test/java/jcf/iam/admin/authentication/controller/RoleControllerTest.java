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
public class RoleControllerTest {

	@Autowired
	private JcfSuaTestServlet servlet;

	private ObjectMapper mapper = new ObjectMapper();

	private String contentType = "application/json+sua";

	@Test
	public void 권한추가수정삭제() throws Exception {
		/*
		 * 추가
		 */
		MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.POST.name(), "/jcfiam/admin/roles/insert");
		MockHttpServletResponse response = new MockHttpServletResponse();

		request.addHeader("Content-Type", contentType);
		request.setContent("{\"role\":{\"roleId\":\"ROLE_TEST\", \"roleName\":\"테스트권한\", \"description\":\"테스트권한\", \"enabled\":\"Y\", \"createUserId\":\"admin\", \"lastModifyUserId\":\"admin\"}}".getBytes());

		String contentString = servlet.doDispatch(request, response);

		/*
		 * 조회
		 */
		request = new MockHttpServletRequest(HttpMethod.GET.name(), "/jcfiam/admin/roles/select");
		response = new MockHttpServletResponse();

		request.addHeader("Content-Type", contentType);
		request.addParameter("roleId", "ROLE_TEST");

		contentString = servlet.doDispatch(request, response);

		assertRole(contentString, "role", "ROLE_TEST", "테스트권한", "테스트권한", "Y");

		/*
		 * 수정
		 */
		request = new MockHttpServletRequest(HttpMethod.PUT.name(), "/jcfiam/admin/roles/update");
		response = new MockHttpServletResponse();

		request.addHeader("Content-Type", contentType);
		request.setContent("{\"role\":{\"roleId\":\"ROLE_TEST\", \"roleName\":\"수정됨\", \"description\":\"수정됨\", \"enabled\":\"N\", \"createUserId\":\"admin\", \"lastModifyUserId\":\"admin\"}}".getBytes());

		contentString = servlet.doDispatch(request, response);

		/*
		 * 조회
		 */
		request = new MockHttpServletRequest(HttpMethod.GET.name(), "/jcfiam/admin/roles/select");
		response = new MockHttpServletResponse();

		request.addHeader("Content-Type", contentType);
		request.addParameter("roleId", "ROLE_TEST");

		contentString = servlet.doDispatch(request, response);

		assertRole(contentString, "role", "ROLE_TEST", "수정됨", "수정됨", "N");

		/*
		 * 삭제
		 */
		request = new MockHttpServletRequest(HttpMethod.DELETE.name(), "/jcfiam/admin/roles/delete");
		response = new MockHttpServletResponse();

		request.addHeader("Content-Type", contentType);
		request.addParameter("roleId", "ROLE_TEST");

		contentString = servlet.doDispatch(request, response);

		/*
		 * 조회
		 */
		request = new MockHttpServletRequest(HttpMethod.GET.name(), "/jcfiam/admin/roles/select");
		response = new MockHttpServletResponse();

		request.addHeader("Content-Type", contentType);
		request.addParameter("roleId", "ROLE_TEST");

		contentString = servlet.doDispatch(request, response);

		System.out.println(contentString);
	}

	@SuppressWarnings("unchecked")
	private void assertRole(String contentString, String root, String roleId, String roleName, String description, String enabled) throws Exception {
		Map<String, List<Map<String, Object>>> resultMap = mapper.readValue(contentString, HashMap.class);

		List<Map<String, Object>> roleList = resultMap.get(root);

		Assert.assertNotNull(roleList);

		Assert.assertEquals(roleId, roleList.get(0).get("roleId"));
		Assert.assertEquals(roleName, roleList.get(0).get("roleName"));
		Assert.assertEquals(description, roleList.get(0).get("description"));
		Assert.assertEquals(enabled, roleList.get(0).get("enabled"));
	}
}
