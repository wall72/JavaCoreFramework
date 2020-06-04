package jcf.sua.ux.miplatform;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import jcf.sua.TestDispatcherServlet;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * 마이플랫톰용 테스트 케이스
 * @author mina
 *
 */
public class MiplatformChannelTest {

	private TestDispatcherServlet servlet = new TestDispatcherServlet("jcf/sua/ux/miplatform/miplatfomChannelTest-Context.xml");


	/**
	 *  단건 조회 용 테스트
	 * /product/selectProduct 로 request  를 보내 ds_product.xml   파일과 일치하는지 비교
	 * @throws Exception
	 */
	@Test@Ignore
	public void testSelectProduct() throws Exception	{
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/product/selectProduct");
		MockHttpServletResponse response = new MockHttpServletResponse();
		request.setCharacterEncoding("utf-8");
		request.addHeader("User-Agent", "MiPlatform 3.2");
		servlet.service(request, response);
		String actual= response.getContentAsString().replace("\r", "");
		String path = MiplatformChannelTest.class.getResource("").getPath();
		String  expected=    FileUtils.readFileToString(new File(path+"ds_product.xml"));
		assertThat(actual, is(expected));
	}

}
