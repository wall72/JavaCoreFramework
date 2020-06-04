-- 사용자 - 권한 매핑
create table JCFIAM_AUTHORITIES
(
  ROLE_ID             VARCHAR2(16) not null,
  USER_ID             VARCHAR2(64) not null,
  CREATE_DATE         DATE,
  CREATE_USER_ID      VARCHAR2(16),
  LAST_MODIFY_DATE    DATE,
  LAST_MODIFY_USER_ID VARCHAR2(16)
);
-- Create/Recreate primary, unique and foreign key constraints
alter table JCFIAM_AUTHORITIES
  add constraint JCFIAM_AUTHORITIES_PK primary key (ROLE_ID, USER_ID);

alter table JCFIAM_AUTHORITIES
  add constraint JCFIAM_AUTHORITIES_FK01 foreign key (ROLE_ID)
  references JCFIAM_ROLES (ROLE_ID);
alter table JCFIAM_AUTHORITIES
  add constraint JCFIAM_AUTHORITIES_FK02 foreign key (USER_ID)
  references JCFIAM_USERS (USER_ID);