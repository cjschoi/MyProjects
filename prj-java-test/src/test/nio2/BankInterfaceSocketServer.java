package test.nio2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AcceptPendingException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import test.nio2.util.NamedThreadFactory;

public class BankInterfaceSocketServer implements Runnable {

	private AsynchronousChannelGroup asyncChannelGroup;
	private String name;
	private AsynchronousServerSocketChannel asyncServerSocketChannel;
	
	//public final static int READ_MESSAGE_WAIT_TIME = 15;
	public final static int MESSAGE_INPUT_SIZE= 1024;
	
	public BankInterfaceSocketServer(String name) throws IOException, InterruptedException, ExecutionException {
    	this.name = name;
		//asyncChannelGroup = AsynchronousChannelGroup.withThreadPool(
		//		Executors.newFixedThreadPool(50, new NamedThreadFactory(name + "_Group_Thread")));
		
		asyncChannelGroup = AsynchronousChannelGroup.withCachedThreadPool(
				Executors.newFixedThreadPool(500), 20);
	}	
	
	public String getName() {
		return this.name;
	}
    
	public void open(InetSocketAddress serverAddress) throws IOException {
   		// open a server channel and bind to a free address, then accept a connection
    	System.out.println("Opening Aysnc ServerSocket channel at " + serverAddress);
   		asyncServerSocketChannel = AsynchronousServerSocketChannel.open(asyncChannelGroup).bind(
   				serverAddress);
   		asyncServerSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, MESSAGE_INPUT_SIZE);
   		asyncServerSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);

		System.out.println("Server channel bound to port: " + serverAddress.getPort());
		System.out.println("Waiting for client to connect... ");
    }
	
	public void run() {
		try {
			if (asyncServerSocketChannel.isOpen()) {
				
				asyncServerSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
	
					@Override
					public void completed(AsynchronousSocketChannel asyncSocketChannel, Object attachment) {
						// TODO Auto-generated method stub
						if (asyncServerSocketChannel != null && asyncServerSocketChannel.isOpen()) {
							asyncServerSocketChannel.accept(null, this);
						}
						handleAcceptConnection(asyncSocketChannel);
					}
	
					@Override
					public void failed(Throwable exc, Object attachment) {
						// TODO Auto-generated method stub
						if (asyncServerSocketChannel.isOpen()) {
							asyncServerSocketChannel.accept(null, this);
							System.out.println("***********" + exc  + " statement=" + attachment);
						}
					}
				});
				System.out.println("Server "+ getName() + " reading to accept first connection...");
				
				
				/////////
				/*
				Future<AsynchronousSocketChannel> acceptResult = asyncServerSocketChannel.accept();
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
						
						//if (message.equals("quit")) {
						//	break;
						//}
						buffer.clear();
					}
				}*/
				
				//serverSocketChannel.close();				
			} 
		} catch (AcceptPendingException ex) {
    		ex.printStackTrace();
    	}	
	}
	
    public void stopServer() throws IOException {
    	System.out.println(">>stopingServer()...");
		this.asyncServerSocketChannel.close();
		this.asyncChannelGroup.shutdown();  	
    }
	
    private void handleAcceptConnection(AsynchronousSocketChannel asyncSocketChannel)   {	
    	System.out.println(">>handleAcceptConnection(), asyncSocketChannel=" +asyncSocketChannel);
		ByteBuffer messageByteBuffer = ByteBuffer.allocate(MESSAGE_INPUT_SIZE);
		try {
			// read a message from the client, timeout after 10 seconds
			Future<Integer> futureReadResult = asyncSocketChannel.read(messageByteBuffer);
			//futureReadResult.get(READ_MESSAGE_WAIT_TIME, TimeUnit.SECONDS);
			futureReadResult.get();
		
			String clientMessage = new String(messageByteBuffer.array(), StandardCharsets.UTF_8.name()).trim();  
			
			messageByteBuffer.clear();
			messageByteBuffer.flip();
         
			String responseString = "echo" + "_" + clientMessage;
			messageByteBuffer = ByteBuffer.wrap((responseString.getBytes()));
			Future<Integer> futureWriteResult = asyncSocketChannel.write(messageByteBuffer);
			//futureWriteResult.get(READ_MESSAGE_WAIT_TIME, TimeUnit.SECONDS);
			futureWriteResult.get();
			
			if (messageByteBuffer.hasRemaining()) {
				messageByteBuffer.compact();
			} else {
				messageByteBuffer.clear();
			}        
		//} catch (InterruptedException | ExecutionException | TimeoutException | CancellationException | UnsupportedEncodingException e) {
		} catch (InterruptedException | ExecutionException | CancellationException | UnsupportedEncodingException e) {	
			e.printStackTrace(); 
		} finally {
			try {
				asyncSocketChannel.close();
			} catch (IOException ioEx) {
				System.out.println(ioEx);
			}
		}
    }
    

    
	/*public static void main(String[] args) throws Exception {
		BankInterfaceSocketServer server = new BankInterfaceSocketServer("SERVER");
		InetSocketAddress serverAddress = new InetSocketAddress("localhost", 3030);
		server.open(serverAddress);
		server.runServer();
		Thread.currentThread().join();
	}*/

}
