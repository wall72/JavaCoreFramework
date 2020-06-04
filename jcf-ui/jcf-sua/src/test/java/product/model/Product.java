package product.model;

import java.math.BigDecimal;
import java.util.Date;

public class Product {

	private Integer productId;

	private String productTypeId;

	private String productName;

	private String productDescription;

	private Date updated;

	private BigDecimal bigDecimal;

	public Product() {

	}

	public Product(Integer productId, String productTypeId, String productName, String productDescription ){
		this.productId = productId;
		this.productTypeId = productTypeId;
		this.productName = productName;
		this.productDescription = productDescription;
	}


	public Product(Integer productId, String productTypeId, String productName, String productDescription,BigDecimal bigDecimal ){
		this.productId = productId;
		this.productTypeId = productTypeId;
		this.productName = productName;
		this.productDescription = productDescription;
		this.bigDecimal = bigDecimal;
	}



	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getProductTypeId() {
		return productTypeId;
	}

	public void setProductTypeId(String productTypeId) {
		this.productTypeId = productTypeId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}



	public BigDecimal getBigDecimal() {
		return bigDecimal;
	}

	public void setBigDecimal(BigDecimal bigDecimal) {
		this.bigDecimal = bigDecimal;
	}

	@Override
	public String toString() {
		return String
				.format("[ProductId] %d\n[ProductTypeId] %s\n[ProductName] %s\n[ProductDescription] %s\n[Updated] %s",
						productId, productTypeId, productName,
						productDescription, updated);
	}
}
