package jcf.sua.dataset;

/**
 *
 * {@link AbstractDataSetStreamWriterStreamHandlerAdapter} 의 구현체
 *
 * @author nolang
 *
 * @param <T>
 */
public class DataSetStreamWriterStreamHandlerAdapter<T> extends AbstractDataSetStreamWriterStreamHandlerAdapter<T> {

	private String dataSetId;
	private int bufferSize;

	public DataSetStreamWriterStreamHandlerAdapter(DataSetStreamWriter streamWriter, String dataSetId, int bufferSize) {
		super(streamWriter);

		this.dataSetId = dataSetId;
		this.bufferSize = bufferSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void open(DataSetStreamWriter streamWriter) {
		streamWriter.startStream(dataSetId, bufferSize);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close(DataSetStreamWriter streamWriter) {
		streamWriter.endStream();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleRow(DataSetStreamWriter streamWriter, T valueObject) {
		streamWriter.addStreamData(valueObject);
	}
}
