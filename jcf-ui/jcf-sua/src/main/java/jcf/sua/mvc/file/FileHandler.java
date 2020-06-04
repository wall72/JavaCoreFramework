package jcf.sua.mvc.file;

import jcf.sua.SuaChannels;
import jcf.sua.mvc.file.operator.FileOperator;

/**
*
* 파일처리를 위한 각 채널별 FileOperator를 관리한다.
*
* @author nolang
*
*/
public interface FileHandler {

	/**
	 *
	 * 채널별 File 처리를 위한 Operator를 반환한다.
	 *
	 * @param channel
	 * @return
	 */
	FileOperator getFileOperator(SuaChannels channel);

}
