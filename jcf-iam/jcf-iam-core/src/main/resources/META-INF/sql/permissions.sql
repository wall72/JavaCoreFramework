-- 접근세부권한 정의
create table JCFIAM_PERMISSIONS
(
  PERMISSION_ID       VARCHAR2(16) not null,
  DESCRIPTION         VARCHAR2(128),
  MASK                NUMBER(8),
  CREATE_DATE         DATE,
  CREATE_USER_ID      VARCHAR2(16),
  LAST_MODIFY_DATE    DATE,
  LAST_MODIFY_USER_ID VARCHAR2(16)
);