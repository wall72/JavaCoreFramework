package jcf.sua.dataset;

import jcf.data.handler.StreamHandler;

/**
*
* 대용량 처리 채널과 데이터베이스의 각 행의 개별 연산을 처리하는 StreamHandler와의 변환을 지원한다.
*
* @author nolang
*
* @param <T>
*/
public abstract class AbstractDataSetStreamWriterStreamHandlerAdapter<T> implements StreamHandler<T> {

	protected DataSetStreamWriter streamWriter;

	public AbstractDataSetStreamWriterStreamHandlerAdapter(DataSetStreamWriter streamWriter) {
		this.streamWriter = streamWriter;
	}

	/**
	 *
	 * 스트리밍 채널을 연다.
	 *
	 * @param streamWriter
	 */
	public abstract void open(DataSetStreamWriter streamWriter);

	/**
	 *
	 * 스트리밍 채널을 닫는다.
	 *
	 * @param streamWriter
	 */
	public abstract void close(DataSetStreamWriter streamWriter);

	/**
	 *
	 * 조회된 각 행을 스트리밍 채널을 통해 클라이언트로 전송한다.
	 *
	 * @param streamWriter
	 * @param valueObject
	 */
	public abstract void handleRow(DataSetStreamWriter streamWriter, T valueObject);

	/**
	 * {@inheritDoc}
	 */
	public void open() {
		open(streamWriter);
	}

	/**
	 * {@inheritDoc}
	 */
	public void handleRow(T valueObject) {
		handleRow(streamWriter, valueObject);
	};

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		close(streamWriter);
	}

}
