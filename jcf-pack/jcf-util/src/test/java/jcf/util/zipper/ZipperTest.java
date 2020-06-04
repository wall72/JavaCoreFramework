package jcf.util.zipper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorCompletionService;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Ignore
public class ZipperTest {

	@Rule
	public TemporaryFolder folder= new TemporaryFolder();

	private static class ZipRunnable implements Runnable {

		private File file;
		private Zipper zipper;

		public ZipRunnable(Zipper zipper, File file) {
			this.zipper = zipper;
			this.file = file;
		}

		public void run() {
			try {
				makeTestFile(file);
				zipper.registerFile(file);

			} catch (IOException e) {
				throw new RuntimeException("error creating testfile", e);
			}

		}

		private void makeTestFile(File file) throws IOException {
			FileWriter fileWriter = new FileWriter(file);
			try {
				fileWriter.write("hello");

			} finally {
				fileWriter.close();
			}
		}
	}

	@Test
	public void testZipServer() throws IOException, InterruptedException {
		File zipFile= folder.newFile("myfile.zip");
		File createdFolder= folder.newFolder("subfolder");

		Zipper zipper = new ZipperBuilder(zipFile).build();

		ExecutorCompletionService<Boolean> ecs = new ExecutorCompletionService<Boolean>(new SimpleAsyncTaskExecutor());

		int repeatCount = 10;

		for (int i = 0; i < repeatCount; i++) {
			File file = new File(createdFolder, "file" + i);
			Runnable task = new ZipRunnable(zipper, file);
			ecs.submit(task , true);
		}

		for (int i = 0; i < repeatCount; i++) {
			ecs.take();
		}

		zipper.shutdown();

	}

}
