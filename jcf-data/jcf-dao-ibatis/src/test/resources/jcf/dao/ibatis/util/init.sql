DROP TABLE MEMBER;

CREATE TABLE MEMBER (
	USER_ID INTEGER,
	USER_NAME VARCHAR(50),
	NICK_NAME VARCHAR(50),
	ADDRESS VARCHAR(200),
	HOME_TOWN VARCHAR(200)
);

INSERT INTO MEMBER (USER_ID, USER_NAME, NICK_NAME, ADDRESS, HOME_TOWN) VALUES (1, 'NAME1', 'NICK1', 'ADDR1', 'HOME1');