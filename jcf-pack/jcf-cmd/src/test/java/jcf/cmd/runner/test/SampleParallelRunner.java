package jcf.cmd.runner.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jcf.cmd.progress.parallel.LogicContainer;
import jcf.cmd.progress.parallel.ParallelProgressSetter;
import jcf.cmd.progress.parallel.ProgressiveRunnable;
import jcf.cmd.runner.AbstractParallelJobRunner;

public class SampleParallelRunner extends AbstractParallelJobRunner {

	private static final Logger logger = LoggerFactory
			.getLogger(SampleParallelRunner.class);
	
	@Override
	protected void setUpContainer(LogicContainer logicContainer) {

		logicContainer.add(new ProgressiveRunnable() {
			public void run(ParallelProgressSetter progress) {
				boilerPlateCode(progress, "job 1", 10);
			}
		});
		
		logicContainer.add(new ProgressiveRunnable() {
			public void run(ParallelProgressSetter progress) {
				boilerPlateCode(progress, "job 2", 9);
			}
		});
		
		logicContainer.add(new ProgressiveRunnable() {
			public void run(ParallelProgressSetter progress) {
				boilerPlateCode(progress, "job 3", 8);
			}
		});
		
	}

	private void boilerPlateCode(ParallelProgressSetter progress,
			String jobName, int total) {
		logger.debug("starting {}", jobName);
		progress.setTotal(total);
		
		for (int i = 0; i < total; i++) {
			try {
				logger.debug("[{}] SampleParallelRunner - {}", jobName);
				Thread.sleep(10);
				
			} catch (InterruptedException e) {
				progress.setMessage("interrupted");
				logger.debug("interrupted {}", jobName);
				break;
			}
			progress.setCurrent(i);
		}
		
		progress.setMessage("finished");
		logger.debug("finished {}", jobName);
	}

}
