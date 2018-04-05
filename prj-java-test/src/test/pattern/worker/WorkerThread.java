package test.pattern.worker;

import test.pattern.worker.Channel;	


public class WorkerThread extends Thread {

	private final Channel channel;
	
	public WorkerThread(final String name, final Channel channel) {
		super(name);
		this.channel = channel;
	}
	
	@Override
	public void run() {
		while(true) {
			final Request request = this.channel.takeRequest();
			request.execute();
		}
	}
	
}
