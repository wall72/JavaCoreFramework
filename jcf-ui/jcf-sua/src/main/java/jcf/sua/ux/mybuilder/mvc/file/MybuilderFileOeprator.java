package jcf.sua.ux.mybuilder.mvc.file;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import jcf.sua.exception.MciException;
import jcf.sua.mvc.file.operator.FileOperator;
import jcf.upload.FileInfo;
import jcf.upload.FileSender;
import jcf.upload.MultiPartInfo;
import jcf.upload.MultiPartReceiver;
import jcf.upload.handler.DownloadEventHandler;
import jcf.upload.handler.UploadEventHandler;
import jcf.upload.persistence.PersistenceManager;

/**
 * {@link FileOperator}
 *
 * @author nolang
 *
 */
public class MybuilderFileOeprator implements FileOperator {

	private static Logger logger = LoggerFactory.getLogger(MybuilderFileOeprator.class);
	private Map<String, UploadEventHandler> uploadEventHandlers;
	private Map<String, DownloadEventHandler> downloadEventHandlers;
	private PathMatcher pathMatcher = new AntPathMatcher();
	private MultiPartReceiver receiver;
	private FileSender sender;
	private String tempDir = "tempdir";

	private UploadEventHandler defaultUploadEventHandler = new UploadEventHandler() {

		public void prepareStorage(PersistenceManager persistenceManager, String folder) {
		}

		public void postprocess(String folder, MultiPartInfo info, PersistenceManager persistenceManager) {
		}

		public long getMaxUploadSize() {
			return 0;
		}

		public String getFolder(HttpServletRequest request) {
			return tempDir;
		}

		public String createFileNameIfAccepted(String folder, FileInfo fileInfo) {
			String fileName = fileInfo.getName();

			int index = fileName.lastIndexOf(File.separator);

			if(index > 0){
				fileName = fileName.substring(index + 1);
			}

			return fileName;
		}
	};

	private DownloadEventHandler defaultDownloadEventHandler = new DownloadEventHandler() {

		public void preprocess(FileInfo fileInfo) {
			// TODO Auto-generated method stub

		}

		public String createFileName(FileInfo fileInfo) {
			// TODO Auto-generated method stub
			return null;
		}

	};

	/**
	 * {@inheritDoc}
	 */
	public boolean isMultiPartRequest(HttpServletRequest request) {
		return ServletFileUpload.isMultipartContent(request);
	}

	/**
	 * {@inheritDoc}
	 */
	public MultiPartInfo handleMultiPartRequest(HttpServletRequest request) {
		try {
			if(receiver == null)	{
				throw new MciException("[JCF-SUA] MyBuilderFileOperator - MultiPartReceiver가 정의되어 있지 않습니다.");
			}

			UploadEventHandler eventHandler = null;

			if (uploadEventHandlers != null) {
				for (Map.Entry<String, UploadEventHandler> e : uploadEventHandlers.entrySet()) {
					if(pathMatcher.match(e.getKey(), request.getRequestURI()))	{
						eventHandler = e.getValue();
						break;
					}
				}
			}

			if(eventHandler == null)	{
				eventHandler = defaultUploadEventHandler;
			}

			return receiver.receive(request, eventHandler);

		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				e.printStackTrace();
			}

			throw new MciException("[JCF-SUA] MyBuilderFileOperator - File Upload 처리중 에러 발생", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void sendFileStream(HttpServletRequest request,
			HttpServletResponse response, FileInfo fileInfo) {
		try {
			if(sender == null)	{
				throw new MciException("[JCF-SUA] MyBuilderFileOperator - FileSender가 정의되어 있지 않습니다.");
			}

			DownloadEventHandler eventHandler = null;

			if (downloadEventHandlers != null) {
				for (Map.Entry<String, DownloadEventHandler> e : downloadEventHandlers.entrySet()) {
					if(pathMatcher.match(e.getKey(), request.getRequestURI()))	{
						eventHandler = e.getValue();
						break;
					}
				}
			}

			if(eventHandler == null)	{
				eventHandler = defaultDownloadEventHandler;
			}

			sender.sendOctetStream(request, response, eventHandler, fileInfo.getFolder(), fileInfo.getName());

		} catch (IOException e) {
			if(logger.isErrorEnabled()){
				e.printStackTrace();
			}

			throw new MciException("[JCF-SUA] MyBuilderFileOperator - File Download 처리중 에러 발생", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setUploadEventHandlers(Map<String, UploadEventHandler> uploadEventHandlers) {
		this.uploadEventHandlers = uploadEventHandlers;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDownloadEventHandlers(Map<String, DownloadEventHandler> downloadEventHandlers) {
		this.downloadEventHandlers = downloadEventHandlers;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setReceiver(MultiPartReceiver receiver) {
		this.receiver = receiver;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setSender(FileSender sender) {
		this.sender = sender;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setPathMatcher(PathMatcher pathMatcher) {
		this.pathMatcher = pathMatcher;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setTempDir(String tempDir) {
		this.tempDir = tempDir;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDefaultUploadEventHandler(
			UploadEventHandler defaultUploadEventHandler) {
		this.defaultUploadEventHandler = defaultUploadEventHandler;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDefaultDownloadEventHandler(
			DownloadEventHandler defaultDownloadEventHandler) {
		this.defaultDownloadEventHandler = defaultDownloadEventHandler;
	}

}
