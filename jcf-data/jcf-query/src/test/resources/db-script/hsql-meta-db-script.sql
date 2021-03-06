DROP SEQUENCE SEQ_SERVICE_META_KEY IF EXISTS;
CREATE SEQUENCE SEQ_SERVICE_META_KEY start with 100 increment by 1;

DROP TABLE SERVICE_DOMAIN IF EXISTS;

CREATE TABLE SERVICE_DOMAIN (
	DOMAIN_NAME varchar(30),
	CONSTRAINT SERVICE_DOMAIN_KEY PRIMARY KEY (DOMAIN_NAME)
);

DROP TABLE SERVICE_APPLICATION IF EXISTS;

CREATE TABLE SERVICE_APPLICATION (
	APPLICATION_NAME varchar(50),
	DOMAIN_NAME varchar(30) NOT NULL,
	CONSTRAINT SERVICE_APPLICATION_KEY PRIMARY KEY (APPLICATION_NAME)
);

DROP TABLE SERVICE_RESOURCE_CANDIDATE IF EXISTS;

CREATE TABLE SERVICE_RESOURCE_CANDIDATE (
	CANDIDATE_RESOURCE_ID INTEGER,
	RESOURCE_CLASS_NAME varchar(200) NOT NULL,
	LAST_MODIFIED_USER_NAME varchar(400) NOT NULL,
	LAST_MODIFIED_DATE DATE NOT NULL,
	DOMAIN_NAME varchar(30) NOT NULL,
	APPLICATION_NAME varchar(50) NOT NULL,
	CONSTRAINT SERVICE_RESOURCE_CANDIDATE_ID PRIMARY KEY (CANDIDATE_RESOURCE_ID)
);

DROP TABLE SERVICE_RESOURCE_SERVICE IF EXISTS;

CREATE TABLE SERVICE_RESOURCE_SERVICE (
	SERVICE_ID INTEGER,
	RESOURCE_CLASS_NAME varchar(200) NOT NULL,
	LAST_MODIFIED_USER_NAME varchar(400) NOT NULL,
	LAST_MODIFIED_DATE DATE NOT NULL,
	SERVICE_PATH varchar(200) NOT NULL,
	WRAPPER_SUFFIX varchar(50) NOT NULL,
	MANAGE_STATUS varchar(10) NOT NULL,
	DOMAIN_NAME varchar(30) NOT NULL,
	APPLICATION_NAME varchar(50) NOT NULL,
	CANDIDATE_RESOURCE_ID INTEGER NOT NULL,
	CONSTRAINT SERVICE_RESOURCE_SERVICE_ID PRIMARY KEY (SERVICE_ID)
);

DROP TABLE SERVICE_TEMPLATE_VARIABLE IF EXISTS;

CREATE TABLE SERVICE_TEMPLATE_VARIABLE (
	VARIABLE_NAME varchar(20) NOT NULL,
	VARIABLE_TYPE varchar(20) NOT NULL,
	SERVICE_ID INTEGER
);

DROP TABLE SERVICE_RESOURCE_PARAMETER IF EXISTS;

CREATE TABLE SERVICE_RESOURCE_PARAMETER (
	PARAMETER_NAME varchar(20) NOT NULL,
	PARAMETER_TYPE varchar(20) NOT NULL,
	SERVICE_ID INTEGER
);

DROP TABLE SERVICE_RESOURCE IF EXISTS;

CREATE TABLE SERVICE_RESOURCE (
	RESOURCE_ID INTEGER,
	RESOURCE_PATH varchar(100) NOT NULL,
	HTTP_METHOD varchar(20) NOT NULL,
	JAVA_METHOD varchar(50) NOT NULL,
	SERVICE_ID INTEGER NOT NULL,
	CONSTRAINT SERVICE_RESOURCE_KEY PRIMARY KEY (RESOURCE_ID)
);



insert into SERVICE_DOMAIN(DOMAIN_NAME) VALUES('hiway');
insert into SERVICE_DOMAIN(DOMAIN_NAME) VALUES('hipro');

insert into SERVICE_APPLICATION(APPLICATION_NAME, DOMAIN_NAME) VALUES('hiway.woa', 'hiway');
insert into SERVICE_APPLICATION(APPLICATION_NAME, DOMAIN_NAME) VALUES('hiway.woa.tutorial', 'hiway');
insert into SERVICE_APPLICATION(APPLICATION_NAME, DOMAIN_NAME) VALUES('hiway.woa.mobile', 'hiway');