package jcf.sua.support.trace;

import java.util.LinkedHashSet;
import java.util.Set;

import jcf.sua.support.trace.element.TraceElement;

/**
 * 웹 요청을 기준으로 사용자의 요청 이력 처리 정보를 유지하는 클래스
 *
 */
public class Trace {

	private String transactionId;
	private long startTime;
	private long endTime;

	private Set<TraceElement> elements;

	public Trace(String transactionId, long startTime) {
		this.transactionId = transactionId;
		this.startTime = startTime;

		setElement(new LinkedHashSet<TraceElement>());
	}

	public String getTransactionId() {
		return transactionId;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long calculateDuration() {
		return endTime - startTime;
	}

	public void addElement(TraceElement te) {
		elements.add(te);
	}

	public void setElement(Set<TraceElement> elements) {
		this.elements = elements;
	}

	public Set<TraceElement> getElements() {
		return elements;
	}
}
