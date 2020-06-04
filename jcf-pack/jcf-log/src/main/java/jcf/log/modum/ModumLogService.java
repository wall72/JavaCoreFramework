package jcf.log.modum;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import jcf.log.BatchWriter;
import jcf.log.LogService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

/**
 * 큐를 이용하여 이벤트를 모아서 배치라이터로 보내준다.
 * <p>
 * 지정된 갯수 이상의 이벤트가 쌓이거나 이벤트 적립이 지정된 기간동안 없으면
 * 큐에 쌓인 이벤트들을 배치라이터로 보내고 큐를 비운다.
 *  
 * @author setq
 *
 */
public class ModumLogService<T> implements LogService<T>, SmartLifecycle {

	private static final Logger logger = LoggerFactory.getLogger(ModumLogService.class);
	
	private BatchWriter<T> batchWriter;
	private int thresold = 10;
	private long ms = 5000;
	private String threadName;
	private static int defaultThreadNameCounter = 0;
	private String DEFAULT_THREAD_NAME = "modumlog";
	private int phase = Integer.MAX_VALUE;
	
	public void setBatchWriter(BatchWriter<T> batchWriter) {
		this.batchWriter = batchWriter;
	}
	/**
	 * 큐에 쌓인 이벤트 수가 이를 넘어가면 비움 처리.
	 * @param thresold
	 */
	public void setThresold(int thresold) {
		this.thresold = thresold;
	}
	
	/**
	 * 여기서 지정된 기간(밀리초)를 넘어가면 비움 처리.
	 * @param ms
	 */
	public void setLatency(long ms) {
		this.ms = ms;
	}
	
	/**
	 * 쓰레드 이름을 지정해준다. 기본값은 modumlog
	 * @param threadName
	 */
	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}
	
	/**
	 * 스프링 스마트라이프사이클의 Phase 값 지정.
	 * 일반 라이프사이클은 0임.
	 * 음수값을 포함, 작은 값을 가지면 우선순위를 가지고 큰 값을 가지면 후순위가 됨.
	 * 예를 들어 후순위이면 시작할 때 늦게 시작하고 끝날 때 먼저 끝남. 
	 * @param phase
	 */
	public void setPhase(int phase) {
		this.phase = phase;
	}
	
	private BlockingQueue<T> queue = new LinkedBlockingQueue<T>();
	
	private volatile boolean running;


	private Thread thread;


	public void sendEvent(T event) {
		try {
			queue.put(event);

		} catch (InterruptedException e) {
			logger.warn("interrupted while putting event to queue", e);
		}
	}
	
	private Runnable getRunnable() {
		return new Runnable() {

			private int errorCount = 0;
			private int warnCount = 1;
			
			public void run() {
				while (running) {
					try {
						Thread.sleep(ms);

					} catch (InterruptedException e) {
						logger.warn("logger interrupted, ignoring.", e);
					}
						
					flush();
				}
				
				/*
				 * on thread shutdown.
				 */
				flush();
			}
			
			private List<T> events = new ArrayList<T>();
			
			private void flush() {
				
				int transferred;
				
				do {
					transferred = queue.drainTo(events, thresold);
					
					if (transferred == 0) {
						break;
					}

					writeEmbracingThrowable(events);
					events.clear();
					
				} while (transferred == thresold);
				
			}

			private void writeEmbracingThrowable(List<T> events) {
				try {
					batchWriter.write(events);
					errorCount = 0;
					warnCount = 1;
					
				} catch (Throwable t) {
					errorCount++;
					
					if (errorCount == warnCount) {
						logger.warn("exception while writing log in batch.", t);
						warnCount *= 2;
					}

				}
			}
			
		};
	}
	public void start() {
		if (threadName == null) {
			defaultThreadNameCounter++;
			threadName = DEFAULT_THREAD_NAME + defaultThreadNameCounter;
		}
		try {
			thread = new Thread(getRunnable(), threadName);
			thread.setPriority(Thread.NORM_PRIORITY + 1);

			running = true;
			logger.info("starting eventLogService {}", threadName);
			thread.start();
			
		} catch (Exception e) {
			logger.warn("error starting eventLogService {}", threadName, e);
		}
	}
	
	public void stop() {
		try {
			running = false;
			thread.join();
			logger.info("stopped eventLogService {}", threadName);
			
		} catch (Exception e) {
			logger.warn("error stopping eventLogService {}", threadName, e);
		}
	}
	public boolean isRunning() {
		return running;
	}
	public int getPhase() {
		return phase ;
	}
	public boolean isAutoStartup() {
		return true;
	}
	public void stop(Runnable callback) {
		stop();
		callback.run();
	}

}
