-- 사용자관리
create table JCFIAM_USERS
(
  USER_ID             VARCHAR2(64) not null,
  USER_NAME           VARCHAR2(32),
  PASSWORD            VARCHAR2(16),
  ENABLED             VARCHAR2(1),
  CREATE_DATE         DATE,
  CREATE_USER_ID      VARCHAR2(16),
  LAST_MODIFY_DATE    DATE,
  LAST_MODIFY_USER_ID VARCHAR2(16)
);
-- Create/Recreate primary, unique and foreign key constraints
alter table JCFIAM_USERS
  add constraint JCFIAM_USERS_PK primary key (USER_ID);