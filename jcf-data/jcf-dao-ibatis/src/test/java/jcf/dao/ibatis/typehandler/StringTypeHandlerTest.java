package jcf.dao.ibatis.typehandler;

import java.util.List;

import javax.sql.DataSource;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
@TransactionConfiguration(transactionManager="txManager")
public class StringTypeHandlerTest {
	
	@Autowired
	private TypeHandlerDao typeHandlerDao;

	
	private SimpleJdbcTemplate simpleJdbcTemplate;
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
	}
	
	@Before
	public void before() {
		SimpleJdbcTestUtils.executeSqlScript(simpleJdbcTemplate, 
				new ClassPathResource("jcf/dao/ibatis/typehandler/init.sql"), true);
	}
	
	@Test
	public void testInsertMember() {
		Type type = new Type(1, "JCF", "제이씨에프", "서울시 종로구 관철동", "서울");
//		type = new Type(1, "JCF", null, null, null);
		
		assertEquals(1, typeHandlerDao.insertMember(type));

	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void testFindData() {
		List memList = typeHandlerDao.findMembers();
		for(int i=0; i<memList.size(); i++) {
			Type type = (Type)memList.get(i);
			
			System.out.println(" ******" + type);
		}
	}
}
