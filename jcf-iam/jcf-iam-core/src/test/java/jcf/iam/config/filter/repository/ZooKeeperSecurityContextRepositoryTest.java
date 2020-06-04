package jcf.iam.config.filter.repository;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class ZooKeeperSecurityContextRepositoryTest {

	private String testString = "테스트 스트링..";
	private String testSubString = "테스트 Sub 스트링..";

	@Test
	public void testConnection() throws Exception {
		ZooKeeper zk = new ZooKeeper("127.0.0.1", 10 * 1000, null);

		if (zk.exists("/test01", false) == null) {
			zk.create("/test01", testString.getBytes("utf-8"), Ids.OPEN_ACL_UNSAFE,
					CreateMode.PERSISTENT);
		}
		if (zk.exists("/test01/sub01", false) == null) {
			zk.create("/test01/sub01", null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			zk.setData("/test01/sub01", testSubString.getBytes("utf-8"), -1);
		}

		byte[] testData = zk.getData("/test01", false, null);
		byte[] testSubData = zk.getData("/test01/sub01", false, null);

		assertThat(testString, is(new String(testData, "utf-8")));
		assertThat(testSubString, is(new String(testSubData, "utf-8")));

		zk.close();
	}
}
