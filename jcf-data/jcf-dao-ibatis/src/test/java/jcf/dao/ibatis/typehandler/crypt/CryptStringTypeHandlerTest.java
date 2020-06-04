package jcf.dao.ibatis.typehandler.crypt;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;

import com.ibatis.sqlmap.client.SqlMapClient;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class CryptStringTypeHandlerTest {
	
	private static final Logger logger = LoggerFactory
			.getLogger(CryptStringTypeHandlerTest.class);

	private SqlMapClientTemplate sqlMapClient;
	
	@Autowired
	public void setSqlMapClient(SqlMapClient sqlMapClient) {
		this.sqlMapClient = new SqlMapClientTemplate(sqlMapClient);
	}
	
	private SimpleJdbcTemplate simpleJdbcTemplate;
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
	}
	
	@Before
	public void before() {
		SimpleJdbcTestUtils.executeSqlScript(simpleJdbcTemplate, 
				new ClassPathResource("jcf/dao/ibatis/typehandler/crypt/init.sql"), true);
	}

	
	@Test
	public void 패스워드암호화_후_평문조회_복호화조회_비교() {
		User user = new User();
		user.setId("1");
		user.setPassword("이거야");
		
		sqlMapClient.insert("insert", user);
		
		User userGetPlain = (User)sqlMapClient.queryForObject("select");
		
		dump(userGetPlain);
		
		User userDecrypted = (User)sqlMapClient.queryForObject("selectDecrypt");
		
		dump(userDecrypted);
		
	}

	private void dump(User user) {
		logger.info("user id : {} password : {}", user.getId(), user.getPassword());
	}
}
