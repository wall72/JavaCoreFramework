package jcf.sua.mvc.file;

import java.util.HashMap;
import java.util.Map;

import jcf.sua.SuaChannels;
import jcf.sua.mvc.file.operator.FileOperator;

/**
*
* {@link FileHandler} 의 구현체
*
* @author nolang
*
*/
public class MciFileHandler implements FileHandler {

	private Map<SuaChannels, FileOperator> fileOperators = new HashMap<SuaChannels, FileOperator>();

	/**
	 * {@inheritDoc}
	 */
	public FileOperator getFileOperator(SuaChannels channel) {
		return fileOperators.get(channel);
	}

	/**
	 *
	 * MyBuilder 파일 처리를 위한 Operator 정의
	 *
	 * @param fileOperator
	 */
	public void setMybuilderFileOperator(FileOperator fileOperater){
		fileOperators.put(SuaChannels.MYBUILDER, fileOperater);
	}

	/**
	 *
	 * MiPlatform 파일 처리를 위한 Operator 정의
	 *
	 * @param fileOperator
	 */
	public void setMiplatformFileOperator(FileOperator fileOperator) {
		fileOperators.put(SuaChannels.MIPLATFORM, fileOperator);
	}

	/**
	 *
	 * XPlatform 파일 처리를 위한 Operator 정의
	 *
	 * @param fileOperator
	 */
	public void setXplatformFileOperator(FileOperator fileOperator) {
		fileOperators.put(SuaChannels.XPLATFORM, fileOperator);
	}
	
	/**
	 * Nexacro 파일 처리를 위한 Operator 정의
	 * @param fileOperator
	 */
	public void setNexacroFileOperator(FileOperator fileOperator){
		fileOperators.put(SuaChannels.NEXACRO, fileOperator);
	}

	/**
	 *
	 * 표준웹 파일 처리를 위한 Operator 정의
	 *
	 * @param fileOperator
	 */
	public void setWebFileOperator(FileOperator fileOperator) {
		fileOperators.put(SuaChannels.WEBFLOW, fileOperator);
	}

}
