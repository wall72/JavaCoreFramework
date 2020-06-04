package jcf.sua.ux;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;

import product.model.Product;

@Ignore
public class TestDataObjectBuilder {

	public Product buildSingleData()	{
		return new Product(1001, "CARRIERS", "살물선", "곡물 ·광석 ·석탄 등을 포장하지 않고 그대로 선창에 싣고 수송하는 화물선");
	}

	public List<Product> buildMultiData()	{
		List<Product> list = new ArrayList<Product>();
		
		list.add(new Product(1001, "CARRIERS", "살물선", "곡물 ·광석 ·석탄 등을 포장하지 않고 그대로 선창에 싣고 수송하는 화물선"));
		list.add(new Product(1002, "CARRIERS", "유조선", "석유류·경유·당밀·포도주원액·화공약품 및 액화석유가스(LPG)·액화천연가스(LNG) 등 액체화물을 용기에 넣지 않은 비포장 상태로 산적하여 대량수송하는 선박"));
		list.add(new Product(1003, "CARRIERS", "컨테이너선", "컨테이너를 수송하는 선박"));
		list.add(new Product(1004, "CARRIERS", "FPSO선", "Floating Production Storage Offloading, 부유식 원유생산저장 설비"));
		
		return list;
	}
	
}
