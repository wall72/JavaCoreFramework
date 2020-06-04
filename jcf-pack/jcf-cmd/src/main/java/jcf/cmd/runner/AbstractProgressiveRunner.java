package jcf.cmd.runner;


import java.io.File;

import jcf.cmd.progress.builder.ProgressWriterBuilder;
import jcf.cmd.progress.builder.ProgressWriterBuilderImpl;
import jcf.cmd.progress.writer.MappedFileWriter;
import jcf.cmd.progress.writer.ProgressWriter;

/**
 * 진행률 등의 정보를 파일에 실시간으로 기록하는 CommandLineRunner.
 * 
 * @author setq
 *
 */
public abstract class AbstractProgressiveRunner implements CommandLineRunner {
	
	private static final String PROGRESS_FILE = "progress";

	public final void run(String[] args) {
		MappedFileWriter writer = new MappedFileWriter(new File(PROGRESS_FILE));

		try {
			run(prepare(new ProgressWriterBuilderImpl(writer)));
			
		} finally {
			writer.close();
		}
	}

	/**
	 * 파일에 쓸 진행률 데이터 구조를 정의한다.
	 * <p>
	 * 지원되는 유형은 자바의 short, int, long, byte array(N), string(N) 등이다. 
	 * 
	 * @param builder
	 * @return
	 */
	protected abstract ProgressWriter prepare(ProgressWriterBuilder builder);
	
	/**
	 * 로직을 수행하고 적절히 진행률을 세팅한다.
	 * 
	 * @param progressWriter
	 */
	protected abstract void run(ProgressWriter progressWriter);

}
