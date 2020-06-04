package jcf.sua.ux.json;

import java.io.UnsupportedEncodingException;
import java.util.List;

import jcf.sua.dataset.DataSetWriter;
import jcf.sua.mvc.MciDataSetAccessor;
import jcf.sua.mvc.MciResponse;
import jcf.sua.ux.json.dataset.JsonDataSetWriter;
import jcf.sua.ux.json.mvc.JsonResponse;

import org.springframework.mock.web.MockHttpServletResponse;

public class JsonWriter {

	public String writeDataObject(Object product) throws UnsupportedEncodingException	{
		MockHttpServletResponse response = new MockHttpServletResponse();
		MciResponse jsonResponse = new JsonResponse();
		
		if(product instanceof List)	{
			jsonResponse.setList("product", (List) product);
		} else {
			jsonResponse.set("product", product);
		}
		
		DataSetWriter writer = new JsonDataSetWriter(response, (MciDataSetAccessor) jsonResponse); 
		
		writer.write();

		return response.getContentAsString();
	}
	
}
