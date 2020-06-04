package jcf.upload.persistence;

import java.io.InputStream;

import jcf.upload.FileInfo;


/**
 * 파일 저장소 관리.
 * <p>
 */
public interface PersistenceManager {

	/**
	 * 폴더 삭제.
	 * @param folder
	 */
	void delete(String folder);

	/**
	 * 폴더 안의 개별 파일 삭제.
	 * @param folder
	 * @param fileName
	 */
	void deleteFile(String folder, String fileName);

	/**
	 * 파일 저장 및 파일 정보 반환.
	 *
	 * @param folder
	 * @param fileName
	 * @param fileInfo
	 * @param stream
	 * @return fileLength, 기타 생성 데이터 모델 포함.
	 */
	FileInfo save(String folder, String fileName, FileInfo fileInfo,  InputStream stream);

	/**
	 * 파일 송신을 위한 파일로더 객체 생성.
	 *
	 * @param folder
	 * @param fileName
	 * @return 파일 송신을 위한 파일로더
	 */
	FileLoader getFileLoader(String folder, String fileName);

}
