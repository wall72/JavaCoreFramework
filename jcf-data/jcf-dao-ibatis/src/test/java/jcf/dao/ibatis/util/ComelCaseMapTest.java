package jcf.dao.ibatis.util;

import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ComelCaseMapTest {
	
	@Autowired
	private SampleDao sampleDao;

	private SimpleJdbcTemplate simpleJdbcTemplate;
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
	}
	
	@Before
	public void before() {
		SimpleJdbcTestUtils.executeSqlScript(simpleJdbcTemplate, 
				new ClassPathResource("jcf/dao/ibatis/util/init.sql"), true);
	}
		
	private static final int MAX_COUNT = 1;

	@Test
	public void testSampleDao() throws Exception {
		int count = 0;
		
		while (count++ < MAX_COUNT) {
			dump(sampleDao.findAll());
			
			
//			Thread.sleep(2000);
//			sampleDao.refresh();
		}
		
	}



	@SuppressWarnings("rawtypes")
	private void dump(List list) {
		System.out.println("dumping list");
		Iterator it = list.iterator();
		while (it.hasNext()) {
			System.out.println(it.next());
		}
	}
}
