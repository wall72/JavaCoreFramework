package jcf.upload;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcf.upload.handler.DownloadEventHandler;

/**
 * 파일을 클라이언트로 전송한다.
 *
 * @author nolang
 *
 */
public interface FileSender {

	/**
	 * 하나의 파일을 지정하여 서블릿 클라이언트로 전송.
	 *
	 * @param request
	 * @param response
	 * @param handler
	 * @param folder
	 * @param individualFile
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	void sendOctetStream(HttpServletRequest request, HttpServletResponse response, DownloadEventHandler handler, String folder, String individualFile) throws IOException;

}
