package jcf.log;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.common.base.Optional;

public class SampleBatchWriter<T extends SampleLogEvent> implements
		BatchWriter<T> {

	Queue<Integer> q = new ConcurrentLinkedQueue<Integer>();
	
	public void write(List<T> logEvents) {
		q.add(logEvents.size());
	}
	
	public  Optional<Integer> pop() {
		return Optional.fromNullable(q.poll());
	}

}
