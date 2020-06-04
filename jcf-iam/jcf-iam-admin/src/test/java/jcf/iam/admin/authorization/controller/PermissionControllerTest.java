package jcf.iam.admin.authorization.controller;

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
public class PermissionControllerTest {

	@Autowired
	private JcfSuaTestServlet servlet;

	private ObjectMapper mapper = new ObjectMapper();

	private String contentType = "application/json+sua";


	@Test
	public void 리소스세부접근권한조회() {
		MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.POST.name(), "/jcfiam/admin/permissions/select");
		MockHttpServletResponse response = new MockHttpServletResponse();

		request.addHeader("Content-Type", contentType);

		String contentString = servlet.doDispatch(request, response);

		System.out.println(contentString);
	}


	@Test
//	@Ignore
	public void 리소스세부접근권한조회추가삭제() throws Exception {
		/*
		 * 추가
		 */
		MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.POST.name(), "/jcfiam/admin/permissions/insert");
		MockHttpServletResponse response = new MockHttpServletResponse();

		request.addHeader("Content-Type", contentType);

		request.setContent("{\"permission\":{\"permissionId\":\"T\", \"description\":\"TEST\", \"createUserId\":\"admin\", \"lastModifyUserId\":\"admin\"}}".getBytes());

		String contentString = servlet.doDispatch(request, response);

		/*
		 * 조회
		 */
		request = new MockHttpServletRequest(HttpMethod.GET.name(), "/jcfiam/admin/permissions/select");
		response = new MockHttpServletResponse();

		request.addHeader("Content-Type", contentType);

		contentString = servlet.doDispatch(request, response);

		assertPermission(contentString, "permissions", "T", "TEST");

		/*
		 * 수정
		 */
		request = new MockHttpServletRequest(HttpMethod.PUT.name(), "/jcfiam/admin/permissions/update");
		response = new MockHttpServletResponse();

		request.addHeader("Content-Type", contentType);
		request.setContent("{\"permission\":{\"permissionId\":\"T\", \"description\":\"TEST1111\", \"createUserId\":\"admin\", \"lastModifyUserId\":\"admin\"}}".getBytes());

		contentString = servlet.doDispatch(request, response);

		/*
		 * 조회
		 */
		request = new MockHttpServletRequest(HttpMethod.GET.name(), "/jcfiam/admin/permissions/select");
		response = new MockHttpServletResponse();

		request.addHeader("Content-Type", contentType);

		contentString = servlet.doDispatch(request, response);

		assertPermission(contentString, "permissions", "T", "TEST1111");

		/*
		 * 삭제
		 */
		request = new MockHttpServletRequest(HttpMethod.DELETE.name(), "/jcfiam/admin/permissions/delete");
		response = new MockHttpServletResponse();

		request.addHeader("Content-Type", contentType);
		request.addParameter("permissionId", "T");

		contentString = servlet.doDispatch(request, response);

		/*
		 * 조회
		 */
		request = new MockHttpServletRequest(HttpMethod.GET.name(), "/jcfiam/admin/permissions/select");
		response = new MockHttpServletResponse();

		request.addHeader("Content-Type", contentType);

		contentString = servlet.doDispatch(request, response);

		System.out.println(contentString);
	}

	@SuppressWarnings("unchecked")
	private void assertPermission(String contentString, String root, String permissionId, String description) throws Exception {
		Map<String, List<Map<String, Object>>> resultMap = mapper.readValue(contentString, HashMap.class);

		List<Map<String, Object>> permissionList = resultMap.get(root);

		Assert.assertNotNull(permissionList);

		boolean result = false;

		for(Map<String, Object> map : permissionList)	{
			if((result = map.get("permissionId").equals(permissionId) && map.get("description").equals(description)))	{
				break;
			}
		}

		Assert.assertTrue(result);
	}
}
