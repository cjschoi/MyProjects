package test.nio2.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import test.nio2.BankInterfaceSocketClient;

public class BulkClientTester {

	public static void main(String[] args) throws IOException {

		int numberOfClient = 10;
		int numberOfMessage = 100;
		
		ArrayList<BankInterfaceSocketClient> clients = new ArrayList<BankInterfaceSocketClient>();
		for(int i = 0; i < numberOfClient; i++) {
			BankInterfaceSocketClient client = new BankInterfaceSocketClient("cli_client_"+i,"localhost",3030);
			clients.add(client);			
		}
		
		for (int i = 0; i < numberOfMessage; i++) {
			int j = 0;
			
			for (BankInterfaceSocketClient client : clients) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							
							Random random = new Random();
							
							String toSend = "TEST_" + random.nextInt() + "_" + random.nextFloat();
							//Thread.sleep(100);
							String result = client.sendMessage(toSend);
							System.out.println(toSend + "=" + " " + result.length() + "," + result);
						} catch (Exception e) {
							e.printStackTrace();
						}		
					}
				}).start();	
			}
			j++;
		}
	}
}
