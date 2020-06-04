package jcf.util.tail;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

/**
 * StreamWriter를 기본 구현한 추상 클래스.
 * heartBeat가 일정 수 입력되면 OutputStreamWriter에 heartBeatSignal을 보낸다. 
 * 
 * @author setq
 *
 */
public abstract class AbstractStreamWriter implements StreamWriter {

	private int count = 0;
	private OutputStreamWriter writer;
	private int heartBeatThresold;
	
	public AbstractStreamWriter(OutputStream outputStream, Charset charset,  int heartBeatThresold) {
		this.heartBeatThresold = heartBeatThresold;
		writer = new OutputStreamWriter(outputStream, charset);
	}
	
	public final void heartBeat() throws IOException {
		count ++;
		if (count > heartBeatThresold) {
			count = 0;
			heartBeatSignal(writer);
			writer.flush();
		}
	}

	public final void println(String line) throws IOException {
		printFormattedLine(writer, line);
		writer.append('\n');
	}

	public void open() {
	}

	public void close() throws IOException {
		writer.flush();
	}
	
	protected abstract void heartBeatSignal(OutputStreamWriter writer) throws IOException;

	protected abstract void printFormattedLine(OutputStreamWriter writer, String line) throws IOException;
}
