package jcf.upload;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcf.upload.handler.DownloadEventHandler;
import jcf.upload.persistence.DefaultPersistenceManager;
import jcf.upload.persistence.FileLoader;
import jcf.upload.persistence.PersistenceManager;

import org.springframework.util.StringUtils;

public class FileSenderImpl implements FileSender {

	private PersistenceManager persistenceManager = new DefaultPersistenceManager();

	public void sendOctetStream(HttpServletRequest request, HttpServletResponse response, DownloadEventHandler handler, String folder, String individualFile) throws IOException {

		FileInfo fileInfo = new FileInfo(folder, individualFile);

		handler.preprocess(fileInfo);

		FileLoader fileLoader = persistenceManager.getFileLoader(folder, individualFile);

		String clientFileName = handler.createFileName(fileInfo);

		if(!StringUtils.hasText(clientFileName))	{
			clientFileName = individualFile;
		}

		response.setContentType("application/octet;charset=utf-8");
		response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(clientFileName,"UTF8"));
		response.setContentLength(((int)fileLoader.getFileSize()));

		if (request.isRequestedSessionIdFromURL()) {
			// 세션 생겼어야할텐데.
			if (request.getSession(false) == null) {

			} else {
				response.sendRedirect(request.getRequestURI());
			}
		}

		fileLoader.sendFile(response.getOutputStream());
	}

	public void setPersistenceManager(PersistenceManager persistenceManager) {
		this.persistenceManager = persistenceManager;
	}
}
