package jcf.sua.support.trace.element;


/**
 *
 * @author nolang
 *
 */
public class HttpRequestTraceElement implements TraceElement {

	private String handlerClassName;
	private String requestUri;
	private String params;
	private String contentTypeHeader;
	private String acceptHeader;

	private ElementBuilder esb = new ElementBuilder() {
		public String build() {
			StringBuilder sb = new StringBuilder();

			sb.append("호출 대상: ");
			sb.append(handlerClassName);
			sb.append(", 호출 경로: ");
			sb.append(requestUri);
			sb.append(", 요청 파라미터: ");
			sb.append(params);
			sb.append(", 요청 컨텐츠 타입: ");
			sb.append(contentTypeHeader);
			sb.append(", 응답 컨텐츠 타입: ");
			sb.append(acceptHeader);

			return sb.toString();
		}
	};

	public HttpRequestTraceElement(String handlerClassName, String acceptHeader,
			String contentTypeHeader, String requestUri, String params) {
		this.handlerClassName = handlerClassName;
		this.requestUri = requestUri;
		this.params = params;
		this.contentTypeHeader = contentTypeHeader;
		this.acceptHeader = acceptHeader;
	}

	public String getElementContents() {
		return esb.build();
	}

	public String getElementName() {
		return "Client Device Information";
	}
}
