package jcf.cmd.runner.spring;

import jcf.cmd.ExecutorException;
import jcf.cmd.issue.ChainedIssueHandler;
import jcf.cmd.issue.IssueHandler;
import jcf.cmd.issue.IssueHandlerFactory;
import jcf.cmd.runner.CommandLineRunner;
import jcf.cmd.runner.CommandLineRunnerRepository;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class SpringRunnerRepository implements CommandLineRunnerRepository, IssueHandlerFactory {


	private String contextPath;
	private ConfigurableApplicationContext applicationContext;
	

	public SpringRunnerRepository(String contextPath) {
		this.contextPath = contextPath;
	}

	/* (non-Javadoc)
	 * @see jcf.cmd.runner.CommandLineRunnerRepository#getRunner(java.lang.String)
	 */
	public CommandLineRunner getRunner(String runnerName) {
		CommandLineRunner runner;
		try {
			runner = getApplicationContext().getBean(runnerName, CommandLineRunner.class);
			
		} catch (NoSuchBeanDefinitionException e) {
			throw new ExecutorException("runnerName '" + runnerName + "' not found.", e);
		}
		return runner;
	}

	private ApplicationContext getApplicationContext() {
		if (applicationContext == null) {
			applicationContext = new FileSystemXmlApplicationContext(contextPath);
		}
		return applicationContext;
	}

	/* (non-Javadoc)
	 * @see jcf.cmd.runner.CommandLineRunnerRepository#getRunners()
	 */
	public String[] getRunners() {
		return getApplicationContext().getBeanNamesForType(CommandLineRunner.class);
	}

	public IssueHandler getIssueHandler() {
		ChainedIssueHandler chainedIssueHandler = new ChainedIssueHandler();
		for (IssueHandler issueHandler : getApplicationContext().getBeansOfType(IssueHandler.class).values()) {
			chainedIssueHandler.add(issueHandler);
		}
		return chainedIssueHandler;
	}

	public void close() {
		applicationContext.close();
	}

}
