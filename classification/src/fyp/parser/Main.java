package fyp.parser;

import java.io.File;
import fyp.utils.DatabaseConnector;
import fyp.models.Patent;

class Main{
    public static void main(String[] args) {
        DatabaseConnector connector = new DatabaseConnector();
        connector.connect();
        String root = "../../data/wipo-alpha/wipo-alpha/test/";
        Parser.parseDir(root, connector);
    } 
}