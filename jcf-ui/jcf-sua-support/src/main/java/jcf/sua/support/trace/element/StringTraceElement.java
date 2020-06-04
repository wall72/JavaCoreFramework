package jcf.sua.support.trace.element;

/**
 *
 * @author nolang
 *
 */
public class StringTraceElement implements TraceElement {

	private String name;
	private String contents;

	public StringTraceElement(String name, String contents) {
		this.name = name;
		this.contents = contents;
	}

	public String getElementContents() {
		return contents;
	}

	public String getElementName() {
		return name;
	}

}
