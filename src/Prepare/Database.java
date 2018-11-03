package Prepare;

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
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Database  extends Thread{
	
	private String url;
	private String username;
	private String password;
	private String Driver;
	private Connection connection;
	private Statement statement;
	
	//tcpconnection part
	private int port;
	private int resultport;
	private int name;
	private int servername;
	private int currentClient;
	private Socket currentSocket;
	private InetAddress address;
	private Map<Integer, InetAddress> fname_address;
	private InetAddress serveraddress;
	private ServerSocket socket;
	private ServerSocket resultSocket;
	private Map<Integer, Socket> fname_recvSocket;
	private Map<Integer, Socket> fname_recvResultSocket;
	private Map<Integer, Socket> fname_sendSocket;
	private Map<Integer, Socket> fname_sendResultSocket;
	private Map<Integer, Integer> ffinish;
	private Map<Socket, FutureTask<String>> ft;
	private Map<Socket, FutureTask<String>> ft2;
	public Database(){
		url = "jdbc:mysql://localhost/";
		username="root";
		password ="";
		Driver = "com.mysql.jdbc.Driver";
		try {
			Class.forName(Driver);
			try {
				connection = DriverManager.getConnection(url,username,password);
				//connection = DriverManager.getConnection(url);
				statement = connection.createStatement();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//tcpcpnnection part
	public Database(int port,int resultport){
		url = "jdbc:mysql://localhost/";
		username="root";
		password ="";
		Driver = "com.mysql.jdbc.Driver";
		try {
			Class.forName(Driver);
			try {
				connection = DriverManager.getConnection(url,username,password);
				statement = connection.createStatement();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		this.servername = -1;
		this.port = port;
		this.resultport = resultport;
		
		try {
			this.serveraddress = InetAddress.getByName("172.16.183.248");
		} catch (UnknownHostException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		fname_address = new HashMap<Integer, InetAddress>();
		fname_recvSocket = new HashMap<Integer, Socket>();
		fname_recvResultSocket =  new HashMap<Integer, Socket>();
		fname_sendSocket = new HashMap<Integer, Socket>();
		fname_sendResultSocket = new HashMap<Integer, Socket>();
		ffinish = new HashMap<Integer, Integer>();
		ft = new HashMap<Socket, FutureTask<String>>();
		ft2 = new HashMap<Socket, FutureTask<String>>();
		fname_address.put(new Integer(servername), serveraddress);

		try {
			// Start a server, store the local address
			this.socket = new ServerSocket(port);
			this.address = InetAddress.getLocalHost();
			this.resultSocket = new ServerSocket(resultport);
			// -1 is server
			// ------------Edit here to change server/client ----------------//
			this.name = -5;
			ffinish.put(new Integer(name), new Integer(0));

			
				/////////////////////////////////////////////////////////////////
				///////////////// SERVER PART START///////////////////////////////////
				/////////////////////////////////////////////////////////////////
				if (this.name == this.servername) {
					System.out.println("My ip is : " + this.address);
					System.out.println("Server " + this.name + " started, waiting for connection...");
					// Connect with clients, send their name to them
//					for (int i = 0; i < 3; i++) {
					
						// Accept socket from clients
						// currentClient is the size of fname_recvSocket, start from 0
					    currentSocket =  socket.accept();
					    System.out.println("*************************currentClient = fname_recvSocket.size()  :"+fname_recvSocket.size());
					    currentClient = fname_recvSocket.size();
						fname_recvSocket.put(currentClient, currentSocket);
						fname_address.put(new Integer(currentClient), fname_recvSocket.get(currentClient).getInetAddress());
						System.out.println("Client " + currentClient + ": " + fname_address.get(currentClient) + " has been connected");
						// Connect to this client
						fname_sendSocket.put(new Integer(currentClient), new Socket(fname_address.get(currentClient), port));
						// Send their name to clients
						OutputStream outputStream = fname_sendSocket.get(currentClient).getOutputStream();
						PrintWriter printWriter = new PrintWriter(outputStream);
						printWriter.print(currentClient + "\n");
						printWriter.flush();
						
						// To monitor if it leave this network
						setDatabaseListenSocketThread(fname_recvSocket.get(currentClient));

//					}
					// Send all clients' address to clients
//					for (int i = 0; i < 3; i++) {
//						OutputStream outputStream = fname_sendSocket.get(i).getOutputStream();
//						PrintWriter printWriter = new PrintWriter(outputStream);
						
						
						// Send the name of clients to new client
						for (int j = 0; j < fname_recvSocket.size()-1; j++) {
							printWriter.print(j + ":" + fname_address.get(j) + "\n");
							printWriter.flush();
						}
						printWriter.print("Finished" + "\n");
						printWriter.flush();
						
						for (int j2 = 0; j2 < fname_recvSocket.size()-1; j2++) {
							// Send the name of new client to all old client
							outputStream = fname_sendSocket.get(j2).getOutputStream();
							printWriter = new PrintWriter(outputStream);
							printWriter.print(currentClient +":" + fname_address.get(currentClient) +"\n");
							printWriter.flush();
							printWriter.print("Finished" + "\n");
							printWriter.flush();
						}
							
						

					/////////////////////////////////////////////////////////////////
					///////////////// SERVER PART END///////////////////////////////////
					/////////////////////////////////////////////////////////////////
				}else{
				/////////////////////////////////////////////////////////////////
				///////////////// CLIENT PART ///////////////////////////////////
				/////////////////////////////////////////////////////////////////

				System.out.println("My ip is : " + this.address);
				System.out.println("Client : Connecting to Server" + serveraddress);

				// Connect to server
				fname_sendSocket.put(new Integer(servername), new Socket(serveraddress, port));
				System.out.println("Client : Connection established with Server" + serveraddress);

				// Connection established with server
				fname_recvSocket.put(new Integer(servername), socket.accept());
				// Receive their name from server
				InputStream inputStream = fname_recvSocket.get(new Integer(servername)).getInputStream();
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String temp = null;
				if ((temp = bufferedReader.readLine()) != null) {
					System.out.println("Get my name:" + temp + " from " + servername);
					// Get name
					this.name = Integer.parseInt(temp);
				}

				fname_address.put(new Integer(this.name), this.address);
				// Establish connections with the other client
				while ((temp = bufferedReader.readLine()) != null) {
					String otherClient[] = temp.split(":/");
					if(otherClient.length == 1) break;
					System.out.println(
							"Get the other client name and address, client " + temp + " from " + servername);
					//Record all the client names
					fname_address.put(new Integer(Integer.parseInt(otherClient[0])),
							InetAddress.getByName(otherClient[1]));
					System.out.println(new Integer(Integer.parseInt(otherClient[0]))+" "+InetAddress.getByName(otherClient[1]));
				}
				Socket tempSocket = null;
				int tempClient = 0;
				while((fname_recvSocket.size() != fname_address.size()-1) && (fname_sendSocket.size() != fname_address.size()-1)) {
					//Ensure every connected client to connecting with them
					tempSocket = socket.accept();
//					System.out.println("tempSocket get "+tempSocket );
//					System.out.println("tempSocket.getInetAddress() get "+tempSocket.getInetAddress() );
					tempClient = findKeybyValue(tempSocket.getInetAddress());
//					System.out.println("tempClient get "+tempClient );
					System.out.println("Connecting to " + tempClient);
					fname_sendSocket.put(new Integer(tempClient),
							new Socket(fname_address.get(tempClient), port));
					System.out.println("Connecting to " + tempClient + " successful");
					fname_recvSocket.put(new Integer(tempClient), tempSocket);
					System.out.println("Connected with " + tempClient + " successful");
					setDatabaseListenSocketThread(fname_recvSocket.get(tempClient));
					System.out.println("Listen socket with client " + tempClient + " successful");
					System.out.println("Distributed Size Update to  " + getDistributedSize());
				}
				fname_sendSocket.put(this.name, new Socket(this.address, port));
				fname_recvSocket.put(this.name, socket.accept());

				/////////////////////////////////////////////////////////////////
				///////////////// CLIENT RESULT PART ///////////////////////////////////
				/////////////////////////////////////////////////////////////////

				System.out.println("My ip is : " + this.address);
				

				tempSocket = null;
				tempClient = 0;
				while ((fname_recvResultSocket.size() != fname_address.size() - 2)
						&& (fname_sendResultSocket.size() != fname_address.size() - 2)) {
					System.out.println("Client Result: Connecting to clients");
					// Ensure every connected client to connecting with them
					tempSocket = resultSocket.accept();
					// System.out.println("tempSocket get "+tempSocket );
					// System.out.println("tempSocket.getInetAddress() get
					// "+tempSocket.getInetAddress() );
					tempClient = findKeybyValue(tempSocket.getInetAddress());
					// System.out.println("tempClient get "+tempClient );
					System.out.println("Connecting to " + tempClient);
					fname_sendResultSocket.put(new Integer(tempClient), new Socket(fname_address.get(tempClient), resultport));
					System.out.println("Connecting to " + tempClient + " successful");
					fname_recvResultSocket.put(new Integer(tempClient), tempSocket);
					System.out.println("Connected with " + tempClient + " successful");
					//Only listen when in need
					System.out.println("Result Distributed Size Update to  " + getResultDistributedSize());
				}

				fname_sendSocket.put(this.name, new Socket(this.address, resultport));
				fname_recvSocket.put(this.name, resultSocket.accept());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			
			/////////////////////////////////////////////////////////////////
			///////////////// SERVER PART START///////////////////////////////////
			/////////////////////////////////////////////////////////////////
			if (this.name == this.servername) {
				do {
				System.out.println("My ip is : " + this.address);
				System.out.println("Server " + this.name + " started, waiting for connection...");
				// Connect with clients, send their name to them

					currentSocket =  socket.accept();
					System.out.println("*************************currentClient = fname_recvSocket.size()  :"+fname_recvSocket.size());
					currentClient = fname_recvSocket.size();
					fname_recvSocket.put(currentClient, currentSocket);
					fname_address.put(new Integer(currentClient), fname_recvSocket.get(currentClient).getInetAddress());
					System.out.println("Client " + currentClient + ": " + fname_address.get(currentClient) + " has been connected");
					// Connect to this client
					fname_sendSocket.put(new Integer(currentClient), new Socket(fname_address.get(currentClient), port));
					// Send their name to clients
					OutputStream outputStream = fname_sendSocket.get(currentClient).getOutputStream();
					PrintWriter printWriter = new PrintWriter(outputStream);
					printWriter.print(currentClient + "\n");
					printWriter.flush();

					
					
					// Send the name of clients to new client
					for (int j = 0; j < fname_recvSocket.size()-1; j++) {
						printWriter.print(j + ":" + fname_address.get(j) + "\n");
						printWriter.flush();
					}
					
					printWriter.print("Finished" + "\n");
					printWriter.flush();
					
					for (int j2 = 0; j2 < fname_recvSocket.size()-1; j2++) {
						// Send the name of new client to all old client
						outputStream = fname_sendSocket.get(j2).getOutputStream();
						printWriter = new PrintWriter(outputStream);
						printWriter.print(currentClient +":" + fname_address.get(currentClient) +"\n");
						printWriter.flush();
						printWriter.print("Finished" + "\n");
						printWriter.flush();
					}
					
					// To monitor if it leave this network
					setDatabaseListenSocketThread(fname_recvSocket.get(currentClient));
					
//				}
				/////////////////////////////////////////////////////////////////
				///////////////// SERVER PART END///////////////////////////////////
				/////////////////////////////////////////////////////////////////
				}while(true);
			} else {
				do {
				System.out.println("//////////////Client Part Start////////////////////");

				/////////////////////////////////////////////////////////////////
				///////////////// CLIENT PART START///////////////////////////////////
				/////////////////////////////////////////////////////////////////
				InputStream inputStream = fname_recvSocket.get(new Integer(servername)).getInputStream();
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String temp = null;
				

					// Establish connections with the other client
					while ((temp = bufferedReader.readLine()) != null) {
						System.out.println(temp);
						String otherClient[] = temp.split(":/");
						if(otherClient.length == 1) break;
						System.out.println(
								"Get the other client name and address, client " + temp + " from " + servername);
						
						fname_address.put(new Integer(Integer.parseInt(otherClient[0])),
								InetAddress.getByName(otherClient[1]));
						System.out.println("Connecting to " + temp);
						fname_sendSocket.put(new Integer(Integer.parseInt(otherClient[0])),
								new Socket(fname_address.get(Integer.parseInt(otherClient[0])), port));
						System.out.println("Connecting to " + fname_address.get(Integer.parseInt(otherClient[0])) + " successful");
						
						fname_recvSocket.put(new Integer(Integer.parseInt(otherClient[0])), socket.accept());
						System.out.println("Connected with " + fname_recvSocket.get(Integer.parseInt(otherClient[0])) + " successful");
						
						setDatabaseListenSocketThread(fname_recvSocket.get(Integer.parseInt(otherClient[0])));
						System.out.println("Listen socket with client " + fname_recvSocket.get(Integer.parseInt(otherClient[0])) + " successful");
						
						System.out.println("Distributed Size Update to  " + getDistributedSize());
					/////////////////////////////////////////////////////////////////
					///////////////// CLIENT RESULT PART/////////////////////////////
					/////////////////////////////////////////////////////////////////
						fname_sendResultSocket.put(new Integer(Integer.parseInt(otherClient[0])),
								new Socket(fname_address.get(Integer.parseInt(otherClient[0])), resultport));
						System.out.println("Connecting to " + fname_address.get(Integer.parseInt(otherClient[0])) + " successful");
						
						fname_recvResultSocket.put(new Integer(Integer.parseInt(otherClient[0])), resultSocket.accept());
						System.out.println("Connected with " + fname_recvSocket.get(Integer.parseInt(otherClient[0])) + " successful");
						// Only listen when in need
						System.out.println("Result Distributed Size Update to  " + getResultDistributedSize());
						
					}
					System.out.println("////////////////Client Part End///////////////////");

				}while(true);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	public void insertLink(String url,String keywords){
		String sql="";
		if(containsHanScript(keywords)) {
			sql = "INSERT INTO `j430003023`.`link` (`URL`, `KEYWORD`) VALUES ('" +url+ "',' ') ";
		}else{
			sql = "INSERT INTO `j430003023`.`link` (`URL`, `KEYWORD`) VALUES ('" +url+ "','" +convert(keywords)+"') ";

		}

		try {
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(sql);
		
		
		
	}
	
	public void insertKeyword(String url,String keywords){
		if(!containsHanScript(keywords)){
			String keyword = getKeyword(url);
			if(!keyword.contains(keywords)){
				String sql = "UPDATE `j430003023`.`link` SET `KEYWORD` ='"+convert(keyword)+" "+convert(keywords)+"' WHERE `URL` = '"+url+"'  ";
		
				try {
					statement.executeUpdate(sql);
				} catch (SQLException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println(sql);
				}
			//System.out.println(sql);
			}
		}
		
	}
	
	public String getKeyword(String url){
		String sql = "SELECT * FROM `j430003023`.`link` WHERE `URL` = '"+url+"' ";
		ResultSet r;
		try {
			r = statement.executeQuery(sql);
			if(!r.next()) return null;
			//System.out.println(sql);
			return r.getString(2);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
	
	public String convert(String s){

		if(s.contains("'")) s=s.replace("'","\\\'");
		if(s.contains("\"")) s=s.replace("\"","\\\"");
		return s;
	}
	
	public static String convertSearch(String s){

		if(s.contains("'")) s=s.replace("'","\\\'");
		if(s.contains("\"")) s=s.replace("\"","\\\"");
		if(s.contains("\\and")) s=s.replace("\\and"," ");
		if(s.contains(" \\or ")) s=s.replace(" \\or","%\'or`KEYWORD`LIKE\' ");
		return s;
	}
	
	public ArrayList<String> search(String query){
		send(query);
		Map<String,String> result = searchLocal(query);
		String[] tmp;
		String answer = recvResult();
		if (!answer.equals(" ")) {
			String ans[] = answer.split("\t\t\t");
			for (int i = 0; i < ans.length; i++) {
				tmp = ans[i].split("\t");
				if(tmp.length == 2) result.put(tmp[0],tmp[1]);
			} 
		}
		ArrayList<String> results = new ArrayList<String>();
		Iterator it = result.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String,String>  entry = (Map.Entry<String,String> ) it.next();
			results.add(entry.getKey()+"\t"+entry.getValue());
		}
		
		return results;
	}
	
	//tcpconnection part
	public synchronized Map<String,String> searchLocal(String query){	
		Map<String,String> result = new HashMap<String,String>();
//        System.out.println("6");

		String s = convertSearch(query);
//        System.out.println("7");

		String sql = "SELECT * FROM `j430003023`.`link` WHERE `KEYWORD` LIKE '%";
		if(s.contains(" ")){
			String[] split = s.split(" ");
			
			for(int i = 0;i < split.length;i++){
				if(!(split[i].equals("")||split[i].equals(" "))) 
					sql = sql+split[i]+"%";
			}
			sql = sql+"'";
		}else{
			sql = sql+s+"%'";
		}
//		String sql = "SELECT * FROM `j430003023`.`link` WHERE `KEYWORD` LIKE '%"+convert(query)+"%'";
		ResultSet r;
		try {
			System.out.println(sql);
			r = statement.executeQuery(sql);
			while(r.next()){
				result.put(r.getString(2),r.getString(3));
//				System.out.println(r.getString(2)+"\t"+r.getString(3));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return result;
	}
	
	public String searchRecvQuery(String query){
		 ArrayList<String> result = new ArrayList<String>();
//	        System.out.println("test2");

			String s = convertSearch(query);

			String sql = "SELECT * FROM `j430003023`.`link` WHERE `KEYWORD` LIKE '%";
			if(s.contains(" ")){
				String[] split = s.split(" ");
				
				for(int i = 0;i < split.length;i++){
					if(!(split[i].equals("")||split[i].equals(" "))) 
						sql = sql+split[i]+"%";
				}
				sql = sql+"'";
			}else{
				sql = sql+s+"%'";
			}
//			String sql = "SELECT * FROM `j430003023`.`link` WHERE `KEYWORD` LIKE '%"+convert(query)+"%'";
			ResultSet r;
			try {
				Statement statement = connection.createStatement();
				System.out.println(sql);
				r = statement.executeQuery(sql);
				String result_s = new String();
				int key = 0;
				while(r.next()){
					if(key!=1) key=1;
					result.add(r.getString(2)+"\t"+r.getString(3));
					System.out.println(r.getString(2)+"\t"+r.getString(3));
					result_s = result_s+r.getString(2)+"\t"+r.getString(3)+ "\t\t\t";
					System.out.println("\nresult_s is: "+result_s);
				}
				if(key==1) return result_s;
				else return " ";
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return " ";
	}
	
	public int send(String msg){
		try {
			//Bound OutputStream with Socket to send the message
			Iterator it = fname_sendSocket.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Integer, Socket> entry = (Map.Entry<Integer, Socket>) it.next();
				if (entry.getKey() != this.servername) {
					// System.out.println("fname "+entry.getKey()+ " " +entry.getValue());
					OutputStream outputStream = entry.getValue().getOutputStream();
					PrintWriter printWriter = new PrintWriter(outputStream);
					printWriter.print(msg + "\n");
					printWriter.flush();
				}
			}
		System.out.println("send to everyone"  + msg);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
	}
	
	public int sendResult(String msg,Socket recvSocket){
		try {
			Iterator it = fname_recvSocket.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Integer, Socket> entry = (Map.Entry<Integer, Socket>) it.next();
				if (entry.getValue().equals(recvSocket)) {
					//Bound OutputStream with Socket to send the message
					OutputStream outputStream=fname_sendResultSocket.get(entry.getKey()).getOutputStream();
					PrintWriter printWriter=new PrintWriter(outputStream);
					printWriter.print(msg+"\n");
					printWriter.flush();
					//sendSocket.shutdownOutput();//shutdown the output 
					System.out.println("Send result :" +msg);
					
					return 0;
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
	}
	
	public String recvResult(){
		System.out.println("recvResult");
		Map<String,String> link = new HashMap<String,String>();
		String temp = new String();
		//Bound OutputStream with Socket to send the message
		Iterator it = fname_recvResultSocket.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, Socket> entry = (Map.Entry<Integer, Socket>) it.next();
			//Set Result Recv Thread
			DatabaseListenSocket ls = new DatabaseListenSocket(entry.getValue(),this);
			ft2.put(entry.getValue(), new FutureTask<>(ls));
			new Thread(ft2.get(entry.getValue())).start();
		}
		Iterator it2 = ft2.entrySet().iterator();
		while (it2.hasNext()) {
			Map.Entry<Socket, FutureTask<String>> entry = (Map.Entry<Socket, FutureTask<String>>) it2.next();
			//Get result
			try {
				System.out.println(entry.getKey());
				String  l = entry.getValue().get();
				System.out.println(l);
				temp = temp +l;				
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return temp;
		
	}
	
	
	public void closeSocket(Socket recvSocket) {
		int clientName = 0;
		Iterator it;
		Map.Entry<Integer, Socket> entry = null;
		Map.Entry<Integer, Socket> entry2 = null;
		Map.Entry<Integer, InetAddress> e = null;
		Map.Entry<Integer, InetAddress> e2 = null;
		Map.Entry<Integer, Integer> en = null;
		Map.Entry<Integer, Integer> en2 = null;
	
		//Get closed client name
		it = fname_recvSocket.entrySet().iterator();
		while (it.hasNext()) {
			entry = (Map.Entry<Integer, Socket>) it.next();
			if (entry.getValue().equals(recvSocket)) {
				clientName = entry.getKey();
			}
				
		}		
		System.out.println("********************************************************");
		System.out.println("Get closed socket name: "+clientName);
		
		//Change My name
		if(this.name > clientName ) {
			System.out.println("Change myname from "+this.name+ " to " +(this.name-1));
			this.name = this.name-1;
		}else {
			System.out.println("Do not neet to change my name:"+this.name);
		}
		

		System.out.println("********************************************************");
//		System.out.println("Delete Socket from fname_address");
//		System.out.println("********************************************************");
		//Delete Socket from fname_address
		it = fname_address.entrySet().iterator();
		while (it.hasNext()) {
//			System.out.println("fname_address it.hasNext");
			e = (Map.Entry<Integer, InetAddress>) it.next();
//			System.out.println("e:"+e);
				if (e.getKey() >= clientName && fname_address.get(e.getKey()+1)!=null) {
//					System.out.println("e:"+e);
					System.out.println("fname_address.replace " + e.getKey() + " " + e.getValue() +" " + fname_address.get(e.getKey()+1));
					fname_address.replace(e.getKey(), e.getValue(), fname_address.get(e.getKey()+1));
				}
		}
		System.out.println("Remove the last entry of fname_address: "+fname_address.get(fname_address.size()-2));
		fname_address.remove(fname_address.size()-2);
		
//		System.out.println("********************************************************");
//		System.out.println("Delete Socket from fname_sendSocket");
//		System.out.println("********************************************************");
		//Delete Socket from fname_sendSocket
		it = fname_sendSocket.entrySet().iterator();
		while (it.hasNext()) {
//			System.out.println("fname_sendSocket it.hasNext");
			entry = (Map.Entry<Integer, Socket>) it.next();
//			System.out.println("e:"+entry);
				if (entry.getKey() >= clientName && fname_sendSocket.get(entry.getKey()+1)!=null) {
//					System.out.println("e:"+entry);
					System.out.println("fname_sendSocket.replace " + entry.getKey() + " " + entry.getValue() +" " + fname_sendSocket.get(entry.getKey()+1));
					fname_sendSocket.replace(entry.getKey(), entry.getValue(), fname_sendSocket.get(entry.getKey()+1));
				}
		}
		System.out.println("Remove the last entry of fname_sendSocket: "+fname_sendSocket.get(getMaxKey(fname_sendSocket)));
		// Need to remove the largest key & value
		fname_sendSocket.remove(getMaxKey(fname_sendSocket));
		
//		System.out.println("********************************************************");
//		System.out.println("Delete Socket from fname_recvSocket");
//		System.out.println("********************************************************");
		//Delete Socket from fname_recvSocket
		it = fname_recvSocket.entrySet().iterator();
		while (it.hasNext()) {
//			System.out.println("fname_recv it.hasNext");
			entry = (Map.Entry<Integer, Socket>) it.next();
//			System.out.println("e:"+entry);
				if (entry.getKey() >= clientName && fname_recvSocket.get(entry.getKey()+1)!=null) {
//					System.out.println("e:"+entry);
					System.out.println("fname_recvSocket.replace " + entry.getKey() + " " + entry.getValue() +" " + fname_recvSocket.get(entry.getKey()+1));
					fname_recvSocket.replace(entry.getKey(), entry.getValue(), fname_recvSocket.get(entry.getKey()+1));
				}
		}
		System.out.println("Remove the last entry of fname_recvSocket: "+fname_recvSocket.get(getMaxKey(fname_recvSocket)));
		// Need to remove the largest key & value
		fname_recvSocket.remove(getMaxKey(fname_recvSocket));
		
		it = fname_sendResultSocket.entrySet().iterator();
		while (it.hasNext()) {
//			System.out.println("fname_sendSocket it.hasNext");
			entry = (Map.Entry<Integer, Socket>) it.next();
//			System.out.println("e:"+entry);
				if (entry.getKey() >= clientName && fname_sendResultSocket.get(entry.getKey()+1)!=null) {
//					System.out.println("e:"+entry);
					System.out.println("fname_sendResultSocket.replace " + entry.getKey() + " " + entry.getValue() +" " + fname_sendResultSocket.get(entry.getKey()+1));
					fname_sendResultSocket.replace(entry.getKey(), entry.getValue(), fname_sendResultSocket.get(entry.getKey()+1));
				}
		}
		System.out.println("Remove the last entry of fname_sendResultSocket: "+fname_sendResultSocket.get(getMaxKey(fname_sendResultSocket)));
		// Need to remove the largest key & value
		fname_sendResultSocket.remove(getMaxKey(fname_sendResultSocket));
		
//		System.out.println("********************************************************");
//		System.out.println("Delete Socket from fname_recvSocket");
//		System.out.println("********************************************************");
		//Delete Socket from fname_recvSocket
		it = fname_recvResultSocket.entrySet().iterator();
		while (it.hasNext()) {
//			System.out.println("fname_recv it.hasNext");
			entry = (Map.Entry<Integer, Socket>) it.next();
//			System.out.println("e:"+entry);
				if (entry.getKey() >= clientName && fname_recvResultSocket.get(entry.getKey()+1)!=null) {
//					System.out.println("e:"+entry);
					System.out.println("fname_recvResultSocket.replace " + entry.getKey() + " " + entry.getValue() +" " + fname_recvResultSocket.get(entry.getKey()+1));
					fname_recvResultSocket.replace(entry.getKey(), entry.getValue(), fname_recvResultSocket.get(entry.getKey()+1));
				}
		}
		System.out.println("Remove the last entry of fname_recvSocket: "+fname_recvResultSocket.get(getMaxKey(fname_recvResultSocket)));
		// Need to remove the largest key & value
		fname_recvResultSocket.remove(getMaxKey(fname_recvResultSocket));
		//Delete Socket from ffinish
//		it = ffinish.entrySet().iterator();
//		while (it.hasNext()) {
//			System.out.println("ffinish it.hasNext");
//			en = (Map.Entry<Integer, Integer>) it.next();
//			System.out.println("e:"+en);
//				if (en.getKey() >= clientName && ffinish.get(en.getKey()+1)!=null) {
//					System.out.println("e:"+en);
//					System.out.println("ffinish.replace " + clientName + " " + en.getValue() +" " + ffinish.get(en.getKey()+1));
//					ffinish.replace(clientName, en.getValue(), ffinish.get(en.getKey()+1));
//				}
//		}
//		System.out.println("Remove the last entry of ffinish: "+ffinish.get(ffinish.size()-1));
//		ffinish.remove(ffinish.size()-1);
		
		
		//If this is server, decrease current client name
//		if(this.name == this.servername) {
//			this.currentClient --;
//		}
		//Since every time use currentClient, it will get the size of fname_recvSocket
		//There is no need to update this variable
		//currentClient = fname_recvSocket.size();
		
		System.out.println("######################################closeSocket done. fname_recvSocket.size(): "+fname_recvSocket.size()+" fname_sendSocket.size():  "+fname_sendSocket.size());
	}
	
	public int getDistributedSize() {
		if(this.name == this.servername)
			return fname_sendSocket.size();
		else
			return fname_sendSocket.size()-1;
	}

	public int getResultDistributedSize() {
		if(this.name == this.servername)
			return 0;
		else
			return fname_sendResultSocket.size();
	}

	public void setDatabaseListenSocketThread(Socket s) {
		///////////////////// Using Thread Moniter Recv Socket////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////
		DatabaseListenSocket ls = new DatabaseListenSocket(s,this);
		ft.put(s, new FutureTask<>(ls));
		new Thread(ft.get(s)).start();

	}

	private Integer findKeybyValue(InetAddress i) {
		Iterator it = fname_address.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, InetAddress> entry = (Map.Entry<Integer, InetAddress>) it.next();
			if (entry.getValue().equals(i))
				return entry.getKey();
		}
		return new Integer(-1);
	}
	
	private Object getMaxKey(Map<Integer, Socket> map) {
		if (map == null) return null;
		Set<Integer> set = map.keySet();
		Object[] obj = set.toArray();
		Arrays.sort(obj);
		return obj[obj.length-1];
		}

	public static boolean containsHanScript(String s) {
        for (int i = 0; i < s.length(); ) {
            int codepoint = s.codePointAt(i);
            i += Character.charCount(codepoint);
            if (Character.UnicodeScript.of(codepoint) == Character.UnicodeScript.HAN) {
                return true;
            }
        }
        return false;
    }
	
	public void close() throws SQLException{
		statement.close();
		connection.close();

	}
	 
	
	

}
