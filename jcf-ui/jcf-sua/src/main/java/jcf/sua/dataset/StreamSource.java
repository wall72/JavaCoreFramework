package jcf.sua.dataset;


public interface StreamSource<T> {

	void read(AbstractDataSetStreamWriterStreamHandlerAdapter<T> streamHandler);

}
