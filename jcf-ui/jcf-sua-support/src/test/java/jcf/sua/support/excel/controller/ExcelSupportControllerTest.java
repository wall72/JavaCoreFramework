package jcf.sua.support.excel.controller;

import jcf.sua.TestDispachterServlet;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 *
 * SUA SUPPORT 엑셀 만들기 테스트
 *
 * @author nolang
 *
 */
//@Ignore
public class ExcelSupportControllerTest {

	@Test
	public void testGenerateExcelFile() throws Exception {
		TestDispachterServlet servlet = new TestDispachterServlet(
				"jcf/sua/support/excel/controller/ExcelSupportControllerTest-Context.xml");

		MockHttpServletRequest request = new MockHttpServletRequest(
				HttpMethod.GET.name(), "/sua/excel/download/users");
		MockHttpServletResponse response = new MockHttpServletResponse();

		request.addHeader("Content-Type", "application/json+sua");
		request.addParameter("_sqlId", "IAMUSER.selectUsers");

		servlet.service(request, response);

		System.out.println(response.getContentAsString());
	}
}
