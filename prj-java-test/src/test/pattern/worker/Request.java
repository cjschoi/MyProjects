package test.pattern.worker;

import java.util.Random;

public class Request {

	private final String name;
	private final int number;
	private static final Random random = new Random();
	
	public Request(final String name, final int number) {
		this.name = name;
		this.number = number;
	}
	
	public void execute() {
		System.out.println(Thread.currentThread().getName() + " executes " + this);
		try {
			Thread.sleep(random.nextInt(1000));
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
