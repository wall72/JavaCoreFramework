package jcf.cmd.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;

import jline.ArgumentCompletor;
import jline.Completor;
import jline.ConsoleReader;
import jline.FileNameCompletor;
import jline.History;
import jline.MultiCompletor;
import jline.NullCompletor;
import jline.SimpleCompletor;

import org.apache.commons.io.FilenameUtils;

/**
 * @author setq
 *
 */
public class ArgumentInput {
	
	public static class Argument {
		private String name;
		private String value;
		private boolean isSet;
		
		public Argument(String name) {
			this.name = name;
		}
		public Argument(String name, String value) {
			this.name = name;
			this.value = value;
			isSet = true;
		}
		public boolean isSet() {
			return isSet;
		}
		public void setValue(String value) {
			this.value = value;
			isSet = true;
		}
		public String getValue() {
			if (isSet) {
				return value;
				
			} else {
				throw new IllegalStateException(name + " is not set");
			}
		}
		@Override
		public String toString() {
			if (isSet) {
				return value;
				
			} else {
				return "<NOT SET>";
			}
		}
	}

	private static final String DEBUG = "debug";
	private static final String PROMPT = "env> ";

//	public static void main(String[] args) {
//		ArgumentInput input = new ArgumentInput("OS", "PROMPT", "name", "address", "zipcode", "TMP", "TEMP");
//		
//		System.out.println(input.get("name"));
//		System.out.println(input.get("address"));
//		System.out.println(input.get("zipcode"));
//		System.out.println(input.get("TMP"));
//		
//	}
	
	private Map<String, Argument> args = new LinkedHashMap<String, Argument>();
	
	public ArgumentInput(String... argNames) {
		init(argNames);
	}
/*
 * load <property file name[.properties]>
 * save <property file name[.properties]>
 * set
 * - show properties
 * set <argName> <argValue>
 * unset <argName>
 * go
 * stop
 * 
 */
	private void init(String... argNames) {
		for (int i = 0; i < argNames.length; i++) {
			String name = argNames[i];
			
			String value = System.getProperty(name);
			if (value == null) {
				value = System.getenv(name);
			}
			if (value == null) {
				args.put(name, new Argument(name));
				
			} else { 
				args.put(name, new Argument(name, value));
			}
		}
		
		try {
			requireMoreArgument(args);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void requireMoreArgument(Map<String, Argument> args) throws IOException {

		if (System.getProperty(DEBUG) == null) {
			return;
		}
		
		System.err.println("please enter missing arguments from environment variables.");
		System.err.println("press [TAB] key to show candidate commands");
		printUsageAndStatus();
		
		final ConsoleReader consoleReader = new ConsoleReader();
		consoleReader.setHistory(getHistory());
		
		SortedSet<String> missingArguments = getMissingArguments(args);
		
		SimpleCompletor candidateCompletor = new SimpleCompletor("ignored");
		candidateCompletor.setCandidates(new TreeSet<String>(args.keySet()) );
		
		consoleReader.addCompletor (
				new MultiCompletor(new Completor[]{
						new ArgumentCompletor(
								new Completor[] {
										new SimpleCompletor(new String[]{"set", "unset"}),
										candidateCompletor,
										new NullCompletor()
								}
						),		
						new ArgumentCompletor(
								new Completor[] {
										new SimpleCompletor(new String[]{"load", "save"}),
										new FileNameCompletor(),
										new NullCompletor()
								}
						),
						new SimpleCompletor(new String[]{"go", "stop"})
				})
		);
		
		
		String line;
		
		for (line = consoleReader.readLine(PROMPT); line != null; line = consoleReader.readLine(PROMPT)) {
			Scanner scanner = new Scanner(line);
			
			if (scanner.hasNext()) {
				String command = scanner.next();
				
				if ("set".equals(command)) {
					doSet(scanner, args, missingArguments);
					
				} else if ("unset".equals(command)) {
					doUnset(scanner, args, missingArguments);
					
				} else if ("load".equals(command)) {
					doLoad(scanner, args, missingArguments);
					
				} else if ("save".equals(command)) {
					doSave(scanner, args, missingArguments);
					
				} else if ("go".equals(command)) {
					break;
					
				} else if ("stop".equals(command)) {
					throw new RuntimeException("stopping on user request.");
					
				} else {
					printUsageAndStatus();
				}

			} else {
				printUsageAndStatus();
				
			}
		}
	}

	private void doSave(Scanner scanner, Map<String, Argument> args,
			SortedSet<String> missingArguments) {
		if (scanner.hasNext()) {
			String fileName = scanner.next();
			if ("".equals(FilenameUtils.getExtension(fileName))) {
				fileName += ".properties";
			}
			
			try {
				save(fileName, args);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} else {
			printUsageAndStatus();
		}
	}
	private void save(String fileName, Map<String, Argument> args) throws IOException {
		Properties properties = new Properties();
		
		for (Entry<String, Argument> entry : args.entrySet()) {
			if (entry.getValue().isSet()) {
				properties.put(entry.getKey(), entry.getValue().getValue());
			}
		}
		
		FileOutputStream outputStream = new FileOutputStream(fileName);
		
		try {
			properties.store(outputStream, "argument save file");
		
		} finally {
			outputStream.close();
		}
		
	}
	private void doLoad(Scanner scanner, Map<String, Argument> args2,
			SortedSet<String> missingArguments) {
		if (scanner.hasNext()) {
			String fileName = scanner.next();
			if ("".equals(FilenameUtils.getExtension(fileName))) {
				fileName += ".properties";
			}
			
			try {
				load(fileName, args);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} else {
			printUsageAndStatus();
		}
		
	}
	
	private void load(String fileName, Map<String, Argument> args) throws IOException {
		Properties properties = new Properties();

		FileInputStream inputStream = new FileInputStream(fileName);
		try {
			properties.load(inputStream);
			
		} finally {
			inputStream.close();
		}
		
		for (Entry<Object, Object> entry : properties.entrySet()) {
			args.put((String) entry.getKey(), new Argument((String) entry.getKey(), (String) entry.getValue()));
		}
		
	}
	private void doUnset(Scanner scanner, Map<String, Argument> args, SortedSet<String> missingArguments) {
		if (scanner.hasNext()) {
			String inArgName = scanner.next();
			
			if (args.containsKey(inArgName)) {
				args.put(inArgName, new Argument(inArgName));
				missingArguments.add(inArgName);
				
			} else {
				System.err.println("argument " + inArgName + " is not declared");
			}
			
		} else {
			printUsageAndStatus();
		}
	}

	private void doSet(Scanner scanner, Map<String, Argument> args, SortedSet<String> missingArguments) {
		if (scanner.hasNext()) {
			String inArgName = scanner.next();
			
			if (args.containsKey(inArgName)) {
				String inValue = scanner.findInLine("[^\\s].*");
				args.put(inArgName, new Argument(inArgName, inValue));
				
				missingArguments.remove(inArgName);
				
			} else {
				System.err.println("argument " + inArgName + " is not declared");
			}
			
		} else {
			printUsageAndStatus();
		}
	}

	private SortedSet<String> getMissingArguments(Map<String, Argument> args) {
		SortedSet<String> missingArguments = new TreeSet<String>();
		for (Entry<String, Argument> entry : args.entrySet()) {
			if (!entry.getValue().isSet()) {
				missingArguments.add(entry.getKey());
			}
		}
		return missingArguments;
	}

	private History getHistory() {
		History history = new History();
		for (Entry<String, Argument> entry : args.entrySet()) {
			if (entry.getValue().isSet()) {
				history.addToHistory("set " + entry.getKey() + " " + entry.getValue());
			}
		}
		return history;
	}

	private void printUsageAndStatus() {
		System.err.println("input format : [set|unset] <argName> <argValue>");
		for (Entry<String, Argument> entry : args.entrySet()) {
			boolean isSet = entry.getValue().isSet();
			System.err.print(isSet?"  ":"> ");
			System.err.print(entry.getKey());
			System.err.print("=");
			System.err.println(entry.getValue());
		}
	}

	public String get(String argName) {
		if (args.containsKey(argName)) {
			return args.get(argName).getValue();
			
		} else {
			throw new IllegalStateException("argument " + argName + " is not declared.");
		}
	}
	
	/**
	 * WARN : {@link #get(String)}을 호출하는 경우에는 존재하지 않는 엔트리의 경우 예외를 발생시키지만{@link #getArgumentsAsMap()}을 호출하는 경우에는 Map의 규약 대로 null을 반환한다.
	 * @return
	 */
	public Map<String, String> getArgumentsAsMap() {
		Map<String, String> ret = new HashMap<String, String>();
		for (Entry<String, Argument> entry : args.entrySet()) {
			if (entry.getValue().isSet()) {
				ret.put(entry.getKey(), entry.getValue().getValue());
			}
		}
		return ret;
	}
	
}
