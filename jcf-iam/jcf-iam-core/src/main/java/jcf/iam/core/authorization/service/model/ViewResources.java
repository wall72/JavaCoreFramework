package jcf.iam.core.authorization.service.model;

import jcf.iam.core.jdbc.IamDefaultTableNames;
import jcf.iam.core.jdbc.authorization.ViewResourcesMapping;
import jcf.query.core.annotation.orm.ColumnDef;
import jcf.query.core.annotation.orm.OrderBy;
import jcf.query.core.annotation.orm.PrimaryKey;
import jcf.query.core.annotation.orm.TableDef;
import jcf.query.core.annotation.orm.Updatable;
import jcf.query.core.evaluator.definition.ColumnType;
import jcf.query.core.evaluator.definition.KeyType;

/**
 * <pre>
 * JCF IAM Default ViewResource (화면) 처리 모델
 * <pre>
 *
 * @see jcf.iam.core.jdbc.authorization.ViewResourcesMapping
 *
 * @author nolang
 *
 */
@TableDef(tableName = IamDefaultTableNames.VIEW_RESOURCES)
public class ViewResources implements ViewResourcesMapping {

	@PrimaryKey(keyType = KeyType.DYNAMIC)
	@ColumnDef(columnName = "VIEW_RESOURCE_ID")
	private String viewResourceId;

	@Updatable
	@PrimaryKey(keyType = KeyType.DYNAMIC, prefix = "nvl(", suffix = ", ' ')")
	@ColumnDef(columnName = "PARENT_VIEW_ID")
	private String parentViewId;

	@Updatable
	@ColumnDef(columnName = "VIEW_RESOURCE_NAME")
	private String viewResourceName;

	@Updatable
	@ColumnDef(columnName = "VIEW_RESOURCE_SEQ")
	@OrderBy(sortOrder = 0)
	private String viewResourceSeq;

	@Updatable
	@ColumnDef(columnName = "DESCRIPTION")
	private String description;

	@Updatable
	@ColumnDef(columnName = "VIEW_RESOURCE_URL")
	private String viewResourceUrl;

	@Updatable
	@ColumnDef(columnName = "VIEW_RESOURCE_PATTERN")
	private String viewResourcePattern;

	@ColumnDef(columnName = "CREATE_DATE", columnType = ColumnType.DATE)
	private String createDate;

	@ColumnDef(columnName = "CREATE_USER_ID")
	private String createUserId;

	@Updatable
	@ColumnDef(columnName = "LAST_MODIFY_DATE", columnType = ColumnType.DATE)
	private String lastModifyDate;

	@Updatable
	@ColumnDef(columnName = "LAST_MODIFY_USER_ID")
	private String lastModifyUserId;

	public String getViewResourceId() {
		return viewResourceId;
	}

	public void setViewResourceId(String viewResourceId) {
		this.viewResourceId = viewResourceId;
	}

	public String getViewResourceName() {
		return viewResourceName;
	}

	public void setViewResourceName(String viewResourceName) {
		this.viewResourceName = viewResourceName;
	}

	public String getViewResourceSeq() {
		return viewResourceSeq;
	}

	public void setViewResourceSeq(String viewResourceSeq) {
		this.viewResourceSeq = viewResourceSeq;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getViewResourceUrl() {
		return viewResourceUrl;
	}

	public void setViewResourceUrl(String viewResourceUrl) {
		this.viewResourceUrl = viewResourceUrl;
	}

	public String getViewResourcePattern() {
		return viewResourcePattern;
	}

	public void setViewResourcePattern(String viewResourcePattern) {
		this.viewResourcePattern = viewResourcePattern;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public String getLastModifyDate() {
		return lastModifyDate;
	}

	public void setLastModifyDate(String lastModifyDate) {
		this.lastModifyDate = lastModifyDate;
	}

	public String getLastModifyUserId() {
		return lastModifyUserId;
	}

	public void setLastModifyUserId(String lastModifyUserId) {
		this.lastModifyUserId = lastModifyUserId;
	}

	public void setParentViewId(String parentViewId) {
		this.parentViewId = parentViewId;
	}

	public String getParentViewId() {
		return parentViewId;
	}
}
