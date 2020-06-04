package jcf.cmd.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampleRunner implements CommandLineRunner {

	private static final Logger logger = LoggerFactory
			.getLogger(SampleRunner.class);
	
	public void run(String[] args) {
		for (int i = 0; i < 10; i++) {
			logger.debug("[{}] SampleRunner!", i);
			try {
				Thread.sleep(50);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
