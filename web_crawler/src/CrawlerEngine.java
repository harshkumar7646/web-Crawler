import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class CrawlerEngine implements Runnable{

	private static final int depth =3;
	private Thread thread;
	private String first_url;
	private ArrayList<String> visited = new ArrayList<>();
	private int ID;
	
	public CrawlerEngine(String url, int num) {
		System.out.println("Web Crawler created");
		first_url = url;
		ID= num;
		
		thread = new Thread(this);
		thread.start();
	}
	
	
	@Override
	public void run() {
		crawl(1,first_url);
	}
	
	private void crawl ( int level, String url) {
		if(level<=depth) {
			Document doc = request(url, visited);
			
			if(doc!=null) {
				for( Element link : doc.select("a[href]")) {
					String next_link = link.absUrl("href");
					if(visited.contains(next_link)== false) {
						crawl(level++,next_link);
					}
				}
			}
		}
	}
	
	private Document request(String url, ArrayList<String> list) {
		try {
			Connection con = Jsoup.connect(url);
			Document doc = con.get();
			
			if(con.response().statusCode()==200) {
				System.out.println("\n ** Bot ID: " + ID + ", Received Webpage at: "+ url);
				
				String title = doc.title();
				System.out.println(title);
				list.add(url);
			}
			return doc;
		}
		
		catch(IOException e) {
			return null;
		}
		
	}
	
	public Thread getthreads() {
		return thread;
	}
	
}
