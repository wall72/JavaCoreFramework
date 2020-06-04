package jcf.query.util;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 *
 * @author nolang
 *
 */
public class Lock {

	private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private static final ReadLock readLock = lock.readLock();
	private static final WriteLock writeLock = lock.writeLock();

	public static ReadLock getReadLock() {
		return readLock;
	}

	public static WriteLock getWriteLock() {
		return writeLock;
	}
}
