package jcf.upload.persistence;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import jcf.upload.FileInfo;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DefaultPersistenceManagerTest {

	@Rule
	public TemporaryFolder testRoot = new TemporaryFolder();
	
	@Test
	public void testDelete() throws IOException {
		DefaultPersistenceManager persistenceManager = new DefaultPersistenceManager();
		persistenceManager.setBaseDirectory(testRoot.getRoot());

		File folder = testRoot.newFolder();
		
		File file = File.createTempFile("pre", "suf", folder);
		
		// when
		persistenceManager.delete(folder.getName());
		
		// then
		assertFalse("delete 후에는 해당 폴더의 임시파일이 삭제되어야 한다.", file.exists());
		assertFalse("delete 후에는 해당 폴더내의 파일 뿐 아니라 폴더도 삭제되어야 한다.", folder.exists());
	}

	@Test
	public void testDeleteFile() throws IOException {
		DefaultPersistenceManager persistenceManager = new DefaultPersistenceManager();
		persistenceManager.setBaseDirectory(testRoot.getRoot());

		File folder = testRoot.newFolder();
		
		File file = File.createTempFile("pre", "suf", folder);
		
		// when
		persistenceManager.deleteFile(folder.getName(), file.getName());
		
		// then
		assertFalse("delete 호출 후에는 해당 임시파일이 삭제되어야 한다.", file.exists());
	}

	@Test
	public void testSave() throws IOException {
		DefaultPersistenceManager persistenceManager = new DefaultPersistenceManager();
		persistenceManager.setBaseDirectory(testRoot.getRoot());

		File folder = testRoot.newFolder();
		folder.delete(); // 폴더 이름만 필요.
		
		byte[] bs = new byte[]{0x00};
		
		InputStream stream = new ByteArrayInputStream(bs);

		String multipartFormFileName1 = "multipartFormFileName1";
		FileInfo inFileInfo = new FileInfo(folder.getName(), multipartFormFileName1); // confusing...
		
		String persistName1 = "persistName1";
		
		// when
		FileInfo outFileInfo = persistenceManager.save(folder.getName(), persistName1 , inFileInfo, stream); 
		
		// then
		assertTrue("업로드된 파일이 파일시스템에 존재해야 한다.", new File(folder, persistName1).exists());
		assertFalse("입/출력 FileInfo는 다른 인스턴스여야 한다.", inFileInfo == outFileInfo);
		assertEquals("다 받은 후에는 파일 길이 정보를 얻을 수 있어야 한다.", bs.length, outFileInfo.getLength()); 
		assertEquals("업로드 폼에서 지정한 파일 이름을 퍼시스턴시 매너저에서 지정한 파일 이름과 구분하여 얻을 수 있어야 한다.", multipartFormFileName1, outFileInfo.getName()); 
		assertEquals("퍼시스턴시 매니저에서 지정한 파일 이름을 업로드 폼에서 지정한 파일 이름과 구분하여 얻을 수 있어야 한다.", persistName1, outFileInfo.getCallName());
	}

	@Test
	public void testGetFileLoader() throws IOException {
		DefaultPersistenceManager persistenceManager = new DefaultPersistenceManager();
		persistenceManager.setBaseDirectory(testRoot.getRoot());

		File folder = testRoot.newFolder();
		folder.delete(); // 폴더 이름만 필요.
		
		byte[] bs = new byte[]{0x00};
		
		InputStream stream = new ByteArrayInputStream(bs);

		String multipartFormFileName1 = "multipartFormFileName1";
		FileInfo inFileInfo = new FileInfo(folder.getName(), multipartFormFileName1); // confusing...
		
		String persistName1 = "persistName1";
		
		persistenceManager.save(folder.getName(), persistName1 , inFileInfo, stream); 
		
		// when
		FileLoader fileLoader = persistenceManager.getFileLoader(folder.getName(), persistName1);
		
		// then
		assertEquals("저장소로부터 파일 크기를 다시 얻어올 수 있어야 한다.", bs.length, fileLoader.getFileSize());
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bs.length);
		fileLoader.sendFile(outputStream);
		
		assertArrayEquals("저장한 파일 컨텐츠가 그대로 다시 로딩되어야 한다.", bs, outputStream.toByteArray());
		
	}

}
