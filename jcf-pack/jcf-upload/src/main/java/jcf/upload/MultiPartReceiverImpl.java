package jcf.upload;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jcf.upload.handler.DefaultUploadEventHandler;
import jcf.upload.handler.UploadEventHandler;
import jcf.upload.persistence.DefaultPersistenceManager;
import jcf.upload.persistence.PersistenceManager;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.lang.ArrayUtils;

public class MultiPartReceiverImpl implements MultiPartReceiver {

	private long maxUploadSize = -1;
	private String defaultEncoding = System.getProperty("file.encoding");
	private PersistenceManager persistenceManager = new DefaultPersistenceManager();;

	/**
	 * 멀티파트 업로드 최대 크기. 바이트 단위. 개별 파일이 아닌 총합으로 지정됨.
	 * <p>
	 * 업로드 이벤트 핸들러에서 (0이 아닌 값 지정으로) 오버라이드 가능.
	 *  <p>
	 *  기본값은 -1 (무제한)
	 *
	 * @param maxUploadSize
	 */
	public void setMaxUploadSize(long maxUploadSize) {
		this.maxUploadSize = maxUploadSize;
	}

	/**
	 * encoding used when no encoding used in the body of the request is specified.
	 * <p>
	 * 기본값은 VM 기본값 (file.encoding).
	 *
	 * @param defaultEncoding
	 */
	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}

	/**
	 * 멀티파트 파일 스트림을 받아서 처리 또는 저장할 PersistenceManager 지정.
	 * <p>
	 * 기본값은 {@link DefaultUploadEventHandler}에 사용자 홈 디렉토리 밑의 upload 디렉토리.
	 *
	 * @param persistenceManager
	 */
	public void setPersistenceManager(PersistenceManager persistenceManager) {
		this.persistenceManager = persistenceManager;
	}

	public MultiPartInfo receive(HttpServletRequest request,
			UploadEventHandler handler) {

		// Check that we have a file upload request
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (! isMultipart) {
			throw new UploadException("not a multipart request.");
		}

		if (request.getCharacterEncoding() == null) {
			try {
				request.setCharacterEncoding(defaultEncoding);

			} catch (UnsupportedEncodingException e) {
				throw new UploadException("error setting character encoding on request.", e);
			}
		}

		ServletFileUpload upload = new ServletFileUpload();
		upload.setSizeMax(handler.getMaxUploadSize() == 0? maxUploadSize : handler.getMaxUploadSize());

		FileItemIterator iter;
		try {
			iter = upload.getItemIterator(request);

		} catch (FileUploadException e) {
			throw new UploadException("error receiving file upload request", e);

		} catch (IOException e) {
			throw new UploadException("error receiving file upload request", e);
		}

		Map<String, Object> attributes = new HashMap<String, Object>();
		List<FileInfo> files = new ArrayList<FileInfo>();

		final String folder = handler.getFolder(request);

		handler.prepareStorage(persistenceManager, folder);

		int fileCount = 0;

		try {
			while (iter.hasNext()) {
			    FileItemStream item = iter.next();
			    String name = item.getFieldName();
			    InputStream stream = item.openStream();

			    if (item.isFormField()) {
			    	handleAttribute(attributes, name, stream);

			    } else {
			    	FileInfo fileInfo = new FileInfo(
							item.getFieldName(),
							item.getName(),
							item.getContentType(),
							item.getHeaders(),
							folder,
							fileCount
							);

			    	String callName = handler.createFileNameIfAccepted(folder, fileInfo);

					files.add(
			    			persistenceManager.save(
			    					folder,
			    					callName,
			    					fileInfo,
			    					stream
			    			));

			    	fileCount++;
			    }
			}

		} catch (IOException e) {
			throw new UploadException("error receiving multipart request", e);

		} catch (FileUploadException e) {
			throw new UploadException("error receiving multipart request", e);
		}

		MultiPartInfo info = new MultiPartInfo(attributes, files);

		handler.postprocess(folder, info, persistenceManager);

		return info;
	}


	private void handleAttribute(Map<String, Object> attributes, String name,
			InputStream stream) throws IOException {

		Object currentValue = null;
		String value = Streams.asString(stream);

		if(attributes.containsKey(name)){
			if(String[].class.isAssignableFrom(attributes.get(name).getClass())){
				currentValue = (String[])attributes.get(name);

			}else{
				currentValue = (String[])ArrayUtils.add((String[])currentValue, attributes.get(name));
			}

			attributes.put(name, (String[])ArrayUtils.add((String[])currentValue, value));

		}else{
			attributes.put(name, value);
		}
	}

}
