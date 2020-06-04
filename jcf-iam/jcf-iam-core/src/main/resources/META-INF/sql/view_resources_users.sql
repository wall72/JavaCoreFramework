-- 화면 - 사용자 매핑
create table JCFIAM_VIEW_RESOURCES_USERS
(
  VIEW_RESOURCE_ID    VARCHAR2(16) not null,
  USER_ID             VARCHAR2(16) not null,
  PERMISSION_ID       VARCHAR2(16) not null,
  CREATE_DATE         DATE,
  CREATE_USER_ID      VARCHAR2(16),
  LAST_MODIFY_DATE    DATE,
  LAST_MODIFY_USER_ID VARCHAR2(16)
);
-- Create/Recreate primary, unique and foreign key constraints
alter table JCFIAM_VIEW_RESOURCES_USERS
  add constraint JCFIAM_VIEW_RESRC_USERS_FK01 foreign key (VIEW_RESOURCE_ID)
  references JCFIAM_VIEW_RESOURCES (VIEW_RESOURCE_ID);
alter table JCFIAM_VIEW_RESOURCES_USERS
  add constraint JCFIAM_VIEW_RESRC_USERS_FK02 foreign key (PERMISSION_ID)
  references JCFIAM_PERMISSIONS (PERMISSION_ID);
alter table JCFIAM_VIEW_RESOURCES_USERS
  add constraint JCFIAM_VIEW_RESRC_USERS_FK03 foreign key (USER_ID)
  references JCFIAM_USERS (USER_ID);
-- Create/Recreate indexes
create index JCFIAM_VIEW_RESRC_USERS_INX01 on JCFIAM_VIEW_RESOURCES_USERS (VIEW_RESOURCE_ID, USER_ID);