package test.nio2;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

public class BankInterfaceSocketClient {

	private String serverName = null;
	private int port;
	private String clientName;

	public final static int MESSAGE_INPUT_SIZE= 128;
	
	//private final static int WAIT_TIME = 3;
	
	public BankInterfaceSocketClient(String clientName, String serverName, int port) throws IOException {
		System.out.println(
				">>AsynCounterClient(clientName=" + clientName + ",serverName=" + serverName + ",port=" + port + ")");
		this.clientName = clientName;
		this.serverName = serverName;
		this.port = port;
	}

	//private AsynchronousSocketChannel connectToServer(int waitTime)
	private AsynchronousSocketChannel connectToServer()
			throws IOException, InterruptedException, ExecutionException, TimeoutException {
		AsynchronousSocketChannel asyncSocketChannel = AsynchronousSocketChannel.open();
		Future<Void> connectFuture = null;

		// Connecting to server
		System.out.println("Connecting to server... " + serverName + ",port=" + port);
		//connectFuture = asyncSocketChannel.connect(new InetSocketAddress("cjs-PC", this.port));
		connectFuture = asyncSocketChannel.connect(new InetSocketAddress(InetAddress.getLocalHost().getHostName(), this.port));

		// You have two seconds to connect. This will throw exception if server
		// is not there.
		//connectFuture.get(waitTime, TimeUnit.SECONDS);
		connectFuture.get();

		//asyncSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 128 * MESSAGE_INPUT_SIZE);
		//asyncSocketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 128 * MESSAGE_INPUT_SIZE);
		//asyncSocketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

		return asyncSocketChannel;
	}
	
	/*public void connectSendMessage(String request)
			throws IOException, InterruptedException, ExecutionException, TimeoutException {
		AsynchronousSocketChannel asyncSocketChannel = AsynchronousSocketChannel.open();
		
		// Connecting to server
		System.out.println("Connecting to server... " + serverName + ",port=" + port);
		
		String message = "";
		
		asyncSocketChannel.connect(new InetSocketAddress(InetAddress.getLocalHost().getHostName(), this.port), null, new CompletionHandler<Void, Object>() {
			@Override
			public void completed(Void result, Object attachment) {
				// TODO Auto-generated method stub
				try {
					ByteBuffer messageByteBuffer = ByteBuffer.wrap(request.getBytes());
					Future<Integer> futureWriteResult = asyncSocketChannel.write(messageByteBuffer);
					futureWriteResult.get();
		        
					//Now wait for return message.
					ByteBuffer returnMessage = ByteBuffer.allocate(MESSAGE_INPUT_SIZE);
					Future<Integer> futureReadResult = asyncSocketChannel.read(returnMessage);
		     		futureReadResult.get();
		     		String response = new String(returnMessage.array(), StandardCharsets.UTF_8.name());
					
					System.out.println("received response=" + response);
					
					messageByteBuffer.clear();
					
				} catch (InterruptedException | ExecutionException | IOException e) {
					handleException(e);
				} finally {
					if (asyncSocketChannel.isOpen()) {
						try {
							asyncSocketChannel.close();
						} catch (IOException e) {
							// Not really anything we can do here.
							e.printStackTrace();
						}
					}
				}
			}

			@Override
			public void failed(Throwable exc, Object attachment) {
				// TODO Auto-generated method stub
				System.out.println("FAILED");
                exc.printStackTrace();
			}
			
		});
	}*/
	

	public String sendMessage(String request) {
		System.out.println(">> sendMessage(request=" + request + ")");
		String response = null;
		AsynchronousSocketChannel asyncSocketChannel = null;
		try {
			//asyncSocketChannel = connectToServer(WAIT_TIME);
			asyncSocketChannel = connectToServer();
			ByteBuffer messageByteBuffer = ByteBuffer.wrap(request.getBytes());
			Future<Integer> futureWriteResult = asyncSocketChannel.write(messageByteBuffer);
			futureWriteResult.get();
        
			//Now wait for return message.
			ByteBuffer returnMessage = ByteBuffer.allocate(MESSAGE_INPUT_SIZE);
			Future<Integer> futureReadResult = asyncSocketChannel.read(returnMessage);
     		futureReadResult.get();
			response = new String(returnMessage.array(), StandardCharsets.UTF_8.name());
			
			System.out.println("received response=" + response);
			
			messageByteBuffer.clear();
		} catch (InterruptedException | ExecutionException | TimeoutException | IOException e) {
			handleException(e);
		} finally {
			if (asyncSocketChannel.isOpen()) {
				try {
					asyncSocketChannel.close();
				} catch (IOException e) {
					// Not really anything we can do here.
					e.printStackTrace();
				}
			}
		}
		return response;
	}
	
	private void handleException(Exception e) {
		e.printStackTrace();
		throw new RuntimeException(e);
	}
	
	
/*	
	 public static void main(String[] args) {
	 
	 try { AsynchronousSocketChannel client =
	 AsynchronousSocketChannel.open(); InetSocketAddress hostAddress = new
	  InetSocketAddress("localhost", 3030); Future<Void> future =
	  client.connect(hostAddress); future.get(); // returns null
	  
	  System.out.println("Client is started: " + client.isOpen());
	  System.out.println("Sending messages to server: ");
	  
	  String [] messages = new String [] {"Time goes fast.", "What now?",
	  "quit"};
	  
	  for (int i = 0; i < messages.length; i++) {
	  
	  byte [] message = new String(messages
	  [i]).getBytes(StandardCharsets.UTF_8.name()); ByteBuffer buffer =
	  ByteBuffer.wrap(message); Future<Integer> result = client.write(buffer);
	  
	  while (!result.isDone()) { System.out.println("... "); }
	  
	  System.out.println(messages [i]); buffer.clear(); Thread.sleep(10000); }
	  // for
	  
	  client.close();
	  
	  } catch (IOException e) {
	  
	  } catch (Exception e) {
	  
	  }
	  
	  }
*/	 

}
