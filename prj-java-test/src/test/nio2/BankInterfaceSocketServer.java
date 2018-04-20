package test.nio2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Future;

public class BankInterfaceSocketServer {

	public static void main(String[] args) {

		try {
			AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open();
			InetSocketAddress hostAddress = new InetSocketAddress("localhost", 3030);
			
			serverSocketChannel.bind(hostAddress);
			System.out.println("Server channel bound to port: " + hostAddress.getPort());
			System.out.println("Waiting for client to connect... ");
			
 
			Future<AsynchronousSocketChannel> acceptResult = serverSocketChannel.accept();
			AsynchronousSocketChannel clientChannel = acceptResult.get();
			System.out.println("Messages from client: ");
			
			if (clientChannel != null && clientChannel.isOpen()) {
				while (true) {
					ByteBuffer buffer = ByteBuffer.allocate(32);
					Future<Integer> result = clientChannel.read(buffer);
					
					while (!result.isDone()) {
						System.out.println("result not done....");
						Thread.sleep(5000);
						// do nothing
					}
					buffer.flip();
					String message = new String(buffer.array(), StandardCharsets.UTF_8.name()).trim();
					System.out.println("message from client : " + message);
					
					/*if (message.equals("quit")) {
						break;
					}*/
					buffer.clear();
				}
			}
			
			//serverSocketChannel.close();

		} catch (IOException e) {

		} catch (Exception e) {

		}

	}

}
