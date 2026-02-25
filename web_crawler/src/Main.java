public class Main {
    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread(() -> new CrawlerEngine("https://en.wikipedia.org/wiki/Search_engine").start());
        Thread t2 = new Thread(() -> new CrawlerEngine("https://github.com/").start());
        Thread t3 = new Thread(() -> new CrawlerEngine("https://www.reddit.com/").start());

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();
    }
}