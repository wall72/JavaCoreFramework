DROP TABLE JCFIAM_USERS IF EXISTS;

CREATE TABLE JCFIAM_USERS
(
  USER_ID             VARCHAR(64) not null,
  USER_NAME           VARCHAR(32),
  PASSWORD            VARCHAR(16),
  ENABLED             VARCHAR(1),
  CREATE_DATE         TIMESTAMP,
  CREATE_USER_ID      VARCHAR(16),
  LAST_MODIFY_DATE    TIMESTAMP,
  LAST_MODIFY_USER_ID VARCHAR(16),
  PRIMARY KEY(USER_ID)
);


INSERT INTO JCFIAM_USERS VALUES('nolang', 'nolang', 'nolang', 'Y', sysdate, 'nolang', sysdate, 'nolang');

DROP TABLE JCFIAM_ROLES IF EXISTS;

CREATE TABLE JCFIAM_ROLES
(
  ROLE_ID             VARCHAR(16) not null,
  ROLE_NAME           VARCHAR(64) not null,
  DESCRIPTION         VARCHAR(512),
  ENABLED             VARCHAR(1),
  PARENT_ROLE_ID      VARCHAR(16),
  CREATE_DATE         TIMESTAMP,
  CREATE_USER_ID      VARCHAR(16),
  LAST_MODIFY_DATE    TIMESTAMP,
  LAST_MODIFY_USER_ID VARCHAR(16),
  PRIMARY KEY(ROLE_ID)
);

INSERT INTO JCFIAM_ROLES VALUES('ROLE_USER', '�Ϲݻ����', '�Ϲݻ����', 'Y', '',  sysdate, 'nolang', sysdate, 'nolang');
INSERT INTO JCFIAM_ROLES VALUES('ROLE_ADMIN', '������', '������', 'Y', '',  sysdate, 'nolang', sysdate, 'nolang');

DROP TABLE JCFIAM_AUTHORITIES IF EXISTS;

CREATE TABLE JCFIAM_AUTHORITIES
(
  ROLE_ID             VARCHAR(16) not null,
  USER_ID             VARCHAR(64) not null,
  CREATE_DATE         TIMESTAMP,
  CREATE_USER_ID      VARCHAR(16),
  LAST_MODIFY_DATE    TIMESTAMP,
  LAST_MODIFY_USER_ID VARCHAR(16),
  PRIMARY KEY(ROLE_ID, USER_ID)
);

INSERT INTO JCFIAM_AUTHORITIES VALUES('ROLE_USER', 'nolang', sysdate, 'nolang', sysdate, 'nolang');
INSERT INTO JCFIAM_AUTHORITIES VALUES('ROLE_ADMIN', 'nolang', sysdate, 'nolang', sysdate, 'nolang');

DROP TABLE JCFIAM_PERMISSIONS IF EXISTS;

CREATE TABLE JCFIAM_PERMISSIONS
(
  PERMISSION_ID       VARCHAR(16) not null,
  DESCRIPTION         VARCHAR(128),
  MASK                INTEGER,
  CREATE_DATE         TIMESTAMP,
  CREATE_USER_ID      VARCHAR(16),
  LAST_MODIFY_DATE    TIMESTAMP,
  LAST_MODIFY_USER_ID VARCHAR(16)
);

DROP TABLE JCFIAM_VIEW_RESOURCES IF EXISTS;

CREATE TABLE JCFIAM_VIEW_RESOURCES
(
  VIEW_RESOURCE_ID    VARCHAR(16) not null,
  PARENT_VIEW_ID    VARCHAR(16),
  VIEW_RESOURCE_NAME  VARCHAR(64) not null,
  VIEW_RESOURCE_SEQ   VARCHAR(3),
  DESCRIPTION         VARCHAR(512),
  VIEW_RESOURCE_URL   VARCHAR(128),
  VIEW_RESOURCE_PATTERN   VARCHAR(128),
  CREATE_DATE         TIMESTAMP,
  CREATE_USER_ID      VARCHAR(16),
  LAST_MODIFY_DATE    TIMESTAMP,
  LAST_MODIFY_USER_ID VARCHAR(16),
  PRIMARY KEY(VIEW_RESOURCE_ID)
);

INSERT INTO JCFIAM_VIEW_RESOURCES VALUES('AAA009', '', '�űԿ�û��ֵ��', '001', '�űԿ�û��ֵ��', '', '', sysdate, 'nolang', sysdate, 'nolang');

DROP TABLE JCFIAM_VIEW_RESOURCES_USERS IF EXISTS;

CREATE TABLE JCFIAM_VIEW_RESOURCES_USERS
(
  VIEW_RESOURCE_ID    VARCHAR(16) not null,
  USER_ID             VARCHAR(16) not null,
  PERMISSION_ID       VARCHAR(16) not null,
  CREATE_DATE         TIMESTAMP,
  CREATE_USER_ID      VARCHAR(16),
  LAST_MODIFY_DATE    TIMESTAMP,
  LAST_MODIFY_USER_ID VARCHAR(16)
);

DROP TABLE JCFIAM_VIEW_RESOURCES_ROLES IF EXISTS;

CREATE TABLE JCFIAM_VIEW_RESOURCES_ROLES
(
  VIEW_RESOURCE_ID    VARCHAR(16) not null,
  ROLE_ID             VARCHAR(16) not null,
  PERMISSION_ID       VARCHAR(16) not null,
  CREATE_DATE         TIMESTAMP,
  CREATE_USER_ID      VARCHAR(16),
  LAST_MODIFY_DATE    TIMESTAMP,
  LAST_MODIFY_USER_ID VARCHAR(16)
)

