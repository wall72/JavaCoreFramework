package jcf.sua.mvc.file;

import jcf.upload.FileInfo;
import jcf.upload.persistence.PersistenceManager;


/**
 *
 * {@link PersistenceManager} 의 확장 구현체
 *
 * @author nolang
 *
 */
public interface MciPersistenceManager extends PersistenceManager {


	/**
	 * 파일 이동
	 *
	 * @param sourceFolder
	 * @param sourceFileName
	 * @param targetFolder
	 * @param targetFileName
	 * @return
	 */
	FileInfo moveFile(String sourceFolder, String sourceFileName, String targetFolder, String targetFileName);


}
