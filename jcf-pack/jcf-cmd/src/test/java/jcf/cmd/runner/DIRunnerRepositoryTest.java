package jcf.cmd.runner;

import jcf.cmd.AbstractTestParent;
import jcf.cmd.runner.spring.FasterSpringRunnerRepository;

import org.junit.Test;

public class DIRunnerRepositoryTest extends AbstractTestParent {

	FasterSpringRunnerRepository repository = new FasterSpringRunnerRepository("jcf.cmd.runner", "classpath:jcf/cmd/runner/test.xml");

	@Test
	public void list_runners() {
		
//		CommandLineRunner runner = repository.getRunner("SampleRunner");
//		runner.run(null);
		
		String[] runners = repository.getRunners();
		for (String runnerName : runners) {
			System.out.println(runnerName);
		}
	}
	
	@Test
	public void run_sample_runner() {
		repository.getRunner("test.SampleParallelRunner").run(null);
//		repository.getRunner("SampleRunner").run(null);
	}
}
