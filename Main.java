package Step1;

import java.io.IOException;
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

public class Main {
	private static Map<String, Integer> urls;
	//private static Iterator<Entry<String, Integer>> iterator;
	private static ArrayList<String> array;	
	public static void main(String[] args){
		 String url = "http://uic.edu.hk/en";
		 //String url = "http://p.uic.edu.hk";

	     // Create a HashMap to record all the urls need te be parsed
		 urls = new HashMap<String, Integer>();
		 array = new ArrayList<String>();
	     // Parsed is 1 unparsed is 0
	     urls.put(url, 0);
	     
	     //iterator = urls.entrySet().iterator();
	     array.add(url);
	     
	     
	     long startTime = System.currentTimeMillis();
	     int i = 0;
	     String ao;
	     while(i<array.size()){
	    	 /*
	    	 Map.Entry<String, Integer> pair =  (Map.Entry<String, Integer>)iterator.next();
	    	 if(urls.get(pair.getKey()) == 1){
	    		 continue;
	    	 }else{
	    		 analysis(pair.getKey());
	    		 urls.put(pair.getKey(),1);
	    	 }
	    	 iterator = urls.entrySet().iterator();
	    	 */
	    	 ao = array.get(i);
	    	 if(urls.get(ao) == 1){
	    		 continue;
	    	 }else{
	    		 analysis(ao);
	    		 urls.put(ao,1);
	    	 }
	    	 
	    	 i++;
	    	 System.out.println("The " +i+" times paresd; "+"URLS size is "+urls.size());
	    	 System.out.println("****************************************\n");
	    	 
	     }
	     long endTime = System.currentTimeMillis();
	     System.out.println((endTime-startTime)/100+" seconds");
	     
	     /*
	     for(String key:urls.keySet()){
	    	 if(urls.get(key) == 1) continue;
	    	 else{
	    		 analysis(key);
	    		 urls.put(key, 1);
	    	 }
	     }
	     */
	}
	public static void analysis(String url) {
       
       
        print("Fetching %s ...", url);

        //Document doc = Jsoup.connect(url).followRedirects(true).get();
        try{
        Document doc = Jsoup.connect(url).ignoreHttpErrors(true).validateTLSCertificates(false).followRedirects(true).get();
        Elements links = doc.select("a[href]");

        print("Links: (%d)", links.size());
        for (Element link : links) {
            if(urls.containsKey(link.attr("abs:href"))||urls.containsKey(link.attr("abs:href")+"/")||urls.containsKey(link.attr("abs:href")+"#")) {
            	//System.out.println("\t This link already in url lists");
            }
            else{
            	// If link is not empty, not Chinese, not a download link, not a email address
            	if(validURL(link.attr("abs:href"))) {
                    print(" * <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));            
            		urls.put(link.attr("abs:href"), 0);
            		array.add(link.attr("abs:href"));
            	}
            	//System.out.println("\t Put this link in url lists");
            }
            
            //System.out.println(" * a: <"+link.attr("abs:href")+">  (%s)"+trim(link.text(), 35));
        }
        }catch(org.jsoup.UnsupportedMimeTypeException e){
        	
        }catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }
    private static boolean validURL(String url){
    	if(url.equals("")||url.contains("/cn")||url.contains(".cn/")
    			||url.contains("download=")||url.startsWith("mailto:")
    			||url.contains("facebook")||url.endsWith(".jpg")
    			||url.contains(".JPG")||url.contains(".JPEG")
    			||url.endsWith(".jpeg")||url.endsWith(".jpg")
    			||url.contains("demo.")||url.contains("#header")
    			||url.contains("#footer")||url.contains("mailto")
    			||url.contains("ispace")||url.contains("webfile")
    			||url.contains(".pdf")||url.contains("tmpl=component&")){
    		return false;
    	}
    	if(!url.contains("uic")||!url.startsWith("http://uic.edu.hk/")){
    		return false;
    	}
    	return true;
    }
}
