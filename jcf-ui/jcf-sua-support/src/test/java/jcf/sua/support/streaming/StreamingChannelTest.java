package jcf.sua.support.streaming;

import jcf.sua.TestDispachterServlet;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartHttpServletRequest;

@Ignore
public class StreamingChannelTest {

	private static final String BOUNDARY = "qWeRtY";
	private static final String ENDLINE = "\r\n";

	private TestDispachterServlet servlet = new TestDispachterServlet("jcf/sua/support/streaming/StreamingChannelTest-Context.xml");

	@Test
	public void testUpload() throws Exception {
		MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();

		request.setContentType("multipart/form-data; boundary=" + BOUNDARY);
		request.setRequestURI("/sua/file/upload/a/b/c");
		request.setContent(createFileContents("testFile.txt", "test test").getBytes());
//		request.addParameter("urn", "/urn");

		servlet.service(request, response);
	}

	@Test
	public void testDownload() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();

		request.setMethod("GET");
		request.setRequestURI("/sua/file/download/a/b/c");
		request.addParameter("fileName", "testFile.txt");

		servlet.service(request, response);
	}

	private String createFileContents(String fileName, String fileContent) {
		StringBuilder reqContent = new StringBuilder();

		reqContent.append("--" + BOUNDARY + ENDLINE);
		reqContent.append("Content-Disposition: form-data;");
		reqContent.append("name=\"fileField\"; filename=\"" + fileName + "\"" + ENDLINE);
		reqContent.append(ENDLINE);
		reqContent.append(fileContent);
		reqContent.append(ENDLINE);
		reqContent.append("--" + BOUNDARY + "--" + ENDLINE);

		return reqContent.toString();

	}
}
