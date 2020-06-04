package jcf.cmd.cron.monitor;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

import jcf.cmd.AbstractTestParent;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class FileScheduleChangeMonitorTest extends AbstractTestParent {

	private static final Charset ENCODING = Charset.forName("UTF-8");

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder(); 
	
	ScheduleUpdater updater = new ScheduleUpdater() {
		
		public void updateSchedule(String jobName, String cmdLine,
				String cronExpression) {
			logger.info("jobName {}", jobName);
			logger.info("cmdLine {}", cmdLine);
			logger.info("cronExpression {}", cronExpression);
		}
	};

	@Test
	public void crontab파일_변경_감지() throws IOException, InterruptedException {
		File file = tempFolder.newFile("crontab");
		writeToFile(file);
		
		FileScheduleChangeChecker listener = new FileScheduleChangeChecker(file);
		assertFalse(listener.isModified());

		Thread.sleep(1000); // depends on file system time granularity
		
		writeToFile(file);

		assertTrue(listener.isModified());
	}

	@Test
	public void crontab파일_읽기() throws IOException, IOException {
		File file = tempFolder.newFile("crontab");
		
		writeToFile(file);
		
		FileScheduleChangeChecker listener = new FileScheduleChangeChecker(file);

		listener.load(updater);
	}
	

	@Test
	public void crontab파일_생성() throws IOException {
		File file = new File("crontab");

		try {
			FileScheduleChangeChecker listener = new FileScheduleChangeChecker(file);

			listener.load(updater);
			
			assertTrue(file.exists());
			
		} finally {

			file.delete();
		}
	}
	
	private void writeToFile(File file) throws FileNotFoundException, IOException {
		FileWriterWithEncoding writer = new FileWriterWithEncoding(file, ENCODING, true);
		writer.append("# comment line\n");
		writer.append("invalid line\n");
		writer.append("0 0/5 * * * ?	TEST/INSTANCE1 sampleRunner arg0 arg1\n");
		writer.append("0 10 * * * MON-FRI TEST/INSTANCE2 weekDayRunner\n");
		writer.append("0 10 * * * MON-FRI TEST/INSTANCE2 weekDayRunner\n");
		writer.append("time " + System.currentTimeMillis());
		writer.close();
	}
}
