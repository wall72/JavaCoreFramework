package jcf.extproc.console;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import jcf.extproc.ExternalProcessOperator;
import jcf.extproc.config.ExtProcConstant;
import jcf.extproc.fileaccess.FileAccess;
import jcf.extproc.fileaccess.FileAccessImpl;
import jcf.extproc.process.JobInstanceInfo;
import jcf.extproc.spring.ExternalProcessOperatorFactory;
import jline.ArgumentCompletor;
import jline.Completor;
import jline.ConsoleReader;
import jline.MultiCompletor;
import jline.NullCompletor;
import jline.SimpleCompletor;

import org.apache.commons.io.IOUtils;

public class Main {
	
	private static final String PROMPT = "batch> ";
	private static final String ENV_DIR = "extproc.work.dir";

	
	public static void main(String[] args) throws Exception {
		
		ExternalProcessOperatorFactory factory = new ExternalProcessOperatorFactory();
		
		FileAccess fileAccess;
		try {
			String envdir = System.getProperty(ENV_DIR);
			if (envdir != null) {
				factory.setBaseDirectory(envdir);
				fileAccess = new FileAccessImpl(new File(envdir));
				
			} else {
				fileAccess = new FileAccessImpl(new File(System.getProperty("java.io.tmpdir")));
			}
			
			factory.afterPropertiesSet();
			
			ExternalProcessOperator operator = factory.getObject();
		
			new Main().cmdHandler(operator, fileAccess);

		} catch(Exception e) {
			System.out.println("TERMINATING DUE TO EXCEPTION..");
			e.printStackTrace();
			
		} finally {
			factory.destroy();
		}
	}
	
	public void cmdHandler(ExternalProcessOperator operator, FileAccess artifactAccess) throws Exception {

		ConsoleReader consoleReader = new ConsoleReader();
		Completor jobNameCompletor = new SimpleCompletor(operator.getJobNames().toArray(new String[0]));
		
		consoleReader.addCompletor(new MultiCompletor(
				new Completor[] {
						new SimpleCompletor(new String[]{"exit", "status"}),
						new ArgumentCompletor(
								new Completor[] {
										new SimpleCompletor(new String[]{"log", "delete", "view"}),
										jobNameCompletor ,
										new NullCompletor()
								}
						)
				}
				));
				
		String line;
		for (line = consoleReader.readLine(PROMPT); line != null; line = consoleReader.readLine(PROMPT)) {
			Scanner scanner = new Scanner(line);
	
			try {
				
				if (! scanner.hasNext()) {
					doStatus(operator);
					continue;
				}
				
				String op = scanner.next();
				
				if (op.equals("exit")) {
					break;
					
				} else if (op.equals("view")) {
					if (scanner.hasNext()) {
						String jobName = scanner.next();
						doView(operator, jobName);
						
					} else {
						doStatus(operator);
					}
					
				} else if (op.equals("log")) {
					if (scanner.hasNext()) {
						String jobName = scanner.next();
						JobInstanceInfo instanceInfo = operator.getLastInstanceInfo(jobName);
						doLog(instanceInfo, artifactAccess);
						
					} else {
						doStatus(operator);
					}
					
				} else if (op.equals("delete")) {
					if (scanner.hasNext()) {
						String jobName = scanner.next();
						doDelete(operator, jobName);
						
					} else {
						doStatus(operator);
					}
					
				} else if (op.equals("status")) {
					doStatus(operator);
				}
					
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}

	private void doView(ExternalProcessOperator operator, String jobName) {
		System.out.println(operator.getJob(jobName));
	}

	private void doDelete(ExternalProcessOperator operator, String jobName) {
		operator.delete(jobName);
	}

	private void doLog(JobInstanceInfo instanceInfo, FileAccess fileAccess) {
		OutputStream os = System.out;
		File file = fileAccess.get(instanceInfo, ExtProcConstant.LOGFILE_NAME);
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			IOUtils.copyLarge(inputStream, os);
		
		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			try {
				inputStream.close();

			} catch (IOException e) {
				;
			}
		}
	}

	private void doStatus(ExternalProcessOperator operator) {
		for (String jobName : operator.getJobNames()) {
			try {
				JobInstanceInfo jobInstanceInfo = operator.getLastInstanceInfo(jobName);
				boolean isRunning = operator.isRunning(jobName);
				
				if (jobInstanceInfo != null) {
					long jobInstance = jobInstanceInfo.getJobInstance();
					String jobInstanceName = jobInstanceInfo.getJobInstanceName();
					
					System.out.println("[" + jobName + "] " + 
							(isRunning?"R":"-") + 
							" #" + jobInstance + 
							" / " + jobInstanceName);
				} else {
					System.out.println("[" + jobName + "] " + 
							(isRunning?"R":"-") + 
							" #N/A");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
