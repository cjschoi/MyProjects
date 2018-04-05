package test.pattern.worker;


/**
 * @author cjs
 * Client 역할로부터 Request 역할을 받아 Worker 역할에 건넵니다.
 */
public class Channel {
	
	private static final int MAX_REQUESTS = 100;
	private final Request[] requestQueue;
	private int tail;
	private int head;
	private int count;
	
	private final WorkerThread[] threadPool;
	
	public Channel(final int threads) {
		this.requestQueue = new Request[MAX_REQUESTS];
		this.tail = 0;
		this.head = 0;
		this.count = 0;
		
		threadPool = new WorkerThread[threads];
		for(int i = 0; i < threadPool.length; i++) {			
			threadPool[i] = new WorkerThread("Worker-" + i, this);
		}
	}

	public void startWorkers() {
		for(int i = 0; i < threadPool.length; i++) {
			threadPool[i].start();
		}
	}
	
	public synchronized void putRequest(final Request request) {
		while(count >= requestQueue.length) {
			try {
				wait();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		requestQueue[tail] = request;
		tail = (tail + 1) % requestQueue.length;
System.out.println("@tail => "+tail);		
		count++;
		notifyAll();
	}
	
	public synchronized Request takeRequest() {
		while(count <= 0) {
			try {
				wait();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		Request request = requestQueue[head];
		head = (head + 1) % requestQueue.length;
System.out.println("@head => "+head);		
		count--;
		notifyAll();
		return request;
	}
}
