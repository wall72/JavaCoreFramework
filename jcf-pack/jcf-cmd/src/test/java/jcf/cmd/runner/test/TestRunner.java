package jcf.cmd.runner.test;

import jcf.cmd.runner.CommandLineRunner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestRunner implements CommandLineRunner {

	private static final Logger logger = LoggerFactory
			.getLogger(TestRunner.class);
	
	public void run(String[] args) {
		for (int i = 0; i < 10; i++) {
			logger.debug("[{}] hello world!", i);
			try {
				Thread.sleep(100);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
