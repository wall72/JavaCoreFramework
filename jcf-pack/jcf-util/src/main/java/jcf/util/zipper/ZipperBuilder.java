package jcf.util.zipper;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;

import org.springframework.core.task.SimpleAsyncTaskExecutor;

/**
 * Zipper builder.
 * <p>
 * 디렉토리 압축을 지원하는 zip 압축 파일을 생성하는 서비스인 zipper를 만드는 builder.
 * <p>
 * Zipper는 입력 파일 생성과 별도의 쓰레드로 동작하므로
 * <ol>
 * <li>최종 압축 파일을 얻는데 걸리는 대기 시간을 최소화할 수 있으며</li>
 * <li>zip파일로 들어간 소스 파일 원본은 삭제 가능(option) 하므로 디스크 공간 이용을 효율적으로 할 수 있다.</li>
 * </ol>
 * 
 * @author setq
 */
public class ZipperBuilder {

	private Executor executor;
	private File zipFile;
	private boolean deleteOnMove = true;
	
	public ZipperBuilder(File zipFile) {
		if (zipFile == null) {
			throw new NullPointerException("zipFile should not be null");
		}
		this.zipFile = zipFile;
	}
	
	public Zipper build() throws IOException {
		if (executor == null) {
			executor = new SimpleAsyncTaskExecutor();
		}
		return new Zipper(executor, zipFile, deleteOnMove);
	}
	
	public ZipperBuilder setExecutor(Executor executor) {
		this.executor = executor;
		return this;
	}

	public ZipperBuilder setDeleteOnMove(boolean deleteOnMove) {
		this.deleteOnMove = deleteOnMove;
		return this;
	}
	
}
