package jcf.query.core.annotation.orm;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * 테이블간의 JOIN 정보를 지정한다.
 *
 * ex)
 *
 *   - 조인대상 테이블의 정의가 아래와 같다고 가정할 때
 *
 *      create table JCFIAM_VIEW_RESOURCES_ROLES
 *      (
 *        VIEW_RESOURCE_ID    VARCHAR2(16) not null,
 *        ROLE_ID             VARCHAR2(16) not null,
 *        PERMISSION_ID       VARCHAR2(16) not null,
 *        CREATE_DATE         DATE,
 *        CREATE_USER_ID      VARCHAR2(16),
 *        LAST_MODIFY_DATE    DATE,
 *        LAST_MODIFY_USER_ID VARCHAR2(16)
 *      );
 *      alter table JCFIAM_VIEW_RESOURCES_ROLES
 *        add constraint JCFIAM_VIEW_RESRC_ROLES_FK01 foreign key (VIEW_RESOURCE_ID)
 *        references JCFIAM_VIEW_RESOURCES (VIEW_RESOURCE_ID);
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
 *
 *   - 위의 테이블과 매핑되는 클래스는 아래와 같이 정의한다.
 *
 *     &#64;TableDef(tableName = "jcfiam_view_resources_roles", alias = "v1") // Driving 테이블 정의
 *      public class SecuredResourcesRoles implements SecuredResourcesMapping {
 *
 *          // targetObject 에 조인될 테이블의 참조 변수의 이름을 명시하고 targetField의 조인될 키컬럼에 해당하는 참조변수의 Field이름을 명시한다.
 *          &#64;ReferenceKey(targetObject = "securedViewResources", targetField = "viewResourceId")
 *          &#64;ColumnDef(columnName = "VIEW_RESOURCE_ID")
 *          private String viewResourceId;
 *
 *          ...
 *
 *          private RoleSecuredViewResource securedViewResources; // 조인될 테이블의 참조
 *
 *          &#64;TableDef(tableName = "jcfiam_view_resources", alias = "v2")
 *          public static class RoleSecuredViewResource implements SecurityStatement {
 *              &#64;ColumnDef(columnName = "VIEW_RESOURCE_ID")
 *              private String viewResourceId;
 *
 *              ...
 *          }
 *
 *			...
 *      }
 *
 * <pre>
 * @see    jcf.query.core.evaluator.ObjectRelationMappingQueryEvaluator
 *
 * @author nolang
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface ReferenceKey {
	/**
	 * 조인대상 테이블의 레퍼런스 클래스의 객체 Field명
	 * @return
	 */
	String targetObject();

	/**
	 * 조인대상 레퍼런스 클래스 내부의 Field 명
	 * @return
	 */
	String targetField();
}
