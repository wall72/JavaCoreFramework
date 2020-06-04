package jcf.query.file;

import jcf.query.file.reader.Reader;
import jcf.query.file.writer.Writer;

/**
 *
 * @author nolang
 *
 */
public class FileExecutor {

	public <T> Reader<T> getReader(String fileName, Class<T> clazz)	{
		return new Reader<T>() {
			public T read() {
				return null;
			}
		};
	}

	public <T> Writer<T> getWriter(String fileName, Class<T> clazz) {
		return new Writer<T>() {
			public void write(T object) {};
		};
	}
}
