-- 화면 - 권한 매핑
create table JCFIAM_VIEW_RESOURCES_ROLES
(
  VIEW_RESOURCE_ID    VARCHAR2(16) not null,
  ROLE_ID             VARCHAR2(16) not null,
  PERMISSION_ID       VARCHAR2(16) not null,
  CREATE_DATE         DATE,
  CREATE_USER_ID      VARCHAR2(16),
  LAST_MODIFY_DATE    DATE,
  LAST_MODIFY_USER_ID VARCHAR2(16)
);
-- Create/Recreate primary, unique and foreign key constraints
alter table JCFIAM_VIEW_RESOURCES_ROLES
  add constraint JCFIAM_VIEW_RESRC_ROLES_FK01 foreign key (VIEW_RESOURCE_ID)
  references JCFIAM_VIEW_RESOURCES (VIEW_RESOURCE_ID);
alter table JCFIAM_VIEW_RESOURCES_ROLES
  add constraint JCFIAM_VIEW_RESRC_ROLES_FK02 foreign key (PERMISSION_ID)
  references JCFIAM_PERMISSIONS (PERMISSION_ID);
alter table JCFIAM_JCFIAM_VIEW_RESOURCES_ROLES
  add constraint JCFIAM_VIEW_RESRC_ROLES_FK03 foreign key (ROLE_ID)
  references JCFIAM_ROLES (ROLE_ID);
  -- Create/Recreate indexes
create index JCFIAM_VIEW_RESRC_ROLES_INX01 on JCFIAM_VIEW_RESOURCES_ROLES (VIEW_RESOURCE_ID, ROLE_ID);