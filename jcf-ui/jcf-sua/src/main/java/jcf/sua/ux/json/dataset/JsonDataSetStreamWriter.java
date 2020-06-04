package jcf.sua.ux.json.dataset;

import java.io.BufferedWriter;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import jcf.sua.dataset.DataSetStreamWriter;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * {@link DataSetStreamWriter}
 *
 * @author nolang
 *
 */
public class JsonDataSetStreamWriter implements DataSetStreamWriter {

	private HttpServletResponse response;
	private JsonGenerator jsonGenerator;
	private ObjectMapper mapper = new ObjectMapper();

	private String contentType = "application/json";
	private String charset = "utf-8";

	public JsonDataSetStreamWriter(HttpServletResponse response) {
		this.response = response;
	}

	/**
	 * {@inheritDoc}
	 */
	public void startStream(String dataSetid, int bufferSize) {
		response.setContentType(contentType);
		response.setCharacterEncoding(charset);

		try {
			BufferedWriter writer = new BufferedWriter(response.getWriter(), bufferSize);

			jsonGenerator = mapper.getJsonFactory().createJsonGenerator(writer);

			jsonGenerator.writeStartObject();
			jsonGenerator.writeArrayFieldStart(dataSetid);
		} catch (IOException e) {
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void addStreamData(Object data) {
		try {
			jsonGenerator.writeObject(data);
		} catch (IOException e) {
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void endStream() {
		try {
			jsonGenerator.writeEndArray();
			jsonGenerator.writeEndObject();
			jsonGenerator.flush();
			jsonGenerator.close();
		} catch (IOException e) {
		}
	}
}
