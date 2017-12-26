package Step3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class TCPConnection extends Thread {
	private int port;
	private int name;
	private int fname;
	private InetAddress address; 
	private InetAddress faddress;
	private ServerSocket serverSocket;
	private Socket recvSocket;
	private Socket sendSocket;
	private int ffinish;
//	private Map<String, Integer> urls;
//	private ArrayList<String> array;
	
	public TCPConnection(int port){
		this.ffinish=0;
		this.port = port;
		//this.name = name;
		//if (name == 0) {
		// computer 0, need to start a server
			//fname = 1;
			try {
				// Start a server, store the local address
				this.serverSocket = new ServerSocket(port);
//				this.address = serverSocket.getInetAddress();
				this.address = InetAddress.getByName("192.168.1.107");

				// name is hashcode of address mode 2, it will be 0 or 1
//				this.name = Math.abs(address.hashCode()%2); 
				this.name = 0;
				System.out.println("my ip is : " +this.address);
				System.out.println("Computer "+this.name+" started, waiting for connection...");
				
				// Connect to friends, friend is server
				fname = 1 - name;
//				Scanner sc = new Scanner(System.in);
//				System.out.println("Please input your friend's ip:");
//				String address_s = sc.nextLine();
				//172.30.13.249
				String faddress_s = "192.168.1.109";
				try {
					faddress = InetAddress.getByName(faddress_s);
					try {
						sendSocket=new Socket(faddress,port);
						System.out.println("Computer "+name +": Connection established with "+faddress.getHostAddress());
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				// Connection established
				recvSocket = serverSocket.accept();
				System.out.println("Computer "+name +": "+faddress.getHostAddress()+" has been connected");
				
				

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		//}else{
		// computer 1, need to connect a connection 
//			fname = 0;
//			Scanner sc = new Scanner(System.in);
//			System.out.println("Please input your friend's ip:");
//			String address_s = sc.nextLine();
//			try {
//				faddress = InetAddress.getByName(address_s);
//				try {
//					sendSocket=new Socket(faddress,port);
//					System.out.println(name +": Connection established with "+faddress.getHostAddress());
//				} catch (UnknownHostException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			} catch (UnknownHostException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			
		//}
		
	}
	

	
	public int getMyname(){
		return name;
	}
	public int getFfinish(){
		return ffinish;
	}
	
	
	@Override
	public void run(){
		
			System.out.println("Run " + recvSocket.isClosed());
			try {
				//Bound InputStream with Socket to get the message
				InputStream inputStream = recvSocket.getInputStream();
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String temp = null;
				while ((temp = bufferedReader.readLine()) != null) {
//					System.out.println(
//							"Get message from "+ fname+ " " + temp + ",which ip is :" + faddress.getHostAddress());
					//Store the link itself as the keyword
					
					if (temp.equals("1")) {
						ffinish=1;
					}else if(temp.equals("0")){
						ffinish=0;
					}else{
//						String s[] = temp.split("\t");
//						if(s.length == 0)Crawler.manageURL(temp, temp);
//						if(s.length == 1)Crawler.manageURL(s[0], s[0]);
						Crawler.manageURL(temp, temp);
					}
				} 
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		
	}
	
	public void send(String msg){
        try {
        	if(msg.startsWith(" ")||msg.equals("")) return;
			//Bound OutputStream with Socket to send the message
			OutputStream outputStream=sendSocket.getOutputStream();
			PrintWriter printWriter=new PrintWriter(outputStream);
			printWriter.print(msg+"\n");
			printWriter.flush();
			//sendSocket.shutdownOutput();//shutdown the output 
//			System.out.println("Send :" +msg);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void close(){
		try {
			sendSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			recvSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
