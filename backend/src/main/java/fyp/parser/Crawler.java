package fyp.parser;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import fyp.utils.Helper;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {
    private List <String> fileHref;
    private String urlStr;
    private String firstLinkText;
    private  String secondLinkText;
    private  String googleUrl = "https://www.google.com/googlebooks/uspto-patents-grants-text.html";
    private String saveDes = "../data/grant/";
    public Crawler(String url) {
        this.urlStr = url;
        this.firstLinkText = "Patent Grant Full Text Data/XML";
        this.secondLinkText = "ipg";
        this.fileHref = new ArrayList<>();
    }

    public void run(){
        try {
            List <String> googleLinks = extractLink(this.googleUrl, this.secondLinkText);
            List <String> usptoLinks = extractLink(this.urlStr, this.firstLinkText);
            List <String> yearLinks;
            for (String link: usptoLinks) {
                System.out.println(link);
                yearLinks = extractLink(link, secondLinkText);
                for(String s: yearLinks){
                    String filename = getFileName(s);
                    int id = check(googleLinks, filename);
                    if(id != -1){
                        download(googleLinks.get(id), saveDes + filename);
                    }
                    else{
//                        download(s, saveDes + filename);
                        System.out.println("this file not in google :v");
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public String getFileName(String s){
        int id = s.lastIndexOf('/');
        if(id != -1){
            return s.substring(id + 1);
        }
        return null;
    }
    public void download(String urlString, String destination){
        System.out.println("Downloading file at " + urlString + " to " + destination);
        File file = new File(destination);
        if(file.exists()){
            System.out.println("File was already downloaded");
            return;
        }
        URL url;
        try {
            url = new URL(urlString);
            FileUtils.copyURLToFile(url, file, 3000, 600000);
            System.out.println("Unzipping .... ");
            Helper.executeShellCommand("unzip -x " + destination + " -d ../data/grant");
            System.out.println("Done!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public int check (List <String> list, String s){
        for (int i = 0; i<list.size(); i++){
            String filename = getFileName(list.get(i));
            if(filename != null && filename.equalsIgnoreCase(s))
                return i;
        }
        return -1;
    }
    public List<String> extractLink (String url, String linkContent) throws IOException{
        List <String> hrefs = new ArrayList<>();
        Document doc = Jsoup.connect(url).get();
        Elements links = doc.select("a[href]");
        for (Element link: links){
            if(link.text().trim().toLowerCase().contains(linkContent.trim().toLowerCase())){
                hrefs.add(link.attr("abs:href"));
            }
        }
        return hrefs;
    }
}