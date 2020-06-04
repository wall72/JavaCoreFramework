package jcf.sua.support.validation;

import jcf.sua.TestDispachterServlet;
import jcf.sua.ux.UxConstants;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@Ignore
public class ValidationlTest {

	private TestDispachterServlet servlet = new TestDispachterServlet(
			"jcf/sua/support/validation/ValidationTest-Context.xml");

	@Test
	public void JSR303벨리데이션테스트() throws Exception {
		String url = "/sample/validation";

		MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.GET.name(), url);
		MockHttpServletResponse response = new MockHttpServletResponse();

		request.addHeader("Accept", "text/xml");
		request.setContent("crud=U&type=test&id=haibane84&name=고재도&num=3"
				.getBytes(UxConstants.DEFAULT_CHARSET));

		servlet.service(request, response);

		System.out.println(response.getContentAsString());
	}

}
