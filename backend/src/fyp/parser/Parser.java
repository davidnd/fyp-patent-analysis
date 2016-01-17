package fyp.parser;

import fyp.models.Patent;
import fyp.utils.Helper;
import fyp.utils.DatabaseConnector;

import java.io.File;

public abstract class Parser{

    public abstract Patent parse(String path);

    public void parseDir(String path, DatabaseConnector db){
        File dirs = new File(path);
        File [] files = dirs.listFiles();
        for (File f: files) {
            if(f.isFile() && Helper.isXML(f.getName())){
                System.out.println("Parsing " + f.getName());
                Patent p = parse(f.getAbsolutePath());
                if(p == null) continue;
                p.clean();
                db.insertPatent(p);
            }
            else if(f.isDirectory())
                parseDir(f.getAbsolutePath(), db);
        }
    }
}