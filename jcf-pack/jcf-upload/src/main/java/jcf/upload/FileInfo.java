package jcf.upload;

import org.apache.commons.fileupload.FileItemHeaders;

/**
 * 파일 업로드/다운로드 개별 파일 정보.
 *
 * @author setq
 */
public final class FileInfo {
	final private String fieldName;
	final private String name;
	final private String type;
	final private FileItemHeaders headers;
	final private String folder;
	final private int fileCount;
	private long length;
	private String callName;

	public FileInfo(String folder, String name) {
		this(null, name, null,   null, folder, 0);
	}

	public FileInfo(String fieldName, String name, String type,
			FileItemHeaders headers, String folder, int fileCount) {
		this.fieldName = fieldName;
		this.name = name;
		this.type = type;
		this.headers = headers;
		this.folder = folder;
		this.fileCount = fileCount;
	}

	public FileInfo(FileInfo f, String callName, long length) {
		this(f.getFieldName(), f.getName(), f.getType(), f.getHeaders(), f.getFolder(), f.getFileCount());
		this.length = length;
		this.callName = callName;
	}

	/**
	 * @see #getCallName()
	 * @return 멀티파트 업로드 폼에서 주어진 파일 이름.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return Returns the content type passed by the browser or null if not defined.
	 */
	public String getType() {
		return type;
	}
	public String getFolder() {
		return folder;
	}
	/**
	 * @return 멀티파트 파일 순번.
	 */
	public int getFileCount() {
		return fileCount;
	}

	public String getFieldName() {
		return fieldName;
	}

	public FileItemHeaders getHeaders() {
		return headers;
	}

	/**
	 * @return 스트림을 다 받고 (저장하고) 난 후에 알 수 있는 단위 파일 크기.
	 */
	public long getLength() {
		return length;
	}

	/**
	 * @see #getName()
	 * @return PersistenceManager에서 사용될 개별 파일 이름
	 */
	public String getCallName() {
		return callName;
	}

}
