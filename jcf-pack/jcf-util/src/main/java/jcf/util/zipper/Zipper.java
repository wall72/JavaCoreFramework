package jcf.util.zipper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.zip.Deflater;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 디렉토리 압축을 지원하는 zip 압축 파일을 생성하는 서비스
 * <p>
 * Zipper는 입력 파일 생성과 별도의 쓰레드로 동작하므로
 * <ol>
 * <li>최종 압축 파일을 얻는데 걸리는 대기 시간을 최소화할 수 있으며</li>
 * <li>zip파일로 들어간 소스 파일 원본은 삭제 가능(option) 하므로 디스크 공간 이용을 효율적으로 할 수 있다.</li>
 * </ol>
 * 입력 파일 추가가 끝나면 반드시 {@link #shutdown()}을 불러서 압축 작업 완료 및 쓰레드 종료를 시키도록 한다.
 * <p>
 * 입력 파일 추가하는 도중 간간이 {@link #checkForFailure()}를 불러서 압축 작업에 예외가 발생하였는지를 체크하도록 한다.
 * 
 * @author setq
 *
 */
public class Zipper {
	
	private static final Logger logger = LoggerFactory.getLogger(Zipper.class);
	
	private LinkedBlockingQueue<File> queue = new LinkedBlockingQueue<File>();

	private ZipArchiveRunnable zipArchiveRunnable = new ZipArchiveRunnable();
	private FutureTask<Boolean> command = new FutureTask<Boolean>(zipArchiveRunnable, true);
	
	private ZipArchiveOutputStream output;
	private File zipFile;
	private Executor executor;
	private boolean deleteOnMove = true;

	
	/**
	 * @param executor
	 * @param zipFile
	 * @throws IOException 
	 */
	Zipper(Executor executor, File zipFile, boolean deleteOnMove) throws IOException {
		this.executor = executor;
		this.zipFile = zipFile; 
		this.deleteOnMove = deleteOnMove;
		
		start();
	}
	
	/**
	 * let zipserver archive the file.
	 * <p>
	 * file entry name in zip archive is based on File name (relative pathName expected).
	 * 
	 * @param file
	 */
	public void registerFile(File file) {
		if (zipArchiveRunnable.keepRunning) {
			logger.debug("registering {}", file);
			queue.add(file);
		} else {
			logger.debug("server is shutdown. abandoning {}", file);
		}
		checkForFailure();
	}

	/**
	 * check if zip stream thread has thrown exception, throwing {@link ZipperException}.
	 */
	public void checkForFailure() {
		if (command.isDone()) {
			try {
				command.get();
				
			} catch (InterruptedException e) {
				throw new ZipperException("error waiting for zip archiving job to finish.", e);

			} catch (ExecutionException e) {
				throw new ZipperException("error while executing zip archiving job", e.getCause());
			}
		}
	}

	/**
	 * wait for zip stream thread to finish then close zip output stream.
	 */
	public void shutdown() {
		if (!queue.isEmpty()) {
			try {
				Thread.sleep(3000);

			} catch (InterruptedException e) {
//				throw new ZipServerException("waiting compensating for i/o lag time", e);
				logger.warn("interrupted waiting compensating for i/o lag time. ignoring.");
			}
		}

		zipArchiveRunnable.keepRunning = false;
		
		logger.info("server shutdown init");

		try {
			command.get();

		} catch (InterruptedException e) {
			throw new ZipperException("error waiting for zip archiving job to finish.", e);

		} catch (ExecutionException e) {
			throw new ZipperException("error while executing zip archiving job", e.getCause());
		
		} finally {
			IOUtils.closeQuietly(output);
		}

		logger.info("server shutdown complete");
	}

	private void start() throws IOException {
		if (!zipArchiveRunnable.keepRunning) {
			throw new IllegalStateException("cannot re-start. server is shutdown.");
		}
		output = new ZipArchiveOutputStream(zipFile);
		output.setLevel(Deflater.BEST_COMPRESSION);
//		output.setLevel(Deflater.DEFAULT_COMPRESSION);

		executor.execute(command);

		logger.info("server starting.");
	}
	
	private class ZipArchiveRunnable implements Runnable {

		boolean keepRunning = true;
		
		public void run() {
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		
			logger.info("Thread priority : {}", Thread.currentThread().getPriority());
			
			while (keepRunning || !queue.isEmpty()) {
				handleFileEntry(pollQueue());
			}

			logger.debug("exiting server thread");
		}

		private File pollQueue() {
			try {
				return queue.poll(1, TimeUnit.SECONDS);
				
			} catch (InterruptedException e) {
				throw new ZipperException("waiting for fileEntry", e);
			}
		}

		private void handleFileEntry(File file) {
			if (file == null) {
				return;
			}
			
			FileInputStream fileInputStream = getFileInputStream(file);
			if (fileInputStream == null) {
				if (keepRunning) {
					if (queue.isEmpty()) {
						try {
							Thread.sleep(1000);

						} catch (InterruptedException e) {
							throw new ZipperException("waiting for file " + file, e);
						}
					}
					queue.add(file);
					
				} else {
					fileInputStream = getFileInputStream(file);
					if (fileInputStream == null) {
//						logger.warn("skipping file : {}", file);
						throw new ZipperException("cannot read file " + file);
					}
				}
			}
			
			if (fileInputStream != null) {
				try {
					copyStreamToZipEntry(file, fileInputStream);
					
				} catch (IOException e) {
					throw new ZipperException("adding fileEntry to zip ", e);
				
				} finally {
					IOUtils.closeQuietly(fileInputStream);
				}
				if (deleteOnMove) {
					if (!file.delete()) {
						logger.warn("error deleting file {}", file);
					}
				}
			}
		}

		private FileInputStream getFileInputStream(final File file) {
			FileInputStream fileInputStream = null; 
			try {
				fileInputStream = new FileInputStream(file);
				
			} catch (FileNotFoundException e) {
				; // ignore returning null
			}
			
			return fileInputStream;
		}

		private void copyStreamToZipEntry(final File file, FileInputStream fileInputStream) throws IOException {
			logger.debug("copying {} {}", file, file.length());
			
			output.putArchiveEntry(new ZipArchiveEntry(file.getPath()));
			IOUtils.copy(fileInputStream, output);
			output.closeArchiveEntry();
		}
		
	}

}
