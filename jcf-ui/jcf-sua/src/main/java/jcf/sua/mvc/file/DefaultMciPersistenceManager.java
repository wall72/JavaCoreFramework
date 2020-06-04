package jcf.sua.mvc.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jcf.upload.FileInfo;
import jcf.upload.UploadException;
import jcf.upload.persistence.FileLoader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 파일 시스템을 이용한 단순한 퍼시스턴시 처리.
 * <p>
 * 특징
 * <ul>
 * <li>URN과 파일 시스템의 디렉토리가 일치.</li>
 * <li>업로드/다운로드 URN도 일치.</li>
 * </ul>
 * @author Administrator
 */
public class DefaultMciPersistenceManager implements MciPersistenceManager {

	protected static final Logger logger = LoggerFactory.getLogger(DefaultMciPersistenceManager.class);

	protected File baseDirectory = new File(System.getProperty("user.home"), "upload");
	public void setBaseDirectory(File baseDirectory) {
		this.baseDirectory = baseDirectory;
	}

	/**
	 * {@inheritDoc}
	 */
	public void delete(String urn) {
		File uploadDirectory1 = new File(baseDirectory, urn);
		File uploadDirectory = uploadDirectory1;
		try {
			FileUtils.deleteDirectory(uploadDirectory);

		} catch (IOException e) {
			throw new UploadException("error cleaning up upload directory " + uploadDirectory, e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void deleteFile(String urn, String individualFile) {
		File uploadDirectory1 = new File(baseDirectory, urn);
		File uploadDirectory = uploadDirectory1;

		File deleteFile = new File(uploadDirectory, individualFile);

		deleteFile.delete();
	}

	/**
	 * {@inheritDoc}
	 */
	public FileInfo save(String folder, String fileName, FileInfo fileInfo, InputStream inputStream) {

		File uploadDirectory = new File(baseDirectory, folder);
		File uploadFile = new File(uploadDirectory, fileName);
//		String createdUrn = urn + "/" + id;

		File tempFile;
		try {
			tempFile = File.createTempFile("upload-", null);
		} catch (IOException e) {
			throw new UploadException("error creating temp file", e);
		}

		FileOutputStream fos;
		try {
			fos = new FileOutputStream(tempFile);

		} catch (FileNotFoundException e) {
			throw new UploadException("error opening outputstream for file " + tempFile, e);
		}

		try {
			IOUtils.copyLarge(inputStream, fos);
			// wait.

		} catch (IOException e) {
			if (! tempFile.delete()) {
				throw new UploadException("error relaying inputStream to file, and deleting tempFile. " + tempFile, e);
			}
			throw new UploadException("error relaying inputStream to  tempFile " + tempFile, e);

		} finally {
			try {
				fos.close();

			} catch (IOException e) {
				logger.warn("error closing output stream " + tempFile, e);
			}
		}

		if (! uploadDirectory.exists()) {
			if (! uploadDirectory.mkdirs()) {
				throw new UploadException("cannot make directory ("+ uploadDirectory + ") for uploaded file content.");
			}
		}

		if (uploadFile.exists()) {
			if (! uploadFile.delete()) {
				throw new UploadException("cannot delete pre-existing file " + uploadFile);
			}
		}

		try {
			FileUtils.moveFile(tempFile, uploadFile);

		} catch (IOException e) {
			throw new UploadException("error renaming tempFile to  file " + uploadFile, e);

		}

		long length = uploadFile.length();

		return new FileInfo(fileInfo, fileName, length);
	}

	/**
	 * {@inheritDoc}
	 */
	public FileInfo moveFile(String sourceFolder, String sourceFileName, String targetFolder, String targetFileName) {
		File sourceDirectory = new File(baseDirectory, sourceFolder);
		File sourceFile = new File(sourceDirectory, sourceFileName);

		if(!sourceDirectory.exists() || !sourceFile.exists())	{
			throw new UploadException("error opening source file " + sourceFolder + File.separator + sourceFileName);
		}

		File targetDirectory = new File(baseDirectory, targetFolder);
		File targetFile = new File(targetDirectory, targetFileName);

		if (! targetDirectory.exists()) {
			if (! targetDirectory.mkdirs()) {
				throw new UploadException("cannot make directory ("+ targetDirectory + ") for uploaded file content.");
			}
		}

		if (targetFile.exists()) {
			if (! targetFile.delete()) {
				throw new UploadException("cannot delete pre-existing file " + targetFile);
			}
		}

		try {
			FileUtils.moveFile(sourceFile, targetFile);

		} catch (IOException e) {
			throw new UploadException("error renaming source file to  target file " + targetFile, e);

		}

		return new FileInfo(targetFolder, targetFileName);
	}

	/**
	 * {@inheritDoc}
	 */
	public FileLoader getFileLoader(final String urn, final String individualFile) {

		final File directory = new File(baseDirectory, urn);
		final File file = new File(directory, individualFile);

		return new FileLoader() {

			public long getFileSize() {
				return file.length();
			}

			public void sendFile(OutputStream outputStream) throws IOException {
				InputStream inputStream = new FileInputStream(file);
				try {
					IOUtils.copyLarge(inputStream, outputStream);
					// wait.

				} finally {
					inputStream.close();
				}
			}

		};
	}

}
