package jcf.sua.mvc;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcf.sua.SuaChannels;
import jcf.sua.dataset.DataSetReader;
import jcf.sua.dataset.DataSetStreamWriter;
import jcf.sua.dataset.DataSetWriter;

/**
*
* 채널 기반 요청 처리를 위해 사용되는 임시 저장소
*
* @author nolang
*
*/
public class MciRequestContext {

	private SuaChannels mciChannelType;
	private boolean isMciRequest;
	private DataSetReader reader;
	private DataSetWriter writer;
	private DataSetStreamWriter streamWriter;
	private MciDataSetAccessor accessor;

	private HttpServletRequest httpServletRequest;
	private HttpServletResponse httpServletResponse;

	private Map<String, Object> namedParameter = new HashMap<String, Object>();

	/**
	 *
	 * 채널 기반 요청인지 반환
	 *
	 * @return
	 */
	public boolean isMciRequest() {
		return isMciRequest;
	}

	/**
	 *
	 * 채널 기반 요청인지를 설정한다.
	 *
	 * @param isMciRequest
	 */
	public void setMciRequest(boolean isMciRequest) {
		this.isMciRequest = isMciRequest;
	}

	/**
	 *
	 * 상향 채널을 반환한다.
	 *
	 * @return
	 */
	public DataSetReader getDataSetReader() {
		return reader;
	}

	/**
	 *
	 * 상향 채널을 설정한다.
	 *
	 * @param reader
	 */
	public void setDataSetReader(DataSetReader reader) {
		this.reader = reader;
	}

	/**
	 *
	 * 하향 채널을 반환한다.
	 *
	 * @return
	 */
	public DataSetWriter getDataSetWriter() {
		return writer;
	}

	/**
	 *
	 * 하향 채널을 설정한다.
	 *
	 * @param writer
	 */
	public void setDataSetWriter(DataSetWriter writer) {
		this.writer = writer;
	}

	/**
	 *
	 * 대용량 데이터 처리를 위한 채널을 반환한다.
	 *
	 * @return
	 */
	public DataSetStreamWriter getDataSetStreamWriter() {
		return streamWriter;
	}

	/**
	 *
	 * 대용량 데이터 처리를 위한 채널을 설정한다.
	 *
	 * @param streamWriter
	 */
	public void setDataSetStreamWriter(DataSetStreamWriter streamWriter) {
		this.streamWriter = streamWriter;
	}

	/**
	 *
	 * 클라이언트로 전송될 데이터를 반환한다.
	 *
	 * @return
	 */
	public MciDataSetAccessor getDataSetAccessor() {
		return accessor;
	}

	/**
	 *
	 * 클라이언트로 전송할 데이터를 설정한다.
	 *
	 * @param accessor
	 */
	public void setDataSetAccessor(MciDataSetAccessor accessor) {
		this.accessor = accessor;
	}

	/**
	 *
	 * HttpServletRequest 를 반환한다.
	 *
	 * @return
	 */
	public HttpServletRequest getHttpServletRequest() {
		return httpServletRequest;
	}

	/**
	 *
	 * HttpServletRequest를 설정한다.
	 *
	 * @param httpServletRequest
	 */
	public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
		this.httpServletRequest = httpServletRequest;
	}

	/**
	 *
	 * HttpServletResponse 를 반환한다.
	 *
	 * @return
	 */
	public HttpServletResponse getHttpServletResponse() {
		return httpServletResponse;
	}

	/**
	 *
	 * HttpServletResponse를 설정한다.
	 *
	 * @param httpServletResponse
	 */
	public void setHttpServletResponse(HttpServletResponse httpServletResponse) {
		this.httpServletResponse = httpServletResponse;
	}

	/**
	 *
	 * 요청을 처리하기 위해 설정된 채널의 타입을 반환한다.
	 *
	 * @return
	 */
	public SuaChannels getMciChannelType() {
		return mciChannelType;
	}

	/**
	 *
	 * 요청을 처리할 채널을 설정한다.
	 *
	 * @param mciChannelType
	 */
	public void setMciChannelType(SuaChannels mciChannelType) {
		this.mciChannelType = mciChannelType;
	}

	/**
	 *
	 * 채널 기반 요청 처리중 발생한 임시 데이터를 저장한다.
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public MciRequestContext addNamedParameter(String key, Object value) {
		this.namedParameter.put(key, value);
		return this;
	}

	/**
	 *
	 * 채널 기반 요청 처리중 발생한 임시 데이터를 반환한다.
	 *
	 * @param key
	 * @return
	 */
	public Object getNamedParameter(String key) {
		return this.namedParameter.get(key);
	}
}
