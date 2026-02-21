import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class CrawlerEngine{

	private static final int MAX_THREAD =4;

	private static ExecutorService executor = Executors.newFixedThreadPool(MAX_THREAD);
	private static Set<String> visited = ConcurrentHashMap.newKeySet();
	private static BlockingQueue<String> frontier = new LinkedBlockingQueue<>();

	public CrawlerEngine(String seed) {
		String seedUrl = canonicalize(seed);
		if(seedUrl!=null)
			frontier.add(seedUrl);
	}

	// @Override
	// public void run() {
	// 	while(true){
	// 		try {
	// 			URLNode node = frontier.poll(5, TimeUnit.SECONDS);
	// 			if(node == null) return;

	// 			crawl(url,depth);

	// 		} catch (Exception e) {
	// 			// TODO: handle exception
	// 			return;
	// 		}
	// 	}
	// }

	public void start(){
		
		for(int i=0;i<MAX_THREAD;i++)
			executor.execute(()->{
		try {
			crawl();
		} catch (Exception e) {
			Thread.currentThread().interrupt();
		}});

		executor.shutdown();

		try {
			executor.awaitTermination(10, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void crawl() {

    while (true) {
        try {
            String url = frontier.poll(5, TimeUnit.SECONDS);

            if(url == null) return;
            if(!visited.add(url)) continue;

            Document doc = request(url);
            if (doc == null) continue;

            for (Element link : doc.select("a[href]")) {
                String next_link = canonicalize(link.absUrl("href"));

                if (next_link != null && !next_link.isEmpty()) {
                    frontier.offer(next_link);
                }
            }

        } catch (InterruptedException e) {
            return;
        } catch (Exception e) {
            // ignore and continue
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

	private String canonicalize(String link){
		try {
			if(link == null || link.isEmpty()) return null;

			URL url = new URL(link);
			String protocol = url.getProtocol();
			if(!protocol.equals("https") && !protocol.equals("http")) 
				return null;

			String host = url.getHost().toLowerCase();
			String path = url.getPath();
			
			if(path==null || path.isEmpty())
				path = "/";
			return protocol + "://" + host + path;

		} catch (Exception e) {
			System.err.println("error in canonicalization method");
			return null;
		}
	}
	
}
