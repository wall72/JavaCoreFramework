package jcf.upload.handler;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import jcf.upload.FileInfo;
import jcf.upload.MultiPartInfo;
import jcf.upload.persistence.PersistenceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * 파일 단위의 최대 업로드 크기 제한은 없다.
 * 업로드 하기 전에 기존에 업로드 된 위치의 폴더는 삭제한다.
 * 퍼시스턴시에서 사용할 폴더 이름은 마지막 슬래시('/')가 나타나기 전까지의 urn로 지정한다.
 * 퍼시스턴시에서 사용할 파일 이름은 업로드 폼에서 지정된 파일 이름과 동일하게 가져간다.
 * </pre> 
 * @author setq
 *
 */
public class DefaultUploadEventHandler implements UploadEventHandler {
	private static final Logger logger = LoggerFactory.getLogger(DefaultUploadEventHandler.class);
	public void postprocess(String urn, MultiPartInfo info, PersistenceManager persistenceManager) {
		logger.info("form upload attributes : {}", info.getAttributes());
		Iterator<FileInfo> it = info.getFileInfos().iterator();
		while (it.hasNext()) {
			FileInfo fileInfo = it.next();
			logger.info("file uploads : {} {} {} {} {}", 
					new Object[]{
					fileInfo.getName(), 
					Long.valueOf(fileInfo.getLength()),
					fileInfo.getType(),
					fileInfo.getFolder(),
					Integer.valueOf(fileInfo.getFileCount())
					});
			}
	}

	public long getMaxUploadSize() {
		return 0;
	}

	public String createFileNameIfAccepted(String urn, FileInfo fileInfo) {
		return fileInfo.getName();
	}

	public void prepareStorage(PersistenceManager persistenceManager, String folder) {
		persistenceManager.delete(folder);
	}

	public String getFolder(HttpServletRequest request) {
		String path = request.getPathInfo();
		String urn = path.substring(0, path.lastIndexOf("/"));

		return urn;
	}

}
