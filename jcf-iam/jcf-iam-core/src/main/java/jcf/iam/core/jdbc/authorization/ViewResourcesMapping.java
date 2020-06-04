package jcf.iam.core.jdbc.authorization;

import jcf.iam.core.jdbc.SecurityStatement;

/**
 * <pre>
 * ViewResource (화면) 처리 모델 클래스를 정의하기 위해 구현한다. (화면단위 ACL 기능을 사용하기 위해서는 반드시 해당 인터페이스의 구현체가 필요하다.)
 *
 *  ex)
 *    - 화면관리 테이블의 정의가 아래와 같다고 가정할 때..
 *
 *      create table JCFIAM_VIEW_RESOURCES
 *      (
 *        VIEW_RESOURCE_ID    VARCHAR2(16) not null,
 *        VIEW_RESOURCE_NAME  VARCHAR2(64) not null,
 *        VIEW_RESOURCE_SEQ   VARCHAR2(3),
 *        DESCRIPTION         VARCHAR2(512),
 *        VIEW_RESOURCE_URL   VARCHAR2(128),
 *        VIEW_RESOURCE_PATTERN   VARCHAR2(128),
 *        CREATE_DATE         DATE,
 *        CREATE_USER_ID      VARCHAR2(16),
 *        LAST_MODIFY_DATE    DATE,
 *        LAST_MODIFY_USER_ID VARCHAR2(16)
 *      );
 *      alter table JCFIAM_VIEW_RESOURCES
 *        add constraint JCFIAM_VIEW_RESOURCES_PK primary key (VIEW_RESOURCE_ID);
 *
 *    - 위의 테이블과 매핑되는 화면관리 클래스는 아래와 같이 정의한다.
 *
 *      &#64;TableDef(tableName = "JCFIAM_VIEW_RESOURCES")
 *      public class ViewResources implements ViewResourcesMapping {
 *
 *          &#64;PrimaryKey(keyType = KeyType.DYNAMIC)
 *          &#64;ColumnDef(columnName = "VIEW_RESOURCE_ID")
 *          private String viewResourceId;
 *
 *          ...
 *      }
 * <pre>
 *
 * @see jcf.iam.core.jdbc.acl.ViewResources
 *
 * @author nolang
 *
 */
public interface ViewResourcesMapping extends SecurityStatement {

	/**
	 *
	 * 화면의 ID를 반환한다.
	 *
	 * @return
	 */
	String getViewResourceId();

	/**
	 *
	 * 화면의 ID를 설정한다.
	 *
	 * @param viewResourceId
	 */
	void setViewResourceId(String viewResourceId);

	/**
	 *
	 * 화면의 이름을 반환한다.
	 *
	 * @return
	 */
	String getViewResourceName();

	/**
	 *
	 * 화면의 URL (접근주소) 를 반환한다.
	 *
	 * @return
	 */
	String getViewResourceUrl();

	/**
	 *
	 * 계층메뉴를 사용할 경우 해당 화면의 상위 메뉴의 화면 ID를 반환한다.
	 *
	 * @return
	 */
	String getParentViewId();

	/**
	 *
	 * 상위메뉴의 화면ID를 설정한다.
	 *
	 * @param parentViewId
	 */
	void setParentViewId(String parentViewId);

}

