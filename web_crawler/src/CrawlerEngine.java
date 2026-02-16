import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class CrawlerEngine implements Runnable{

	private static final int MAX_THREAD =4;
    private static final int MAX_DEPTH =4;
	private String first_url;
	private int depth;

	private static ExecutorService executor = Executors.newFixedThreadPool(MAX_THREAD);
	private static Set<String> visited = ConcurrentHashMap.newKeySet();

	public CrawlerEngine(String link, int d) {
		first_url = link;
		depth= d;
	}
	
	@Override
	public void run() {
		crawl(first_url,depth);
	}
	public void start(){
		executor.submit(new CrawlerEngine(first_url,1));
	}
	
	private void crawl (String url, int depth) {
		if(depth>MAX_DEPTH) return;

		if(!visited.add(url)) return;

		Document doc = request(url);
					
		if(doc!=null) {
			for( Element link : doc.select("a[href]")) {
				String next_link = link.absUrl("href");
				if(!visited.contains(next_link)) {
					executor.submit(new CrawlerEngine(next_link, depth+1));
				}
			}
		}
	}
	
	private Document request(String url) {
		try {
			Connection con = Jsoup.connect(url).timeout(5000);
			Document doc = con.get();
			
			if(con.response().statusCode()==200) {
				System.out.println(Thread.currentThread().getName()+" visited: "+url);
			}
			return doc;
		}
		
		catch(IOException e) {
			System.err.println("error in request method");
			return null;
		}
		
	}
	
}
