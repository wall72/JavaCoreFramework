package jcf.sua.support.rad.controller;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jcf.sua.TestDispachterServlet;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.ReflectionUtils;

/**
 *
 *
 * @author nolang
 *
 */
@Ignore
public class RadSupportControllerTest {

	private TestDispachterServlet servlet = new TestDispachterServlet("jcf/sua/support/rad/controller/RadSupportControllerTest-Context.xml");
	private ObjectMapper mapper = new ObjectMapper();

	@Test
	public void RADJson() throws Exception {
		MockHttpServletRequest request =  new MockHttpServletRequest(HttpMethod.GET.name(), "/sql/select/users");
		MockHttpServletResponse response = new MockHttpServletResponse();

		request.addHeader("Content-Type", "application/json+sua");
		request.addParameter("_sqlId", "IAMUSER.selectUsers");
		request.addParameter("userId", "nolang");

		servlet.service(request, response);

		System.out.println(response.getContentAsString());

		assertResult(response.getContentAsString(), "users", "nolang", "nolang", "Y");
	}

	@Test
	public void RADMiplatform() throws Exception {
		MockHttpServletRequest request =  new MockHttpServletRequest(HttpMethod.GET.name(), "/sql/select/users");
		MockHttpServletResponse response = new MockHttpServletResponse();

		request.addHeader("User-Agent", "MiPlatform 3.2");
		request.addParameter("_sqlId", "IAMUSER.selectUsers");
		request.addParameter("userId", "nolang");

		servlet.service(request, response);

		System.out.println(response.getContentAsString());
	}

	@Test
	public void RADSave() throws Exception {
		/*
		 * �߰�
		 */
		MockHttpServletRequest request =  new MockHttpServletRequest(HttpMethod.POST.name(), "/sql/insert/users");
		MockHttpServletResponse response = new MockHttpServletResponse();

		request.addHeader("Content-Type", "application/json+sua");
		request.addParameter("_sqlId", "IAMUSER.insertUser");
		request.setContent("{\"users\":{\"userId\":\"testuser\", \"userName\":\"testname\", \"password\":\"testpassword\", \"enabled\":\"Y\", \"createUserId\":\"admin\", \"lastModifyUserId\":\"admin\"}}".getBytes());

		servlet.service(request, response);

		/*
		 * ��ȸ
		 */
		request =  new MockHttpServletRequest(HttpMethod.GET.name(), "/sql/select/users");
		response = new MockHttpServletResponse();

		request.addHeader("Content-Type", "application/json+sua");
		request.addParameter("_sqlId", "IAMUSER.selectUsers");
		request.addParameter("userId", "testuser");

		servlet.service(request, response);

		assertResult(response.getContentAsString(), "users", "testuser", "testpassword", "Y");

		/*
		 * ����
		 */
		request =  new MockHttpServletRequest(HttpMethod.DELETE.name(), "/sql/update/users");
		response = new MockHttpServletResponse();

		request.addHeader("Content-Type", "application/json+sua");
		request.addParameter("_sqlId", "IAMUSER.updateUser");
		request.setContent("{\"users\":{\"userId\":\"testuser\", \"userName\":\"testname\", \"password\":\"change\", \"enabled\":\"Y\", \"createUserId\":\"admin\", \"lastModifyUserId\":\"admin\"}}".getBytes());

		servlet.service(request, response);

		/*
		 * ��ȸ
		 */
		request =  new MockHttpServletRequest(HttpMethod.GET.name(), "/sql/select/users");
		response = new MockHttpServletResponse();

		request.addHeader("Content-Type", "application/json+sua");
		request.addParameter("_sqlId", "IAMUSER.selectUsers");
		request.addParameter("userId", "testuser");

		servlet.service(request, response);

		assertResult(response.getContentAsString(), "users", "testuser", "change", "Y");

		/*
		 * ����
		 */
		request =  new MockHttpServletRequest(HttpMethod.DELETE.name(), "/sql/delete/users");
		response = new MockHttpServletResponse();

		request.addHeader("Content-Type", "application/json+sua");
		request.addParameter("_sqlId", "IAMUSER.deleteUser");
		request.setContent("{\"users\":{\"userId\":\"testuser\"}}".getBytes());

		servlet.service(request, response);
	}

	@SuppressWarnings("unchecked")
	private void assertResult(String contentString, String root, String username, String password, String enabled) throws Exception {
		Map<String, List<Map<String, Object>>> resultMap = mapper.readValue(contentString, HashMap.class);

		List<Map<String, Object>> userList = resultMap.get(root);

		Assert.assertNotNull(userList);

		Assert.assertEquals(username, userList.get(0).get("userId"));
		Assert.assertEquals(password, userList.get(0).get("password"));
		Assert.assertEquals(enabled, userList.get(0).get("enabled"));
	}

	@Test
	@Ignore
	public void test()	{
		String sqlId = "sqlmap.TestQuery.select";
		Object statement = "";

		try {
			int index = sqlId.lastIndexOf(".");

			Object query = Class.forName(sqlId.substring(0, index)).newInstance();
			Field field = ReflectionUtils.findField(query.getClass(), sqlId.substring(index + 1));
			field.setAccessible(true);
			statement = ReflectionUtils.getField(field, query);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		System.out.println(statement);
	}
}
