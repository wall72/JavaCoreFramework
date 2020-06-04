package jcf.iam.admin.authentication.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jcf.iam.admin.authentication.service.RoleService;
import jcf.iam.admin.authentication.service.UserService;
import jcf.iam.core.authentication.userdetails.model.Role;
import jcf.iam.core.authentication.userdetails.model.User;
import jcf.sua.JcfSuaTestServlet;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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
public class AuthorityControllerTest {

	@Autowired
	private JcfSuaTestServlet servlet;

	@Autowired
	private RoleService roleService;

	@Autowired
	private UserService userService;

	private ObjectMapper mapper = new ObjectMapper();

	private String contentType = "application/json+sua";

	@Test
	public void 사용자권한리스트조회() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.GET.name(), "/jcfiam/admin/authorities/selectAuthorities");
		MockHttpServletResponse response = new MockHttpServletResponse();

		request.addHeader("Content-Type", contentType);
		request.addParameter("username", "nolang");

		String contentString = servlet.doDispatch(request, response);

		assertUserAuthority(contentString, "authorityList", "ROLE_USER");
		assertUserAuthority(contentString, "authorityList", "ROLE_ADMIN");
	}

	@Test
	public void 사용자권한추가삭제조회() throws Exception {
		/*
		 * 추가
		 */
		MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.POST.name(), "/jcfiam/admin/authorities/insert");
		MockHttpServletResponse response = new MockHttpServletResponse();

		request.addHeader("Content-Type", contentType);
		request.setContent("{\"userAuthority\":{\"userId\":\"testuser\", \"roleId\":\"ROLE_TEST\", \"createUserId\":\"admin\", \"lastModifyUserId\":\"admin\"}}".getBytes());

		String contentString = servlet.doDispatch(request, response);

		/*
		 * 조회
		 */
		request = new MockHttpServletRequest(HttpMethod.GET.name(), "/jcfiam/admin/authorities/selectAuthorities");
		response = new MockHttpServletResponse();

		request.addHeader("Content-Type", contentType);
		request.addParameter("username", "testuser");

		contentString = servlet.doDispatch(request, response);

		assertUserAuthority(contentString, "authorityList", "ROLE_TEST");

		/*
		 * 삭제
		 */
		request = new MockHttpServletRequest(HttpMethod.DELETE.name(), "/jcfiam/admin/authorities/delete");
		response = new MockHttpServletResponse();

		request.addHeader("Content-Type", contentType);
		request.addParameter("username", "testuser");

		contentString = servlet.doDispatch(request, response);

		/*
		 * 조회
		 */
		request = new MockHttpServletRequest(HttpMethod.GET.name(), "/jcfiam/admin/authorities/selectAuthorities");
		response = new MockHttpServletResponse();

		request.addHeader("Content-Type", contentType);
		request.addParameter("username", "testuser");

		contentString = servlet.doDispatch(request, response);
	}

	@SuppressWarnings("unchecked")
	private void assertUserAuthority(String contentString, String root, String authorityName) throws Exception {
		Map<String, List<Map<String, Object>>> resultMap = mapper.readValue(contentString, HashMap.class);

		List<Map<String, Object>> authorityList = resultMap.get(root);

		Assert.assertNotNull(authorityList);

		boolean result = false;

		for(Map<String, Object> map : authorityList)	{
			if((result = map.get("roleId").equals(authorityName)))	{
				break;
			}
		}

		Assert.assertTrue(result);
	}


	@Before
	public void before() {
		User user = new User();

		user.setUsername("testuser");
		user.setEnabled("Y");
		user.setPassword("비밀번호");
		user.setCreateUserId("nolang");
		user.setLastModifyUserId("nolang");

		userService.insertUser(user);

		Role role = new Role();

		role.setRoleId("ROLE_TEST");
		role.setRoleName("테스트권한");

		roleService.insertRole(role);
	}

	@After
	public void after() {
		userService.deleteUser("testuser");
		roleService.deleteUser("ROLE_TEST");
	}
}
