package test.nio2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AcceptPendingException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import test.nio2.util.RetryUtil;

/**
 * Thread SocketClient
 * 
 * @author cjs
 *
 */
public class BankInterfaceSocketClientDaemon implements Runnable {

	private AsynchronousChannelGroup asyncChannelGroup;
	private String name;
	private AsynchronousServerSocketChannel asyncServerSocketChannel;
	
	
	
	
	private String serverName = null;
	private int port;
	private String clientName;
	
	
	public final static int READ_MESSAGE_WAIT_TIME = 30;
	//private AsynchronousSocketChannel connectToServer(int waitTime)
	
	public final static int MESSAGE_INPUT_SIZE= 128;
	
	//private final static int WAIT_TIME = 3;
	
	public BankInterfaceSocketClientDaemon(String name) throws IOException {
    	this.name = name;		
		asyncChannelGroup = AsynchronousChannelGroup.withCachedThreadPool(
				Executors.newFixedThreadPool(50), 20);
	}
	
	public String getName() {
		return this.name;
	}
	
	/* connection open */
	/**
	 * ���� ä�� open. ���� ��û�� Waiting....     
	 * 
	 * @param serverAddress
	 * @throws IOException
	 */
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
	
	@Override
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
	
    /**
     * ��ݿ�û
     *   ������ EAI ������ ��û
     *   ��� ��ȸ
     * 
     * @param asyncSocketChannel
     */
    private void handleAcceptConnection(AsynchronousSocketChannel asyncSocketChannel)   {	
    	System.out.println(">>handleAcceptConnection(), asyncSocketChannel=" +asyncSocketChannel);
		ByteBuffer messageByteBuffer = ByteBuffer.allocate(MESSAGE_INPUT_SIZE);
		try {
			/**
			 *  �����б�
			 */
			// read a message from the client, timeout after 10 seconds
			Future<Integer> futureReadResult = asyncSocketChannel.read(messageByteBuffer);
			futureReadResult.get(READ_MESSAGE_WAIT_TIME, TimeUnit.SECONDS);
			//futureReadResult.get();
		
			String clientMessage = new String(messageByteBuffer.array(), StandardCharsets.UTF_8.name()).trim();  // ����
			
			/**
			 *  ������ EAI ������ ��û
			 */			
			{
				//.............
				String result = retrySendMessage(clientMessage);
			}
			
			/**
			 * �����ȸ
			 */
			
			/////////////////////////
			
			
			//messageByteBuffer.clear();
			messageByteBuffer.flip();
			
			String responseString = "echo" + "_" + clientMessage;
			messageByteBuffer = ByteBuffer.wrap((responseString.getBytes()));
			Future<Integer> futureWriteResult = asyncSocketChannel.write(messageByteBuffer);
			futureWriteResult.get(READ_MESSAGE_WAIT_TIME, TimeUnit.SECONDS);
			//futureWriteResult.get();
			
			if (messageByteBuffer.hasRemaining()) {
				messageByteBuffer.compact();
			} else {
				messageByteBuffer.clear();
			}        
		} catch (InterruptedException | ExecutionException | TimeoutException | CancellationException | UnsupportedEncodingException e) {
			e.printStackTrace(); 
		} finally {
			try {
				asyncSocketChannel.close();
			} catch (IOException ioEx) {
				System.out.println(ioEx);
			}
		}
    }
	
///////////////////////////////	
	

//||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||	
//|||| retry ���� connenctToServer |||||||||||||||||||||||||||||||||||||||||||||||||||< Start
	// retryConnectToServer callable
    private Callable<AsynchronousSocketChannel> connectWork = () -> {
    	System.out.println("callable..");
		AsynchronousSocketChannel asyncSocketChannel = AsynchronousSocketChannel.open();
		Future<Void> connectFuture = null;

		// Connecting to server
		System.out.println("Connecting to server... " + serverName + ",port=" + port);
		//connectFuture = asyncSocketChannel.connect(new InetSocketAddress("cjs-PC", this.port));
		
		//@ TODO EAI���� url �ޱ� 
		connectFuture = asyncSocketChannel.connect(new InetSocketAddress(InetAddress.getLocalHost().getHostName(), this.port)); 

		// You have two seconds to connect. This will throw exception if server
		// is not there.
		//connectFuture.get(READ_MESSAGE_WAIT_TIME, TimeUnit.SECONDS);
		connectFuture.get();

		//asyncSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 128 * MESSAGE_INPUT_SIZE);
		//asyncSocketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 128 * MESSAGE_INPUT_SIZE);
		//asyncSocketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

		return asyncSocketChannel; 
    };
	
	/* retry�������� connect */
    // EAI������ connect
	private AsynchronousSocketChannel retryConnectToServer() 
			throws Exception {
		AsynchronousSocketChannel asyncSocketChannel = RetryUtil.runWithRetry(connectWork);
		return asyncSocketChannel;
	}
//|||| retry ���� connenctToServer |||||||||||||||||||||||||||||||||||||||||||||||||||> End	
//||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||	
	
	
	
//||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||	
//|||| retry ���� sendMessage |||||||||||||||||||||||||||||||||||||||||||||||||||< Start
	public String retrySendMessage(String request) {
		System.out.println(">> sendMessage(request=" + request + ")");
		String response = null;
		AsynchronousSocketChannel asyncSocketChannel = null;
		try {
			//asyncSocketChannel = connectToServer(WAIT_TIME);
			// EAI������ connect
			asyncSocketChannel = retryConnectToServer();
			
			// EAI������ ���������ϱ� ���Ͽ� ä�ο� ����
			ByteBuffer messageByteBuffer = ByteBuffer.wrap(request.getBytes());
			Future<Integer> futureWriteResult = asyncSocketChannel.write(messageByteBuffer);
			futureWriteResult.get();
			
			// EAI�����κ��� �������۰�� �ޱ�
			//  1.	���� �ڱ� ��ü ��û (���μ��� ���̵� 1.5)
			//	2.	1.6���� ������ ���Ͽ� 30�� ��� �� �� ���� �� 20�� �������� 3ȸ �� ���� �ڱ� ��ü ��� ��ȸ
			//	3.	2�ܰ���� ���� ���� �� ������ notification �߻�
			//@ TODO retry���� ����
			
			//Now wait for return message.
			ByteBuffer returnMessage = ByteBuffer.allocate(MESSAGE_INPUT_SIZE);
			Future<Integer> futureReadResult = asyncSocketChannel.read(returnMessage);
     		futureReadResult.get();
			response = new String(returnMessage.array(), StandardCharsets.UTF_8.name());
			
			System.out.println("received response=" + response);
			
			messageByteBuffer.clear();
		} catch (InterruptedException | ExecutionException | IOException e ) {
			handleException(e);
		} catch (Exception e) {
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
//|||| retry ���� sendMessage |||||||||||||||||||||||||||||||||||||||||||||||||||> End	
//||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
	
	
	/*public void startClientDaemon() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				//startClient();
			}
		}).start();
	}*/
	
	private void handleException(Exception e) {
		e.printStackTrace();
		throw new RuntimeException(e);
	}
	 

}
