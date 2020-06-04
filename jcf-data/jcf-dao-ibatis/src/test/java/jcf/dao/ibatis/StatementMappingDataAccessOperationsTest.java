package jcf.dao.ibatis;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import jcf.dao.StatementMappingDataAccessOperations;
import jcf.dao.ibatis.crud.ExecutionResult;
import jcf.dao.ibatis.crud.RowStatus;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.ibatis.sqlmap.client.SqlMapClient;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
public class StatementMappingDataAccessOperationsTest {

	private static final Logger logger = LoggerFactory.getLogger(StatementMappingDataAccessOperationsTest.class);

	@Autowired
	private StatementMappingDataAccessOperations dao;

	@Autowired
	private SqlMapClient sqlMap;

	
	private SimpleJdbcTemplate simpleJdbcTemplate;
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
	}
	
	@Before
	public void before() {
		SimpleJdbcTestUtils.executeSqlScript(simpleJdbcTemplate, 
				new ClassPathResource("jcf/dao/ibatis/init.sql"), true);
	}
		
	@SuppressWarnings({ "rawtypes", "unused" })
	@Test
	public void saveList() throws SQLException {
		try {
			sqlMap.getDataSource().getConnection().setAutoCommit(false);
			dao.saveList("*", makeMapList());
			List list = dao.selectList("select", 11);
			
		} catch (DataAccessException e) {
			sqlMap.getDataSource().getConnection().rollback();
			List list = dao.selectList("select", 11);
			Assert.assertTrue("list should be empty", list.size() == 0);
		}

	}

	@SuppressWarnings("rawtypes")
	@Test
	public void saveListWithoutRollback() {

//		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		List<ExecutionResult> executionResults = dao.saveListWithoutRollback("*", makeMapList());

		for (ExecutionResult er : executionResults) {
			logger.debug("{}, {}, {}", new Object[] { er.getOperation(), er.getAffectedRows(), er.getException() });
		}

		List list = dao.selectList("select", 11);
//		Map<String, Object> map = dao.selectMap("select", 11, "ID", "ID");

		Assert.assertEquals("중복 삽입이지만 하나는 남아있어야 함.", 1, list.size());
	}

	@SuppressWarnings({ "unused", "unchecked" })
	@Test
	public void selectMapTest() {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", 100);
		map.put("password", "홍길동");
		map.put("rowStatus", RowStatus.DEFAULT_ROW_STATUS_INSERT);

		list.add(map);

		dao.saveList("*", list);

		Map<String, Object> selectMap1 = dao.selectMap("select", 100, "ID");

		System.out.println(selectMap1.get(100));

		Map<String, Object> selectMap2 = dao.selectMap("select", 100, "ID", "ID");

		System.out.println(selectMap1.get(100));

	}

	@SuppressWarnings("rawtypes")
	private List makeMapList() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", 11);
		map.put("password", "홍길동");
		map.put("rowStatus", RowStatus.DEFAULT_ROW_STATUS_INSERT);

		list.add(map);
		list.add(map);

		return list;
	}

}
