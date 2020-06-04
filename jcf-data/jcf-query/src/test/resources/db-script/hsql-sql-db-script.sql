DROP TABLE JCF_SQL IF EXISTS;

CREATE TABLE JCF_SQL(
	QUERY_ID VARCHAR(20),
	QUERY VARCHAR(512),
	PRIMARY KEY(QUERY_ID)
)

INSERT INTO JCF_SQL VALUES ('SELECT', 'SELECT QUERY FROM JCF_SQL WHERE QUERY_ID = :queryid');

--INSERT INTO PRODUCT_TYPE VALUES('SHT', '���� Ÿ��', '���� ��');

--INSERT INTO PRODUCT VALUES(1001, 'SHT', '�칰��', '� ������ ����ź ��; �������� �ʰ� �״�� ��â�� �ư� ����ϴ� ȭ����', sysdate);
--INSERT INTO PRODUCT VALUES(1002, 'SHT', '/v��', '��/���/����С����ֿ�ס�ȭ���ǰ �� ��ȭ��/����(LPG)����ȭõ������(LNG) �� ��üȭ��; ��⿡ ���� ��: ������ ���·� �����Ͽ� �뷮����ϴ� ����', sysdate);
--INSERT INTO PRODUCT VALUES(1003, 'SHT', '�����̳ʼ�', '�����̳ʸ� ����ϴ� ����', sysdate);
--INSERT INTO PRODUCT VALUES(1004, 'SHT', 'FPSO��', 'Floating Production Storage Offloading, ��/�� ��/������� ����', sysdate);

