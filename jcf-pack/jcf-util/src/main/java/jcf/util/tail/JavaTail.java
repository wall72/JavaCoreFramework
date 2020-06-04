package jcf.util.tail;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * 순차적으로 쓰여지는 텍스트 {@link File}을 읽어서 거의 실시간으로 {@link OutputStream} 또는 {@link StreamWriter}에 출력해준다. 
 * <p>이는 unix의 "tail -f &lt;file&gt;"와 유사한 동작인데, 단 대화식 콘솔로 동작을 끊을 수 없으므로 END MARKER를 마주치게 되면 자동으로 동작을 멈추도록 하였다.
 * <p>또한 OutputStream측의 대기 시간 초과로 인한 끊김을 방지하기 위한 heart beat 기능이 구현되어있다. 
 * <p>heart beat 기능은 일정 시간동안 입력 소스 (File에 추가되는 라인)이 없으면 출력측 (OutputStream)으로 {@link StreamWriter#heartBeat()}에 정의된 동작을 하도록 하여
 * 기저에 사용된 OutputStream 구현 (서블릿 등)에서 스트림을 끊는 것을 방지한다.
 * 
 * @author setq
 *
 */
public class JavaTail {

	private static final long DEFAULT_MAX_TRAILING_BYTES = 1000;
	private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();
	
	private long maxTailingBytes = DEFAULT_MAX_TRAILING_BYTES;
	private Charset charset = DEFAULT_CHARSET;
	private File file;

	/**
	 * @param file 입력 텍스트 파일.
	 * @throws InterruptedException
	 */
	public JavaTail(File file) throws InterruptedException {
		this.file = file;
	}

	/**
	 * 지나치게 큰 파일의 경우에는 파일의 앞 부분은 건너뛰고 마지막 maxTailingBytes 부터 출력하도록 함.
	 * @param maxTailingBytes
	 * @return
	 */
	public JavaTail setMaxTailingBytes(long maxTailingBytes) {
		this.maxTailingBytes = maxTailingBytes;
		return this;
	}
	
	/**
	 * 입력 텍스트 파일의 charset 지정.
	 * @param charset
	 * @return
	 */
	public JavaTail setCharset(Charset charset) {
		this.charset = charset;
		return this;
	}
	
	/**
	 * 입력 파일의 추가 사항을 기다리지 않고 모두 출력한 후 종료.
	 * @param outputStream
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void cat(OutputStream outputStream) throws IOException, InterruptedException {
		cat(new DefaultStreamWriter(outputStream));
	}
	
	/**
	 * 입력 파일의 추가 라인을 endMarker가 나올 때까지 기다리면서 출력.
	 * <p>
	 * heartBeat를 위한 입력 검사 주기는 1000ms.
	 *  
	 * 
	 * @param outputStream
	 * @param endMarker
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void tailf(OutputStream outputStream, String endMarker) throws IOException, InterruptedException {
		tailf(new DefaultStreamWriter(outputStream), 1000, endMarker);
	}
	
	
	/**
	 * 입력 파일의 추가 사항을 기다리지 않고 모두 @{link StreamWriter}를 이용하여 출력한 후 종료.
	 * 
	 * @param streamWriter
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void cat(StreamWriter streamWriter) throws IOException, InterruptedException {
		tailInternal(new Cat(streamWriter));
	}
	/**
	 * 입력 파일의 추가 라인을 endMarker가 나올 때까지 기다리면서 @{link StreamWriter}를 이용하여 출력.
	 * <p>
	 * heartBeat를 위한 입력 검사 주기는 1000ms.
	 * @param streamWriter
	 * @param heartBeatMilliseconds
	 * @param endMarker
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void tailf(StreamWriter streamWriter, int heartBeatMilliseconds, String endMarker) throws IOException, InterruptedException {
		tailInternal(new ContinuousTail(streamWriter, heartBeatMilliseconds, endMarker));
	}
	
	private static interface Tail {
		void tail(BufferedReader br) throws IOException, InterruptedException;
	}
	private static class Cat implements Tail {

		private StreamWriter streamWriter;

		public Cat(StreamWriter streamWriter) {
			this.streamWriter = streamWriter;
		}

		public void tail(BufferedReader br) throws IOException, InterruptedException {
			streamWriter.open();
			String line;
			while ((line = br.readLine()) != null) {
				streamWriter.println(line);
			}
			streamWriter.close();
		}
		
	}
	
	private static class ContinuousTail implements Tail {

		private StreamWriter streamWriter;
		private String endMarker;
		private long heartBeatMilliseconds;

		public ContinuousTail(StreamWriter streamWriter, int heartBeatMilliseconds, String endMarker) {
			this.streamWriter = streamWriter;
			this.endMarker = endMarker;
			this.heartBeatMilliseconds = heartBeatMilliseconds;
		}

		public void tail(BufferedReader br) throws IOException, InterruptedException {
			streamWriter.open();
			while (true) {
				String line = br.readLine();
				if (line == null) {
					streamWriter.heartBeat();
					Thread.sleep(heartBeatMilliseconds);
					
				} else {
					streamWriter.println(line);
					
					if (line.startsWith(endMarker)) {
						break;
					}
				}
			}
			streamWriter.close();
		}
		
	}
	
	private void tailInternal(Tail tail) throws IOException, InterruptedException {
		BufferedReader fileReader = getFileReader(file, 3);
		
		try {
			tail.tail(fileReader);

		} finally {
			fileReader.close();
		}
		
	}

	/**
	 * 파일 리더를 얻는다. 대상 파일이 아직 생성되지 않은 경우 시간 지연을 두고 재시도.
	 * @param retryCount 재시도 횟수
	 * @return 버퍼드 리더
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	private BufferedReader getFileReader(File file, int retryCount) throws InterruptedException, IOException {
		BufferedReader br;
		try {
			FileInputStream fin = new FileInputStream(file);
			
			InputStream in;
			
			long n = file.length() - maxTailingBytes;
			
			if (n > 0) {
				fin.skip(n);
				in = new SequenceInputStream(
						new ByteArrayInputStream(
								charset.encode(CharBuffer.wrap(String.format("... skipping %,d bytes ...\n", n))).array()),
						fin
					);

			} else {
				in = fin;
			}
			
			br = new BufferedReader(new InputStreamReader(in, charset));
			
		} catch (FileNotFoundException e) {
			if (retryCount-- > 0) {
				Thread.sleep(333);
				br = getFileReader(file, retryCount);
				
			} else {
				br = null;
			}
		}
		
		return br;
	}
}
