package jcf.sua;

public interface Message<T> {
	/**
	 * @return
	 */
	T getPayload();

	/**
	 * @return
	 */
	MessageHeaders getMessageHeaders();
}
