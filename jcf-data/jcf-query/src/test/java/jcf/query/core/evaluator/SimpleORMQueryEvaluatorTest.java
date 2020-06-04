package jcf.query.core.evaluator;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import jcf.query.TemplateEngineType;
import jcf.query.core.QueryTemplate;
import jcf.query.core.annotation.orm.ColumnDef;
import jcf.query.core.annotation.orm.OrderBy;
import jcf.query.core.annotation.orm.PrimaryKey;
import jcf.query.core.annotation.orm.ReferenceKey;
import jcf.query.core.annotation.orm.TableDef;
import jcf.query.core.annotation.orm.Updatable;
import jcf.query.core.evaluator.definition.ColumnType;
import jcf.query.core.evaluator.definition.KeyType;
import jcf.query.core.evaluator.definition.QueryStatement;
import jcf.query.domain.ProductRelationMapping;

import org.junit.Before;
import org.junit.Test;

public class SimpleORMQueryEvaluatorTest {

	private QueryTemplate queryTemplate;

	@Before
	public void setup() {
		Map<TemplateEngineType, QueryEvaluator> queryEvaluators = new HashMap<TemplateEngineType, QueryEvaluator>();
		queryEvaluators.put(TemplateEngineType.SIMPLE_ORM, new SimpleORMQueryEvaluator());

		queryTemplate = new QueryTemplate();
		queryTemplate.setDefaultTemplate(TemplateEngineType.SIMPLE_ORM);
		queryTemplate.setQueryEvaluators(queryEvaluators);
	}

	@Test
	public void testSelectQuery()	{
		ProductRelationMapping product = new ProductRelationMapping();
		product.setProductId(1004);

		QueryMetaData metadata = queryTemplate.getQuery(SimpleORMQueryType.SELECT, product);

		String expected = "SELECT PRODUCT_ID productId ,PRODUCT_TYPE_ID productTypeId ,PRODUCT_NAME productName ,PRODUCT_DESCRIPTION productDescription ,UPDATED updated FROM PRODUCT  WHERE PRODUCT_ID = :productId";
		String actual = metadata.getStatement();

		assertThat(actual, is(expected));
	}

	@Test
	public void testSelectJoinQuery()	{
		SecuredResourcesRoles role = new SecuredResourcesRoles();

		QueryMetaData metadata = queryTemplate.getQuery(SimpleORMQueryType.SELECT, role);

		String expected = "SELECT v1.VIEW_RESOURCE_ID viewResourceId ,v1.ROLE_ID roleId ,v1.permission_id permissionId ,to_char( v1.CREATE_DATE , 'yyyy-mm-dd hh24:mi:ss') createDate ,v1.CREATE_USER_ID createUserId ,to_char( v1.LAST_MODIFY_DATE , 'yyyy-mm-dd hh24:mi:ss') lastModifyDate ,v1.LAST_MODIFY_USER_ID lastModifyUserId ,v2.VIEW_RESOURCE_ID viewResourceId ,v2.VIEW_RESOURCE_PATTERN viewResourcePattern ,v2.VIEW_RESOURCE_SEQ viewResourceSeq FROM VIEW_RESOURCES_ROLES v1 ,VIEW_RESOURCES v2  WHERE v1.VIEW_RESOURCE_ID = v2.VIEW_RESOURCE_ID  ORDER BY v2.VIEW_RESOURCE_SEQ ";
		String actual = metadata.getStatement();

		assertThat(actual, is(expected));
	}

	@Test
	public void testInsertQuery()	{
		ProductRelationMapping product = new ProductRelationMapping();
		product.setProductId(1004);

		QueryMetaData metadata = queryTemplate.getQuery(SimpleORMQueryType.INSERT, product);

		String expected = "INSERT INTO PRODUCT (PRODUCT_ID , PRODUCT_TYPE_ID , PRODUCT_NAME , PRODUCT_DESCRIPTION , UPDATED ) VALUES (:productId, :productTypeId, :productName, :productDescription, sysdate)";
		String actual = metadata.getStatement();

		assertThat(actual, is(expected));
	}

	@Test
	public void testUpdateQuery()	{
		ProductRelationMapping product = new ProductRelationMapping();
		product.setProductId(1004);

		QueryMetaData metadata = queryTemplate.getQuery(SimpleORMQueryType.UPDATE, product);

		String expected = "UPDATE PRODUCT SET PRODUCT_TYPE_ID = :productTypeId, PRODUCT_NAME = :productName, PRODUCT_DESCRIPTION = :productDescription, UPDATED = sysdate  WHERE PRODUCT_ID = :productId";
		String actual = metadata.getStatement();

		assertThat(actual, is(expected));
	}

	@Test
	public void testDeleteQuery()	{
		ProductRelationMapping product = new ProductRelationMapping();
		product.setProductId(1004);

		QueryMetaData metadata = queryTemplate.getQuery(SimpleORMQueryType.DELETE, product);

		String expected = "DELETE FROM PRODUCT  WHERE PRODUCT_ID = :productId";
		String actual = metadata.getStatement();

		assertThat(actual, is(expected));
	}

	/*
	 * JOIN TEST
	 */
	@TableDef(tableName = "VIEW_RESOURCES_ROLES", alias = "v1")
	public static class SecuredResourcesRoles implements QueryStatement {

		@ReferenceKey(targetObject = "securedResourcesPermissions", targetField = "viewResourceId")
		@ColumnDef(columnName = "VIEW_RESOURCE_ID")
		private String viewResourceId;

		@ColumnDef(columnName = "ROLE_ID")
		private String roleId;

		@ColumnDef(columnName = "permission_id")
		private String permissionId;

		@ColumnDef(columnName = "CREATE_DATE", columnType = ColumnType.DATE, prefix = "to_char(", suffix = ", 'yyyy-mm-dd hh24:mi:ss')")
		private String createDate;

		@ColumnDef(columnName = "CREATE_USER_ID")
		private String createUserId;

		@Updatable
		@ColumnDef(columnName = "LAST_MODIFY_DATE", columnType = ColumnType.DATE, prefix = "to_char(", suffix = ", 'yyyy-mm-dd hh24:mi:ss')")
		private String lastModifyDate;

		@Updatable
		@ColumnDef(columnName = "LAST_MODIFY_USER_ID")
		private String lastModifyUserId;

		private RoleSecuredViewResource securedResourcesPermissions;

		@TableDef(tableName = "VIEW_RESOURCES", alias = "v2")
		public static class RoleSecuredViewResource implements QueryStatement {
			@ColumnDef(columnName = "VIEW_RESOURCE_ID")
			private String viewResourceId;

			@ColumnDef(columnName = "VIEW_RESOURCE_PATTERN")
			private String viewResourcePattern;

			@ColumnDef(columnName = "VIEW_RESOURCE_SEQ")
			@OrderBy(sortOrder = 0)
			private String viewResourceSeq;
		}

		public String getResourcePattern() {
			return securedResourcesPermissions.viewResourcePattern;
		}

		public String getResourceId() {
			return this.viewResourceId;
		}

		public String getResourcePermission() {
			return this.permissionId;
		}

		public String getOrderSeq() {
			return securedResourcesPermissions.viewResourceSeq;
		}
	}

	@Test
	public void testSelectJoinQuery2()	{
		TestSecuredResourcesRoles role = new TestSecuredResourcesRoles();

		QueryMetaData metadata = queryTemplate.getQuery(SimpleORMQueryType.SELECT, role);

		String actual = metadata.getStatement();

		System.out.println(actual);
	}

	@TableDef(tableName = "CO_MENU", alias = "m")
	public static class TestSecuredResourcesRoles implements QueryStatement {

		@PrimaryKey(keyType =KeyType.DYNAMIC)
		@ReferenceKey(targetObject = "privilege", targetField = "menuId")
		@ColumnDef(columnName = "menu_id")
		private String menuId;

		@ColumnDef(columnName = "array_ord")
		private String arrayOrd;

		@ColumnDef(columnName = "menu_url")
		private String resourcePattern;

		private Privilege privilege;

		@TableDef(tableName = "CO_PRIV_CTG_MENU", alias = "p")
		public static class Privilege  implements QueryStatement	{

			@ColumnDef(columnName = "menu_id")
			private String menuId;

			@ColumnDef(columnName = "priv_cd")
			private String privCd;

			public String getPrivCd() {
				return privCd;
			}
		}

		public String getOrderSeq() {
			return arrayOrd;
		}

		public String getResourceId() {
			return menuId;
		}

		public String getResourcePattern() {
			return resourcePattern;
		}

		public String getResourcePermission() {
			return privilege.getPrivCd();
		}

	}
}
