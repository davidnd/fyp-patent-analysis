package fyp.parser;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import fyp.utils.DatabaseConnector;
import fyp.models.Patent;
import fyp.utils.Helper;

class Main{
    public static void main(String[] args) {
        List <USPTOParser> parsers = new ArrayList <USPTOParser>();
        String root = "../data/grant";
        File dirs = new File(root);
        System.out.println(dirs.getAbsolutePath());
        File [] files = dirs.listFiles();
        for (File f: files) {
            if(f.isFile() && Helper.hasExtension(f.getName(), "xml")){
                // DatabaseConnector connector = new DatabaseConnector("fyp");
                // connector.connect();
                parsers.add(new USPTOParser(f.getAbsolutePath()));
            }
        }
    }
}