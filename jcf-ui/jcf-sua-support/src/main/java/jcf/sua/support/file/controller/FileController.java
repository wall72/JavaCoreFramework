package jcf.sua.support.file.controller;

import javax.servlet.http.HttpServletRequest;

import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.MciRequestContextHolder;
import jcf.sua.mvc.MciResponse;
import jcf.sua.mvc.file.MciPersistenceManager;
import jcf.upload.FileInfo;
import jcf.upload.MultiPartInfo;
import jcf.upload.handler.UploadEventHandler;
import jcf.upload.persistence.PersistenceManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/sua/file")
public class FileController {

	private static final String replaceRegExpr = "[/_a-zA-Z0-9]*/(up|down)load/";

	@Autowired(required = false)
	private MciPersistenceManager persistence;

	@RequestMapping("/upload/**/*")
	public void upload(MciRequest request, MciResponse response, @RequestParam(defaultValue = "", value = "urn") String urn) throws Exception {

		String subDirectory = urn;

		if (!StringUtils.hasText(subDirectory)) {
			String requestURI = MciRequestContextHolder.get().getHttpServletRequest().getRequestURI();
			subDirectory = requestURI.replaceFirst(replaceRegExpr, "");
		}

		final String targetFolder = subDirectory;

		request.handleIfMultipart(new UploadEventHandler() {

			public void prepareStorage(PersistenceManager persistenceManager, String folder) {
				/*
				 * 파일을 저장하기 전 기존 파일 삭제등의 작업을 수행한다.
				 */
			}

			public void postprocess(String folder, MultiPartInfo info, PersistenceManager persistenceManager) {
				/*
				 * 파일업로드 작업과 함께 처리되어야하는 후속작업(파일과 함께 전달된 폼필드 처리등..)을 수행함.
				 */
			}

			public long getMaxUploadSize() {
				return 10*1024*1024;
			}

			public String getFolder(HttpServletRequest request) {
				return targetFolder;
			}

			public String createFileNameIfAccepted(String folder, FileInfo fileInfo) {
				return fileInfo.getCallName() == null ? fileInfo.getName() : fileInfo.getCallName();
			}

		}, persistence);

		response.addSuccessMessage("파일처리 성공");
	}

	@RequestMapping("/download/**/*")
	public void download(MciRequest request, MciResponse response, @RequestParam(defaultValue = "", value = "urn") String urn, @RequestParam(defaultValue = "", value = "fileName") String fileName) {
		String subDirectory = urn;

		if (!StringUtils.hasText(subDirectory)) {
			String requestURI = MciRequestContextHolder.get().getHttpServletRequest().getRequestURI();
			subDirectory = requestURI.replaceFirst(replaceRegExpr, "");
		}

		response.setDownloadFile(new FileInfo(subDirectory, fileName));
	}
}
