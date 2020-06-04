package jcf.util.tail;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

/**
 * 
 * StreamWriter 기본 구현.
 * 별다른 포매팅 없이 OutputStream에 출력.
 * heartBeat는 newline(\r)
 * 
 * @author setq
 *
 */
public class DefaultStreamWriter extends AbstractStreamWriter {

	public DefaultStreamWriter(OutputStream outputStream) {
		this(outputStream, Charset.defaultCharset());
	}

	public DefaultStreamWriter(OutputStream outputStream, Charset charset) {
		this(outputStream, charset, 60);
	}
	
	public DefaultStreamWriter(OutputStream outputStream, Charset charset, int heartBeatThresold) {
		super(outputStream, charset, heartBeatThresold);
	}
	
	@Override
	protected void printFormattedLine(OutputStreamWriter writer, String line) throws IOException {
		writer.write(line);
	}

	@Override
	protected void heartBeatSignal(OutputStreamWriter writer) throws IOException {
		writer.append("\n");
	}

}
