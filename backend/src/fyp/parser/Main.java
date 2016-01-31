package fyp.parser;

import java.io.File;
import fyp.utils.DatabaseConnector;
import fyp.models.Patent;

class Main{
    public static void main(String[] args) {
        DatabaseConnector connector = new DatabaseConnector("patent");
        connector.connect();
        String root = "../../data/grant";
        Parser parser = new USPTOParser();
        // parser.parseDir(root, connector);
        File file = new File(root);
        parser.parseDir(root, connector);
    } 
}