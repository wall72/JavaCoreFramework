package jcf.iam.admin.view.controller;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jcf.sua.JcfSuaTestServlet;

import org.codehaus.jackson.map.ObjectMapper;
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
public class ViewResourcesControllerTest {

	@Autowired(required=false)
	private JcfSuaTestServlet servlet;

	@Test
	public void 메뉴트리조회() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.POST.name(), "/jcfiam/admin/view/selectMenuTree");
		MockHttpServletResponse response = new MockHttpServletResponse();

		request.addHeader("Content-Type", "application/json+sua");

		String contentString = servlet.doDispatch(request, response);

		System.out.println(contentString);
	}

	@Test
	public void 메뉴항목정보조회() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.POST.name(), "/jcfiam/admin/view/selectMenu");
		MockHttpServletResponse response = new MockHttpServletResponse();

		request.addHeader("Content-Type", "application/json+sua");
		request.addParameter("viewResourceId", "AAA009");

		String contentString = servlet.doDispatch(request, response);

		ObjectMapper mapper = new ObjectMapper();

		Map<String, List<Map<String, Object>>> map = mapper.readValue(contentString, HashMap.class);
		List<Map<String, Object>> menuList = (List<Map<String, Object>>) map.get("menu");

		assertEquals("AAA009", menuList.get(0).get("viewResourceId"));
//		assertEquals("신규요청장애등록", menuList.get(0).get("viewResourceName"));
	}

}
