package test.nio2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Future;

public class BankInterfaceSocketClient {

	public static void main(String[] args) {

		try {
	        AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
	        InetSocketAddress hostAddress = new InetSocketAddress("localhost", 3030);
	        Future<Void> future = client.connect(hostAddress);
	        future.get(); // returns null
	
	        System.out.println("Client is started: " + client.isOpen());
	        System.out.println("Sending messages to server: ");
			
	        String [] messages = new String [] {"Time goes fast.", "What now?", "quit"};
			
	        for (int i = 0; i < messages.length; i++) {
			
	            byte [] message = new String(messages [i]).getBytes(StandardCharsets.UTF_8.name());
	            ByteBuffer buffer = ByteBuffer.wrap(message);
	            Future<Integer> result = client.write(buffer);
			
	            while (!result.isDone()) {
	                System.out.println("... ");
	            }
			
	            System.out.println(messages [i]);
	            buffer.clear();
	            Thread.sleep(10000);
			} // for
			
			client.close();

		} catch (IOException e) {

		} catch (Exception e) {

		}

	}

}
