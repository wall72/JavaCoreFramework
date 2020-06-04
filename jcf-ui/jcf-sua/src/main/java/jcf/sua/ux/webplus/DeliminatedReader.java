package jcf.sua.ux.webplus;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import jcf.sua.exception.MciException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeliminatedReader {

	private static final Logger logger = LoggerFactory
			.getLogger(DeliminatedReader.class);

	private boolean eof;
	private InputStreamReader reader;
	private char delimeter;

	public DeliminatedReader(InputStream in, String charset, char delimeter) {
		try {
			this.reader = new InputStreamReader(in, charset);

		} catch (UnsupportedEncodingException e) {
			throw new MciException("charset " + charset);
		}
		this.delimeter = delimeter;
	}

	public String read(){
		if (eof) {
			return null;
		}
		StringBuilder sb = new StringBuilder();

		int read;
		while (!eof) {
			try {
				read = reader.read();

			} catch (IOException e) {
				throw new MciException("error reading inputstream", e);
			}

			if (read == -1) {
				eof = true;
				break;

			} else if (read == delimeter) {
				break;

			} else {
				sb.append((char) read);
			}
		}

		String ret = sb.toString();

		logger.debug("reading deliminated string : '{}'", ret);

		return ret;
	}

	public boolean isAvailable() {
		return !eof;
	}
}