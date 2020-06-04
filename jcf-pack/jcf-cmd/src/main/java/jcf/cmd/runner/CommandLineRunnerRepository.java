package jcf.cmd.runner;

public interface CommandLineRunnerRepository {

	CommandLineRunner getRunner(String runnerName);

	String[] getRunners();

}