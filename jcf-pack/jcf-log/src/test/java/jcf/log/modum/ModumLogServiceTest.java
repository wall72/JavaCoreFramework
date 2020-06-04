package jcf.log.modum;

import static org.junit.Assert.*;
import jcf.log.SampleBatchWriter;
import jcf.log.SampleLogEvent;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;


public class ModumLogServiceTest {
	
	ModumLogService<SampleLogEvent> mls;
	SampleBatchWriter<SampleLogEvent> bw;
	
	@Rule
	public ExternalResource er = new ExternalResource() {
		@Override
		protected void before() throws Throwable {
			super.before();
			
			bw = new SampleBatchWriter<SampleLogEvent>();

			mls = new ModumLogService<SampleLogEvent>();
			mls.setLatency(10);
			mls.setThresold(10);
			mls.setBatchWriter(bw );
		}
		@Override
		protected void after() {
			mls = null;
			mls = null;
			
			super.after();
		}
	};
	@Test
	public void 로그_한_개()  {
		mls.start();
		mls.sendEvent(new SampleLogEvent("first event"));
		mls.stop();

		assertEquals(1, bw.pop().or(0).intValue());
	}
	
	@Test
	public void 로그_15개() {
		mls.start();
		for (int i = 0; i < 15; i++) {
			mls.sendEvent(new SampleLogEvent("test"));
		}
		mls.stop();

		assertEquals(10, bw.pop().or(0).intValue());
		assertEquals(5, bw.pop().or(0).intValue());
	}
	
	@Test
	public void 간헐적_로그() throws InterruptedException {
		mls.start();
		mls.sendEvent(new SampleLogEvent("test"));
		Thread.sleep(20);
		
		mls.sendEvent(new SampleLogEvent("test"));
		mls.stop();

		assertEquals(1, bw.pop().or(0).intValue());
		assertEquals(1, bw.pop().or(0).intValue());
	}
}
