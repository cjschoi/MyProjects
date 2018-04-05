package test.pattern.worker;

public class _Tester {

	public static void main(String[] args) {
		  
		// Worker ����
		final Channel channel = new Channel(5);
		channel.startWorkers();
		// Client ����
		new ClientThread("aaa", channel).start();
		new ClientThread("bbb", channel).start();
		new ClientThread("ccc", channel).start();
	}

}
