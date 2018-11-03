package Prepare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
	public static void main(String[] args) {
		Map<String, Integer> urls;
		ArrayList<String> array;
		Database database;
		Crawler crawler ;
		TCPConnection tcpconnection;
		int port = 9999;
		int finish = 0;
		
		
		String url = "http://uic.edu.hk/en";
		database = new Database();
		// String url = "http://p.uic.edu.hk";

		// Create a HashMap to record all the urls need te be parsed
		// array store all the links need to be paresd
		// urls store wheter the a url is paresd or not
		urls = new HashMap<String, Integer>();
		array = new ArrayList<String>();
		crawler = new Crawler(urls,array,database);

		tcpconnection = new TCPConnection(port,crawler);
		crawler.setTCPConnection(tcpconnection);
		tcpconnection.start();

		long startTime = System.currentTimeMillis();
		int i = 0;
		String ao;

		System.out.println("distributedSize is: " +tcpconnection.getDistributedSize());
		System.out.println("crawler: " + crawler);
//		tcpconnection.setListenSocketThread();

		// array.size()
		
		if(tcpconnection.getMyname() == tcpconnection.getServername())  {
			try {
				Thread.sleep(100000000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if(tcpconnection.getMyname() == 0)  {
			if (Math.abs(url.hashCode() % tcpconnection.getDistributedSize()) == tcpconnection.getMyname()) {
				// Parsed is 1 unparsed is 0
				System.out.println(
						"uic link hash code is: " + Math.abs(url.hashCode() % tcpconnection.getDistributedSize()));
				urls.put(url, 0);
				database.insertLink(url, "uic");
				array.add(url);

			} else {
				tcpconnection.send(url, Math.abs(url.hashCode() % tcpconnection.getDistributedSize()));
			} 
		}
		

		// tcpconnection.run();
		

		while (finish == 0 || tcpconnection.getFfinish() == 0){
//		while(true) {
			while (i < array.size()) {
				if (finish==1) {
					finish = 0;
					tcpconnection.send("0",-1);
				}
				ao = array.get(i);
				if (urls.get(ao) == 1) {
					continue;
				} else {
//					System.out.println("crawling :"+ao);
					crawler.crwaling(ao);
					urls.put(ao, 1);
				}

				i++;
//				System.out
//						.println("The " + i + " times paresd; " + "URLS size is " + urls.size() + " \n Parsing: " + ao);
				// System.out.println("****************************************\n");
			}
			finish = 1;
			tcpconnection.send("1",-1);

		}
		long endTime = System.currentTimeMillis();
		System.out.println((endTime - startTime) / 100 + " seconds");

		//tcpconnection.close();
		System.out.println("main is finished");
	}
	
}
