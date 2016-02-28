package fyp.parser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Pack200;

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
    private String logFolder = "log/";
    private String saveDes = "../data/grant/";
    public Crawler(String url) {
        this.urlStr = url;
        this.firstLinkText = "Patent Grant Full Text Data/XML";
        this.secondLinkText = "ipg";
        this.fileHref = new ArrayList<>();
    }

    public void run(int limit){
        try {
            List <String> googleLinks = extractLink(this.googleUrl, this.secondLinkText);
            List <String> usptoLinks = extractLink(this.urlStr, this.firstLinkText);
            List <String> yearLinks;
            for (String link: usptoLinks) {
                System.out.println(link);
                yearLinks = extractLink(link, secondLinkText);
                for(String s: yearLinks){
                    if(limit <= 0)
                        return;
                    String filename = getFileName(s);
                    int id = check(googleLinks, filename);
                    if(id != -1){
                        boolean res = download(googleLinks.get(id), saveDes, filename);
                        if(res) limit--;
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

    public boolean download(String urlString, String destination, String filename){
        // 1. if log file exists, file already downloaded and parsed or parsing
        // 2. if xml exists, file already downloaded and parsing or waiting for parsing
        // 3. if zip exists but no xml, re-download
        //name without extension
        String name = filename.substring(0, filename.indexOf("."));
        System.out.println("Downloading file at " + urlString + " to " + destination + filename);
        Helper.writeLog("log/download.log", "Downloading file at " + urlString + " to " + destination + filename, true);
        File logfile = new File(logFolder + name + ".log");
        if(logfile.exists()){
            System.out.println("File was already downloaded");
            Helper.writeLog("log/download.log", "Aborted, file was already downloaded and parsed!", true);
            return false;
        }
        URL url;
        File file = new File(destination + filename);
        File xmlFile = new File(destination + name + ".xml");
        if(xmlFile.exists()){
            //xml alr exist, waiting to be parsed, ignore
            System.out.println("File was already downloaded");
            Helper.writeLog("log/download.log", "Aborted, file was already downloaded and unzipped!", true);
            return false;
        }
        try {
            url = new URL(urlString);
            FileUtils.copyURLToFile(url, file, 3000, 600000);
            System.out.println("Unzipping .... ");
            Helper.writeLog("log/download.log", "Unzipping file ... ", true);
            Helper.executeShellCommand("unzip -x " + destination + filename + " -d ../data/grant");
            System.out.println("Done!");
            Helper.writeLog("log/download.log", "Done - file " + file.getName() + " downloaded and unzipped!", true);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
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