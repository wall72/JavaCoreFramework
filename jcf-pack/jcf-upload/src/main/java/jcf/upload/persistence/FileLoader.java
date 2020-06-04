package jcf.upload.persistence;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 파일 송신을 위한 세션.
 */
public interface FileLoader {

	/**
	 * @return 저장소로부터 얻어온 파일의 크기
	 */
	long getFileSize();

	/**
	 * 파일 전송. (스트림 복사)
	 * @param outputStream
	 * @throws IOException 
	 */
	void sendFile(OutputStream outputStream) throws IOException;

}
