package fyp.parser;

public class CrawlerMain{
    public static void main(String[] args) {
        int numberOfWeeks = 10;
        if(args.length > 0)
            numberOfWeeks = Integer.parseInt(args[0]);
        String url = "https://data.uspto.gov/uspto.html";
        Crawler crawler = new Crawler(url);
        crawler.run(numberOfWeeks);
    }
}