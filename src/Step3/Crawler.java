package Step3;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jsoup.*;
import org.jsoup.examples.*;
import org.jsoup.helper.*;
import org.jsoup.nodes.*;
import org.jsoup.parser.*;
import org.jsoup.safety.*;
import org.jsoup.select.*;

public class Crawler {
	private static Map<String, Integer> urls;
	private static ArrayList<String> array;
	private static Database database;
	private static TCPConnection tcpconnection;
	private static int port = 9999;
	private static int finish = 0;

	public static void main(String[] args) {
		String url = "http://uic.edu.hk/en";
		database = new Database();
		// String url = "http://p.uic.edu.hk";

		// Create a HashMap to record all the urls need te be parsed
		// array store all the links need to be paresd
		// urls store wheter the a url is paresd or not
		urls = new HashMap<String, Integer>();
		array = new ArrayList<String>();

		tcpconnection = new TCPConnection(port);
		tcpconnection.start();

		long startTime = System.currentTimeMillis();
		int i = 0;
		String ao;
		// array.size()

		if (Math.abs(url.hashCode() % 2) == tcpconnection.getMyname()) {
			// Parsed is 1 unparsed is 0
			System.out.println("uic link hash code is: " + Math.abs(url.hashCode() % 2));
//			urls.put(url, 0);
//			database.insertLink(url, "uic");
//			// iterator = urls.entrySet().iterator();
//			array.add(url);
			try {
				System.out.println("waiting");
				Thread.sleep(1000);
				System.out.println("waiting end");
			} catch (Exception e) {
				System.exit(0);// 退出程序
			}
		} else {
			tcpconnection.send(url);
			
			try {
				System.out.println("waiting");
				Thread.sleep(10000);
				System.out.println("waiting end");
			} catch (Exception e) {
				System.exit(0);// 退出程序
			}
		}

		// tcpconnection.run();

		while (finish == 0 || tcpconnection.getFfinish() == 0){
			while (i < array.size()) {
				if (finish==1) {
					finish = 0;
					tcpconnection.send("0");
				}
				ao = array.get(i);
				if (urls.get(ao) == 1) {
					continue;
				} else {
					crwaling(ao);
					urls.put(ao, 1);
				}

				i++;
//				System.out
//						.println("The " + i + " times paresd; " + "URLS size is " + urls.size() + " \n Parsing: " + ao);
				// System.out.println("****************************************\n");
			}
			finish = 1;
			tcpconnection.send("1");

		}
		long endTime = System.currentTimeMillis();
		System.out.println((endTime - startTime) / 100 + " seconds");

		//tcpconnection.close();
		System.out.println("main is finished");
	}

	public static void crwaling(String url) {

		// print("Fetching %s ...", url);

		try {
			Document doc = Jsoup.connect(url).validateTLSCertificates(false).followRedirects(true).get();
			Elements links = doc.select("a[href]");

			// print("Links: (%d)", links.size());
			for (Element link : links) {
				if (Math.abs(link.hashCode() % 2) == tcpconnection.getMyname()) {
					// if the hashcode is myname
					manageURL(link.attr("abs:href"), link.text());
				} else {
					// if the hashcode belongs to friends
					// !! need to be done
					// should send the keywords?
					tcpconnection.send(link.attr("abs:href"));
				}

			}
		} catch (org.jsoup.UnsupportedMimeTypeException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void print(String msg, Object... args) {
		System.out.println(String.format(msg, args));
	}

	private static String trim(String s, int width) {
		if (s.length() > width)
			return s.substring(0, width - 1) + ".";
		else
			return s;
	}

	// Add URL or not add
	// 0 means this url already in urls/array
	// 1 means put this url to urls and array
	// -1 means this url is meaningless, not in and do not need in urls and
	// array
	// synchronized to make sure at one time only one change
	public synchronized static int manageURL(String url, String keywords) {

		if (urls.containsKey(url)) {
			// If the link this time discoverd is already in urls,
			// Check the keyword if need to be stored
			String keyword = database.getKeyword(url);
			if (keyword != null && !keyword.contains(keywords)) {
				database.insertKeyword(url, keywords);
			}

			return 0;

		} else {
			// If link is not empty, not Chinese, not a download link, not a
			// email address
			if (validURL(url)) {
				// print(" * <%s> (%s)", link.attr("abs:href"),
				// trim(link.text(), 35));
				urls.put(url, 0);
				database.insertLink(url, keywords);
				array.add(url);
				return 1;
			}
			// System.out.println("\t Put this link in url lists");
			return -1;
		}
	}

	private static boolean validURL(String url) {
		if (url.equals("") || url.contains("/cn") || url.contains(".cn/") || url.contains("download=")
				|| url.startsWith("mailto:") || url.contains("facebook") || url.endsWith(".jpg") || url.contains(".JPG")
				|| url.contains(".JPEG") || url.endsWith(".jpeg") || url.endsWith(".jpg") || url.contains("demo.")
				|| url.contains("#header") || url.contains("#footer") || url.contains("mailto")
				|| url.contains("ispace") || url.contains("webfile") || url.contains(".pdf")
				|| url.contains("tmpl=component&")|| url.contains("?")) {
			return false;
		}
		if (!url.contains("uic") || !(url.startsWith("http://uic.edu.hk/")||url.startsWith("http://dst.uic.edu.hk/en"))) {
			return false;
		}
		return true;
	}
}