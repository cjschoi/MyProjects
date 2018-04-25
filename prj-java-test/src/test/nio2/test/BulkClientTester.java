package test.nio2.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import test.nio2.BankInterfaceSocketClient;

public class BulkClientTester {

	public static void main(String[] args) throws IOException {

		int numberOfClient = 30;
		int numberOfMessage = 1000;
		
		ArrayList<BankInterfaceSocketClient> clients = new ArrayList<BankInterfaceSocketClient>();
		for(int i = 0; i < numberOfClient; i++) {
			BankInterfaceSocketClient client = new BankInterfaceSocketClient("cli_client_"+i,"localhost",3030);
			client.startClientDaemon();
			clients.add(client);			
		}
		
		for (int i = 0; i < numberOfMessage; i++) {
			int j = 0;
			
			for (BankInterfaceSocketClient client : clients) {
				try {
					Random random = new Random();
					String toSend = "client["+j+"] msg["+i+"] TEST_" + random.nextInt() + "_" + random.nextFloat();
					String result = client.sendMessage(toSend);
					System.out.println(toSend + "=" + " " + result.length() + "," + result);
				} catch (Exception e) {
					e.printStackTrace();
				}
				j++;
			}
			
		}
	}
}
