public class Main {

    public static void main(String[] args) {

        CrawlerEngine crawler1 =new CrawlerEngine("https://en.wikipedia.org/wiki/Search_engine");
        CrawlerEngine crawler2 =new CrawlerEngine("https://github.com/");
        CrawlerEngine crawler3 =new CrawlerEngine("https://www.reddit.com/");

        crawler1.start();
        crawler2.start();
        crawler3.start();
    }
}
