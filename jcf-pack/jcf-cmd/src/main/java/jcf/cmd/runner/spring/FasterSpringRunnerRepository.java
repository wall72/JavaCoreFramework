package jcf.cmd.runner.spring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jcf.cmd.ExecutorException;
import jcf.cmd.runner.CommandLineRunner;
import jcf.cmd.runner.CommandLineRunnerRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

public class FasterSpringRunnerRepository implements CommandLineRunnerRepository {

	private static final Logger logger = LoggerFactory
			.getLogger(FasterSpringRunnerRepository.class);
	
	private final String basePackage;
	private final String contextPath;
	private ConfigurableApplicationContext cac;

	/**
	 * CommandLineRunner가 위치할 베이스 패키지명 (마침표 (.)로 구분)
	 * @param basePackage
	 */
	public FasterSpringRunnerRepository(String basePackage, String contextPath) {
		if (basePackage.endsWith(".")) {
			this.basePackage = basePackage;
			
		} else {
			this.basePackage = basePackage + ".";
		}
		this.contextPath = contextPath;
	}
	
	/* (non-Javadoc)
	 * @see jcf.cmd.runner.CommandLineRunnerRepository#getRunner(java.lang.String)
	 */
	public CommandLineRunner getRunner(String runnerName) {
		Class<?> runnerClass;
		try {
			runnerClass = ClassUtils.forName(basePackage + runnerName, null);

		} catch (ClassNotFoundException e) {
			throw new ExecutorException("error creating runner", e);

		} catch (LinkageError e) {
			throw new ExecutorException("error creating runner", e);
		}

		if (! ClassUtils.isAssignable(CommandLineRunner.class, runnerClass)) {
			throw new ExecutorException("given runner is not of type CommandLineRunner");
		}
		Object runnerObject = getApplicationContext().getAutowireCapableBeanFactory().autowire(runnerClass, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
		
		return (CommandLineRunner) runnerObject;
	}

	private ConfigurableApplicationContext getApplicationContext() {
		if (cac == null) {
			cac = new FileSystemXmlApplicationContext(contextPath);
		}
		return cac;
	}

	
	
	private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

	private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);

	private String resourcePattern = "**/*Runner.class";

	
	
	public String[] getRunners() {
		try {
			return scan().toArray(new String[]{});
			
		} catch (IOException e) {
			throw  new ExecutorException("error scaning", e);
		}
	}

	private List<String> scan() throws IOException {
		List<String> list = new ArrayList<String>();
		
		String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
				resolveBasePackage(basePackage) + this.resourcePattern;
		Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);

		for (Resource resource : resources) {
			if (resource.isReadable()) {
				try {
					MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
					
					ClassMetadata classMetadata = metadataReader.getClassMetadata();
					if (classMetadata.isIndependent() && classMetadata.isConcrete()) {
						String[] interfaceNames = classMetadata.getInterfaceNames();
						
						boolean found = false;
						for (String interfaceName : interfaceNames) {
							if (interfaceName.equals("jcf.cmd.runner.CommandLineRunner")) {
									found = true;
									break;
							}
						}
						if (found ||  isAssignable(classMetadata.getSuperClassName())) {
							list.add(stripBasePackage(classMetadata.getClassName()));
						}
					}
					
				}
				catch (Throwable ex) {
					throw new ExecutorException(
							"Failed to read candidate component class: " + resource, ex);
				}
			}
		}
		return list;
	}

	private Set<String> assignableClasses = new HashSet<String>();
	
	private boolean isAssignable(String superClassName) {
		if (assignableClasses.contains(superClassName)) {
			logger.info("returning cached {}", superClassName);
			return true;
		}

		try {
			Class<?> clazz = Class.forName(superClassName);
			if (CommandLineRunner.class.isAssignableFrom(clazz)) {
				assignableClasses.add(superClassName);
				return true;
			}
				
		} catch (ClassNotFoundException e) {
			logger.warn("error instantiating super class " + superClassName, e);
		}
		
		return false;
	}

	private String stripBasePackage(String className) {
		return className.substring((basePackage).length());
	}

	protected String resolveBasePackage(String basePackage) {
		return ClassUtils.convertClassNameToResourcePath(basePackage);
		
	}
	
	public void close() {
		if (cac != null) {
			cac.close();
		}
	}
}
