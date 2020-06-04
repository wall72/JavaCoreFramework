package jcf.sua.ux.json.mvc;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jcf.data.GridData;
import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.MciRequestContextHolder;
import jcf.sua.mvc.file.DefaultMciPersistenceManager;
import jcf.sua.ux.TestDataObjectBuilder;
import jcf.sua.ux.json.JsonWriter;
import jcf.sua.ux.json.dataset.JsonDataSetReader;
import jcf.sua.ux.json.mvc.file.JsonFileOperator;
import jcf.upload.FileInfo;
import jcf.upload.MultiPartInfo;
import jcf.upload.MultiPartReceiver;
import jcf.upload.MultiPartReceiverImpl;
import jcf.upload.handler.UploadEventHandler;
import jcf.upload.persistence.FileLoader;
import jcf.upload.persistence.PersistenceManager;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartHttpServletRequest;

import product.model.Product;

public class JsonRequestTest {

	private TestDataObjectBuilder builder = new TestDataObjectBuilder();

	@Test
	public void test_GetRequestURI()	{
		String exptected = "/testurl";

		MockHttpServletRequest httpRequest = new MockHttpServletRequest("GET", exptected);
		MciRequestContextHolder.get().setHttpServletRequest(httpRequest);
		MciRequest request = new JsonRequest(new JsonDataSetReader(httpRequest, null), null);

		assertThat(request.getRequestURI(), is(exptected));
	}

	@Test
	public void test_Get()	{
		Product expected = builder.buildSingleData();

		MciRequest request = getMciRequestObject(expected);

		Product actual = request.get("product", Product.class);

		assertThat(actual.getProductId(), is(expected.getProductId()));
		assertThat(actual.getProductTypeId(), is(expected.getProductTypeId()));
		assertThat(actual.getProductName(), is(expected.getProductName()));
		assertThat(actual.getProductDescription(), is(expected.getProductDescription()));
	}

	@Test
	public void test_GetGridData()	{
		List<Product> expected = builder.buildMultiData();

		MciRequest request = getMciRequestObject(expected);

		GridData<Product> actual = request.getGridData("product", Product.class);

		for (int i = 0; i < actual.size(); ++i) {
			assertThat(actual.get(i).getProductId(), is(expected.get(i).getProductId()));
			assertThat(actual.get(i).getProductTypeId(), is(expected.get(i).getProductTypeId()));
			assertThat(actual.get(i).getProductName(), is(expected.get(i).getProductName()));
			assertThat(actual.get(i).getProductDescription(), is(expected.get(i).getProductDescription()));
		}
	}

	@Test
	public void test_getMap()	{
		Product expected = builder.buildSingleData();

		MciRequest request = getMciRequestObject(expected);

		Map<String, ?> actual = request.getMap("product");

		assertThat((Integer) actual.get("productId"), is(expected.getProductId()));
		assertThat((String) actual.get("productTypeId"), is(expected.getProductTypeId()));
		assertThat((String) actual.get("productName"), is(expected.getProductName()));
		assertThat((String) actual.get("productDescription"), is(expected.getProductDescription()));
	}

	@Rule
	public TemporaryFolder baseFolder = new TemporaryFolder();

	private static final String BOUNDARY = "qWeRtY";
	private static final String ENDLINE = "\r\n";

	@Test@Ignore
	public void test_HandleIfMultipart() throws Exception	{
		String expected = "테스트 파일 내용";
		final String expectedSubFolder = "subFolder";
		final String expectedFileName = "test.txt";

		MockMultipartHttpServletRequest httpRequest = new MockMultipartHttpServletRequest();

		httpRequest.setContentType("multipart/form-data; boundary=" + BOUNDARY);
		httpRequest.setContent(createFileContents(expectedFileName, expected).getBytes());

		DefaultMciPersistenceManager persistence = new DefaultMciPersistenceManager();
		persistence.setBaseDirectory(baseFolder.getRoot());

		MultiPartReceiverImpl receiver = new MultiPartReceiverImpl();
		receiver.setPersistenceManager(persistence);

		Map<String, MultiPartReceiver> receivers = new HashMap<String, MultiPartReceiver>();
		receivers.put("/**/*", receiver);

		JsonFileOperator fileOperator = new JsonFileOperator();

		fileOperator.setTempDir("tempFolder");
		fileOperator.setReceivers(receivers);

		MciRequest request = new JsonRequest(new JsonDataSetReader(httpRequest, fileOperator), null);

		request.handleIfMultipart(new UploadEventHandler() {

			public void prepareStorage(PersistenceManager persistenceManager, String folder) {
				assertThat(folder, is(expectedSubFolder));
			}

			public void postprocess(String folder, MultiPartInfo info, PersistenceManager persistenceManager) {

			}

			public long getMaxUploadSize() {
				return 100;
			}

			public String getFolder(HttpServletRequest request) {
				return expectedSubFolder;
			}

			public String createFileNameIfAccepted(String folder, FileInfo fileInfo) {
				assertThat(fileInfo.getCallName(), is(expectedFileName));
				assertThat(folder, is(expectedSubFolder));

				return fileInfo.getCallName();
			}
		}, persistence);

		FileLoader fileLoader = persistence.getFileLoader(expectedSubFolder, expectedFileName);

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		fileLoader.sendFile(outStream);

		assertThat(outStream.toString(), is(expected));
	}

    private String createFileContents(String fileName, String fileContent) {
        StringBuilder reqContent = new StringBuilder();

        reqContent.append("--" + BOUNDARY + ENDLINE);
        reqContent.append("Content-Disposition: form-data;");
        reqContent.append("name=\"testfile1\"; filename=\"" + fileName + "\"" + ENDLINE);
        reqContent.append(ENDLINE);
        reqContent.append(fileContent);
        reqContent.append(ENDLINE);
        reqContent.append("--" + BOUNDARY + "--" + ENDLINE);

        return reqContent.toString();

    }

	private MciRequest getMciRequestObject(final Object object) {
		return new JsonRequest(new JsonDataSetReader(new MockHttpServletRequest(){

			@Override
			public BufferedReader getReader() throws UnsupportedEncodingException {
				return new BufferedReader(new StringReader(new JsonWriter().writeDataObject(object)));
			}

		}, null), null);
	}

}
