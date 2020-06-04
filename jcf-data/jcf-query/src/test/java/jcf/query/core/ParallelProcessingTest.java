package jcf.query.core;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jcf.query.domain.Product;
import jcf.query.web.CommonVariableHolder;
import jcf.task.MultiTaskExecutor;
import jcf.task.TaskWorker;
import jcf.task.TaskWorkerFactory;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import statement.SampleStatement;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-namespace.xml" })
public class ParallelProcessingTest {

	@Autowired
	private QueryExecutor queryExecutor;

	@Autowired
	private MultiTaskExecutor executor;

	private TestService service;

	private int loopCount = 100;

	@Before
	public void setup() {
		service = new TestService(queryExecutor);
	}

	@Test
	public void testSelect() {
		CommonVariableHolder.addVariable("TEST_VALUE", "TEST");

		Collection<TaskWorker<TaskInfo>> tasks = new ArrayList<TaskWorker<TaskInfo>>();

		for (int i = 0; i < loopCount; ++i) {
			tasks.add(TaskWorkerFactory.getTaskWorker(service, "selectProduct", new Class[]{String.class, int.class}, new Object[]{"ID" + i, 1004}, TaskInfo.class));
		}

		List<TaskInfo> productList = executor.execute(tasks, TaskInfo.class);

		for(TaskInfo taskInfo : productList){
			Product actual = taskInfo.result;

			assertNotNull(actual);
			assertThat(1004, is(actual.getProductId()));
			assertThat("CARRIERS", is(actual.getProductTypeId()));
			assertThat("FPSO선", is(actual.getProductName()));
			assertThat("Floating Production Storage Offloading, 부유식 원유생산저장 설비", is(actual.getProductDescription()));

			System.out.println(String.format("TASKID(%s, %s) 실행순서(%d) 수행시간(%d) 조회결과 - %s", taskInfo.taskId, CommonVariableHolder.get("TEST_VALUE"), taskInfo.taskSequence, taskInfo.executeTime, actual.toString().replaceAll("[\n\t]", " ")));
		}
	}

	@Test
	@Ignore
	public void testInsert() {
		Collection<TaskWorker<TaskInfo>> tasks = new ArrayList<TaskWorker<TaskInfo>>();

		int testId = 10000;

		for (int i = 0; i < loopCount; ++i) {
			tasks.add(TaskWorkerFactory.getTaskWorker(service, "insertProduct", new Class[]{String.class, int.class}, new Object[]{"ID" + i, testId + i}, TaskInfo.class));
		}

		List<TaskInfo> productList = executor.execute(tasks, TaskInfo.class);

		for(TaskInfo taskInfo : productList){
			Product actual = taskInfo.result;

			assertNotNull(actual);
			assertThat(testId++, is(actual.getProductId()));
			assertThat("CARRIERS", is(actual.getProductTypeId()));
			assertThat("배", is(actual.getProductName()));
			assertThat("그냥 배", is(actual.getProductDescription()));

			System.out.println(String.format("TASKID(%s) 실행순서(%d) 수행시간(%d) 조회결과 - %s", taskInfo.taskId, taskInfo.taskSequence, taskInfo.executeTime, actual.toString().replaceAll("[\n\t]", " ")));
		}
	}


	public static class TestService {

		private static int taskSequence = 1;

		private QueryExecutor queryExecutor;

		private Object lock = new Object();

		public TestService(QueryExecutor queryExecutor) {
			this.queryExecutor = queryExecutor;
		}

		public TaskInfo selectProduct(String taskId, int productId) {
			long startTime = System.currentTimeMillis();

			Product product = new Product();
			product.setProductId(productId);

			synchronized (lock) {
				if(taskSequence++ == 1){
					CommonVariableHolder.clear();
				}
			}

			System.out.println(Thread.currentThread().getName() + " - " + CommonVariableHolder.get("TEST_VALUE"));

			return new TaskInfo(taskId, taskSequence, queryExecutor.queryForObject(SampleStatement.selectProductsWithIfStatment, product, Product.class), System.currentTimeMillis() - startTime);
		}

		public TaskInfo insertProduct(String taskId, int productId)	{
			long startTime = System.currentTimeMillis();

			Product insert = new Product();

			insert.setProductId(productId);
			insert.setProductTypeId("CARRIERS");
			insert.setProductName("배");
			insert.setProductDescription("그냥 배");

			queryExecutor.update(SampleStatement.insertProduct, insert);

			return new TaskInfo(taskId, taskSequence++, insert, System.currentTimeMillis() - startTime);
		}
	}

	public static class TaskInfo	{
		private String taskId;
		private int taskSequence;
		private Product result;
		private long executeTime;

		public TaskInfo(String taskId, int taskSequence, Product result, long executeTime) {
			this.taskId = taskId;
			this.taskSequence = taskSequence;
			this.result = result;
		}
	}
}
