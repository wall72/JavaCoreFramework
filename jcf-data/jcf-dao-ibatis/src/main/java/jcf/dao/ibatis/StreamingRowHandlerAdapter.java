package jcf.dao.ibatis;

import jcf.dao.StreamingRowHandler;

import com.ibatis.sqlmap.client.event.RowHandler;

public class StreamingRowHandlerAdapter implements RowHandler {

	private StreamingRowHandler streamingRowHandler;

	public StreamingRowHandlerAdapter(StreamingRowHandler streamingRowHandler) {
		this.streamingRowHandler = streamingRowHandler;
	}
	
	public void handleRow(Object valueObject) {
		streamingRowHandler.handleRow(valueObject);
	}

}
