-- 권한정의
create table JCFIAM_ROLES
(
  ROLE_ID             VARCHAR2(16) not null,
  ROLE_NAME           VARCHAR2(64) not null,
  DESCRIPTION         VARCHAR2(512),
  ENABLED             VARCHAR2(1),
  PARENT_ROLE_ID      VARCHAR2(16),
  CREATE_DATE         DATE,
  CREATE_USER_ID      VARCHAR2(16),
  LAST_MODIFY_DATE    DATE,
  LAST_MODIFY_USER_ID VARCHAR2(16)
);
-- Create/Recreate primary, unique and foreign key constraints
alter table JCFIAM_ROLES
  add constraint JCFIAM_ROLES_PK primary key (ROLE_ID);