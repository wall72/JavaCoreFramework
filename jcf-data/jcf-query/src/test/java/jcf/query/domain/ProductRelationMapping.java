package jcf.query.domain;

import java.util.Date;

import jcf.query.core.annotation.orm.ColumnDef;
import jcf.query.core.annotation.orm.PrimaryKey;
import jcf.query.core.annotation.orm.TableDef;
import jcf.query.core.annotation.orm.Updatable;
import jcf.query.core.evaluator.definition.ColumnType;
import jcf.query.core.evaluator.definition.KeyType;
import jcf.query.core.evaluator.definition.QueryStatement;
import jcf.query.core.evaluator.definition.SelectStatement;

@TableDef(tableName="PRODUCT")
public class ProductRelationMapping implements QueryStatement {

	@PrimaryKey(keyType = KeyType.DYNAMIC)
	@ColumnDef(columnName = "PRODUCT_ID", columnType = ColumnType.NUMBER)
	private Integer productId;

	@Updatable
	@PrimaryKey(keyType = KeyType.DYNAMIC)
	@ColumnDef(columnName = "PRODUCT_TYPE_ID")
	private String productTypeId;

	@Updatable
	@ColumnDef(columnName = "PRODUCT_NAME")
	private String productName;

	@Updatable
	@ColumnDef(columnName = "PRODUCT_DESCRIPTION")
	private String productDescription;

	@Updatable
	@ColumnDef(columnName = "UPDATED", columnType = ColumnType.DATE)
	private Date updated;

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

	@Override
	public String toString() {
		return String
				.format("[ProductId] %d\n[ProductTypeId] %s\n[ProductName] %s\n[ProductDescription] %s\n[Updated] %s",
						productId, productTypeId, productName,
						productDescription, updated);
	}

	public String getQuery() {
		return "select * from dual";
	}
}
