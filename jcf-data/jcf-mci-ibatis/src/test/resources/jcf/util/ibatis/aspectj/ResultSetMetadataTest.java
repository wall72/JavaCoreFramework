package jcf.util.ibatis.aspectj;

import java.sql.SQLException;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.event.RowHandler;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ResultSetMetadataTest {
	
	@Autowired
	SqlMapClient sqlMapClient;
	
	@Test
	@Ignore
	public void test스트리밍() throws SQLException {
		sqlMapClient.queryWithRowHandler("selectAll", new RowHandler() {

			public void handleRow(Object valueObject) {
				System.out.println(valueObject);
			}
			
		});
	}
}
