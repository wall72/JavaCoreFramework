package jcf.upload.handler;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;

import jcf.upload.FileInfo;
import jcf.upload.MultiPartInfo;
import jcf.upload.persistence.PersistenceManager;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

public class DefaultUploadEventHandlerTest {

	DefaultUploadEventHandler handler = new DefaultUploadEventHandler();

	@Test
	public void testPostprocess() {
		MultiPartInfo info = getMultiPartInfo();

		handler.postprocess(null, info, null);
	}

	private static MultiPartInfo getMultiPartInfo() {
		return new MultiPartInfo(Collections.<String, Object> emptyMap(),
				Collections.<FileInfo> singletonList(new FileInfo("folder",
						"name")));
	}

	@Test
	public void testGetMaxUploadSize() {
		assertEquals(0, handler.getMaxUploadSize());
	}

	@Test
	public void testCreateFileNameIfAccepted() {
		FileInfo fileInfo = new FileInfo("folder", "name");
		String urn = "any";
		
		//when
		String fileName = handler.createFileNameIfAccepted(urn, fileInfo);

		//then
		assertEquals(fileInfo.getName(), fileName);
	}

	@Test
	public void testPrepareStorage() {
		//given
		PersistenceManager persistenceManager = mock(PersistenceManager.class);
		
		//when
		handler.prepareStorage(persistenceManager, "folder");

		//then
		verify(persistenceManager).delete(anyString());
	}

	@Test
	public void testGetFolder() {
		//given
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setPathInfo("/kim/min/ah");
		
		//when
		String folder = handler.getFolder(request);
		
		//then
		assertEquals("/kim/min", folder);
	}

}
