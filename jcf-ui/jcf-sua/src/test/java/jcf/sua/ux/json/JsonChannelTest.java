package jcf.sua.ux.json;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jcf.sua.TestDispatcherServlet;
import jcf.sua.dataset.DataSetWriter;
import jcf.sua.mvc.MciDataSetAccessor;
import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.MciResponse;
import jcf.sua.ux.TestDataObjectBuilder;
import jcf.sua.ux.json.dataset.JsonDataSetReader;
import jcf.sua.ux.json.dataset.JsonDataSetWriter;
import jcf.sua.ux.json.mvc.JsonRequest;
import jcf.sua.ux.json.mvc.JsonResponse;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import product.model.Product;


/**
 * 
 * <pre>
 * JSON 채널을 사용하여 서버와 연동하는 기능를 테스트한다.
 * 
 * - 가정
 *   1. 서버와의 연동에 사용되는 컨트롤러의 각 핸들러 메소드는 클라이언트에서 전송된 데이터를
 *      가공하지 않고 다시 클라이언트로 반환한다.    
 * </pre>
 * 
 * @author nolang
 *
 */
public class JsonChannelTest {

	private TestDispatcherServlet servlet = new TestDispatcherServlet("jcf/sua/ux/json/JsonChannelTest-Context.xml");
	private TestDataObjectBuilder builder = new TestDataObjectBuilder();
	private JsonReader reader = new JsonReader();
	private JsonWriter writer = new JsonWriter();
	
	@Test@Ignore
	public void 단건데이터_연동을_테스트한다() throws Exception	{
		Product expected = builder.buildSingleData();
		
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/product/select");
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		request.setContentType("application/json+sua");
		request.setContent(writer.writeDataObject(expected).getBytes());
		
		servlet.service(request, response);
		
		Product actual = reader.readSingleDataObject(response.getContentAsString());
		
		assertThat(actual.getProductId(), is(expected.getProductId()));
		assertThat(actual.getProductTypeId(), is(expected.getProductTypeId()));
		assertThat(actual.getProductName(), is(expected.getProductName()));
		assertThat(actual.getProductDescription(), is(expected.getProductDescription()));
	}

	@Test@Ignore
	public void 다건데이터_연동을_테스트한다() throws Exception	{
		List<Product> expected = builder.buildMultiData();
		
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/product/list");
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		request.setContentType("application/json+sua");
		request.setContent(writer.writeDataObject(expected).getBytes());
		
		servlet.service(request, response);
		
		List<Product> actual = reader.readMultiDataObject(response.getContentAsString());

		assertThat(actual.size(), is(expected.size()));
		
		for (int i = 0; i < expected.size(); ++i) {
			assertThat(actual.get(i).getProductId(), is(expected.get(i).getProductId()));
			assertThat(actual.get(i).getProductTypeId(), is(expected.get(i).getProductTypeId()));
			assertThat(actual.get(i).getProductName(), is(expected.get(i).getProductName()));
			assertThat(actual.get(i).getProductDescription(), is(expected.get(i).getProductDescription()));
		}
	}
	
}
