package product.controller;

import java.math.BigDecimal;

import jcf.data.GridData;
import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.MciResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import product.model.Product;

@Controller
@RequestMapping("/product")
public class ProductController {

	@RequestMapping("/select")
	public void select(MciRequest request, MciResponse response) {
		Product product = request.get("product", Product.class);
		response.set("product", product);
	}

	@RequestMapping("/list")
	public void list(MciRequest request, MciResponse response) {
		GridData<Product> gridData = request.getGridData("product", Product.class);
		response.setList("product", gridData.getList());
	}

	/*  파라메타 없이 조회만 함 **/
	@RequestMapping("/selectProduct")
	public void select2(MciRequest request, MciResponse response) {
		Product p= new Product();
		p.setBigDecimal(new BigDecimal(0.55));
		p.setProductDescription("aaa");
		p.setProductId(1);
		p.setProductDescription("ddd");
		response.set("product", p);
	}
}
