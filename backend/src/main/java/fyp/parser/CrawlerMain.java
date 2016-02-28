package fyp.parser;

public class CrawlerMain{
    public static void main(String[] args) {
        String url = "https://data.uspto.gov/uspto.html";
        Crawler crawler = new Crawler(url);
        crawler.run();
    }
}