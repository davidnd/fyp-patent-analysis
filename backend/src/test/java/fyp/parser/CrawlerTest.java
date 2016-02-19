package fyp.parser;

import org.junit.Test;

public class CrawlerTest{
    @Test
    public void sample(){
        String url = "https://data.uspto.gov/uspto.html";
        Crawler crawler = new Crawler(url);
        crawler.run();
    }
}