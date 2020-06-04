package jcf.data.handler;

import jcf.data.RowStatus;

/**
 *
 * @author 고강민
 *
 * @param <T>
 */
public interface RowTransactionCallback<T> {

	void doInRowTransaction(RowStatus rowStatus, T item);

}
