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
import java.sql.*;
import java.util.ArrayList;

public class Database  extends Thread{
	
	private String url;
	private String username;
	private String password;
	private String Driver;
	private Connection connection;
	private Statement statement;
	
	//tcpconnection part
	private int port;
	private int name;
	private int fname;
	private InetAddress address; 
	private InetAddress faddress;
	private ServerSocket serverSocket;
	private ServerSocket serverResultSocket;
	private Socket recvSocket;
	private Socket recvResultSocket;
	private Socket sendSocket;
	private Socket sendResultSocket;
	
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

		this.port = port;
		try {
			// Start a server, store the local address
			this.serverSocket = new ServerSocket(port);
//			this.address = InetAddress.getByName("192.168.1.107");
			this.address = InetAddress.getByName("172.30.14.255");

			this.name = 0;
			System.out.println("Database: My ip is : " + this.address);
			System.out.println("Database: Computer " + this.name + " started, waiting for connection...");

			// Connect to friends, friend is server
			fname = 1 - name;
			// 172.30.13.249
			// 172.30.13.133
			String faddress_s = "172.20.10.4";
//			String faddress_s = "192.168.1.109";

			try {
				faddress = InetAddress.getByName(faddress_s);
				try {
					sendSocket = new Socket(faddress, port);
					System.out.println("Database: Computer " + name + ": Connection established with "
							+ faddress.getHostAddress());
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
			System.out.println("Database: Computer " + name + ": " + faddress.getHostAddress() + " has been connected");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// result port part
		try {
			// Start a server, store the local address
			this.serverResultSocket = new ServerSocket(resultport);
			System.out.println("Database result: My ip is : " + this.address);
			System.out.println("Database result: Computer " + this.name + " started, waiting for connection...");

			// Connect to friends, friend is server
			// 172.30.13.249
//			String faddress_s = "172.30.13.133";
			// 172.30.13.133

			String faddress_s = "172.20.10.4";
			try {
				faddress = InetAddress.getByName(faddress_s);
				try {
					sendResultSocket = new Socket(faddress, resultport);
					System.out.println("Database result: Computer " + name + ": Connection established with "
							+ faddress.getHostAddress());
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
			recvResultSocket = serverResultSocket.accept();
			System.out.println("Database result: Computer " + name + ": " + faddress.getHostAddress() + " has been connected");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
					System.out.println(
							"run() Get message from "+ fname+ " " + temp + ",which ip is :" + faddress.getHostAddress());
					//Store the link itself as the keyword
//					System.out.println("test1");
					sendResult(searchRecvQuery(temp));
			       
//			        System.out.println("5");

//					if (result.size()!=0) {
//						String result_s = new String();
//						for (int i = 0; i < result.size(); i++) {
//							if (result.get(i).contains("http")) {
//								result_s = result_s+result.get(i) + "\n";
//							}
//						}
//						sendResult(result_s);
//					}else{
//						sendResult(" ");
//					}
				
				
				} 
		        
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			

//        	System.out.println("11");

		
	}
	
	public String searchRecvQuery(String query){
		 ArrayList<String> result = new ArrayList<String>();
//	        System.out.println("test2");

			String s = convert(query);

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
	
	public ArrayList<String> search(String query){
		send(query);
		ArrayList<String> result = searchLocal(query);
		String answer = recvResult();
		if (!answer.equals(" ")) {
			String ans[] = answer.split("\t\t\t");
			for (int i = 0; i < ans.length; i++) {
				result.add(ans[i]);
			} 
		}
		return result;
	}
	
	//tcpconnection part
	public synchronized ArrayList<String> searchLocal(String query){	
		ArrayList<String> result = new ArrayList<String>();
//        System.out.println("6");

		String s = convert(query);
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
				result.add(r.getString(2)+"\t"+r.getString(3)+"\n");
//				System.out.println(r.getString(2)+"\t"+r.getString(3));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return result;
	}
	public int send(String msg){
		try {
			//Bound OutputStream with Socket to send the message
			OutputStream outputStream=sendSocket.getOutputStream();
			PrintWriter printWriter=new PrintWriter(outputStream);
			printWriter.print(msg+"\n");
			printWriter.flush();
			//sendSocket.shutdownOutput();//shutdown the output 
			System.out.println("Send :" +msg);
			
			return 0;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
	}
	public int sendResult(String msg){
		try {
			//Bound OutputStream with Socket to send the message
			OutputStream outputStream=sendResultSocket.getOutputStream();
			PrintWriter printWriter=new PrintWriter(outputStream);
			printWriter.print(msg+"\n");
			printWriter.flush();
			//sendSocket.shutdownOutput();//shutdown the output 
			System.out.println("Send result :" +msg);
			
			return 0;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
	}
	public String recvResult(){
		try {
			//Bound InputStream with Socket to get the message
			InputStream inputStream = recvResultSocket.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String temp = null;
			while ((temp = bufferedReader.readLine()) != null) {
				System.out.println(
						"Get message from "+ fname+ " " + temp + ",which ip is :" + faddress.getHostAddress());
				//Store the link itself as the keyword
				return temp;
			} 
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
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
