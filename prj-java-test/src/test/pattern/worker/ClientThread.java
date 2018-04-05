package test.pattern.worker;

import java.util.Random;

/**
 * @author cjs
 * 업무를 작성하여 Channel 역할에게 일을 전달
 */
public class ClientThread extends Thread {

	private final Channel channel;
	private final static Random random = new Random();  
	
	public ClientThread(final String name, final Channel channel) {
		super(name);
		this.channel = channel;
	}
	
	@Override
	public void run() {
		try {
			for (int i = 0; true; i++) {
				final Request request = new Request(getName(), i);
				channel.putRequest(request);
				Thread.sleep(random.nextInt(1000));
			}
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
