package Prepare;

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
	private Map<String, Integer> urls;
	private ArrayList<String> array;
	private Database database;
	private TCPConnection tcpconnection;
	private int finish = 0;
	
	Crawler(Map<String, Integer> urls, ArrayList<String> array, Database database){
		this.urls = urls;
		this.array = array;
		this.database = database;
		
	}
	
	public void crwaling(String url) {
		// print("Fetching %s ...", url);

		try {
			Document doc = Jsoup.connect(url).validateTLSCertificates(false).followRedirects(true).get();
			Elements links = doc.select("a[href]");

			// print("Links: (%d)", links.size());
			for (Element link : links) {
				
				if (Math.abs(link.hashCode() % tcpconnection.getDistributedSize()) == tcpconnection.getMyname()) {
					// if the hashcode is myname
					manageURL(link.attr("abs:href"), link.text());
				} else {
					// if the hashcode belongs to friends
					// !! need to be done
					// should send the keywords?
					tcpconnection.send(link.attr("abs:href"),Math.abs(link.hashCode() % tcpconnection.getDistributedSize()));
				}

			}
		} catch (org.jsoup.UnsupportedMimeTypeException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void print(String msg, Object... args) {
		System.out.println(String.format(msg, args));
	}

	private String trim(String s, int width) {
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
	public synchronized int manageURL(String url, String keywords) {
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
	
	public void setTCPConnection(TCPConnection tcpconnection) {
		this.tcpconnection = tcpconnection;
	}

	private boolean validURL(String url) {
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