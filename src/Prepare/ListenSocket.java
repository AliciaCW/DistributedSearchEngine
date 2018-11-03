package Prepare;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Callable;

public class ListenSocket implements Callable<Integer> {
	private Socket recvSocket;
	private Crawler crawler;
	private TCPConnection tcpconnection;

	public ListenSocket(Socket s, Crawler c,TCPConnection t) {
		recvSocket = s;
		crawler = c;
		tcpconnection = t;
		System.out.println("!!!!!!!!!!!!!!!!!"+recvSocket);
	}

	@Override
	public Integer call() {
		try {
			InputStream inputStream = recvSocket.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String temp = null;
//			System.out.println("!!!!!!!!!!!!!!!!!"+recvSocket+" try");
			while ((temp = bufferedReader.readLine()) != null) {
//				if (temp.equals("1")) {
//					// ffinish.put(findKeybyValue(entry.getKey().getInetAddress()),1);
//					// System.out.println(findKeybyValue(entry.getKey().getInetAddress())+" 1");
//					return 1;
//				} else if (temp.equals("0")) {
//					// ffinish.put(findKeybyValue(entry.getKey().getInetAddress()),0);
//					// System.out.println(findKeybyValue(entry.getKey().getInetAddress())+" 0");
//					return 0;
//				} else {
//					System.out.println("Get message " + temp + ",which socket is :" + recvSocket);
					// Store the link itself as the keyword
					// String s[] = temp.split("\t");
					// if(s.length == 0)Crawler.manageURL(temp, temp);
					// if(s.length == 1)Crawler.manageURL(s[0], s[0]);
//					System.out.println("start managing: " + crawler);
//					System.out.println("Received from " +recvSocket + temp);
					crawler.manageURL(temp, temp);
//					System.out.println("end managing");
//				}
			}
		} catch (SocketException s) {
			tcpconnection.closeSocket(recvSocket);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return -1;
	}

}
