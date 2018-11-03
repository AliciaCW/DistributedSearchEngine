package Prepare;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Callable;

public class DatabaseListenSocket implements Callable<String> {
	private Socket recvSocket;
	private Database database;

	public DatabaseListenSocket(Socket s,Database d) {
		recvSocket = s;
		database = d;
	}

	@Override
	public String call() {
		try {
			InputStream inputStream = recvSocket.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String temp = null;
			while ((temp = bufferedReader.readLine()) != null) {
				
				if(recvSocket.getLocalPort() == 9998) {
					System.out.println(
							"Get result from "+ recvSocket+ " " + temp);
					return temp;
				}
				else {
					System.out.println(recvSocket.getLocalPort() == 9998);
					System.out.println(
							"run() Get message from "+ recvSocket+ " " + temp);
					database.sendResult(database.searchRecvQuery(temp),recvSocket);
				}
			}
		} catch (SocketException s) {
			database.closeSocket(recvSocket);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return "hey";
	}

}
