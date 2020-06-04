package jcf.sua.ux.json;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jcf.sua.mvc.MciRequest;
import jcf.sua.ux.json.dataset.JsonDataSetReader;
import jcf.sua.ux.json.mvc.JsonRequest;

import org.springframework.mock.web.MockHttpServletRequest;

import product.model.Product;

public class JsonReader {

	public Product readSingleDataObject(String responseJSON) {
		MockHttpServletRequest httpRequest = new MockHttpServletRequest();
		
		httpRequest.setContent(responseJSON.getBytes());
		
		MciRequest request = new JsonRequest(new JsonDataSetReader(httpRequest, null), null);
		
		return request.get("product", Product.class);
	}

	public List<Product> readMultiDataObject(String responseJSON) {
		MockHttpServletRequest httpRequest = new MockHttpServletRequest();
		
		httpRequest.setContent(responseJSON.getBytes());
		
		MciRequest request = new JsonRequest(new JsonDataSetReader(httpRequest, null), null);
		
		List<Product> list = request.getGridData("product", Product.class).getList();
		
		Collections.sort(list, new Comparator<Product>() {
			public int compare(Product o1, Product o2) {
				return o1.getProductId() - o2.getProductId();
			}
		});
		
		return list;
	}
}
