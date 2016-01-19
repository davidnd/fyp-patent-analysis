package fyp.parser;

import fyp.models.Patent;
import fyp.utils.Helper;
import fyp.utils.DatabaseConnector;

import java.io.File;

public abstract class Parser{

    public abstract Patent parse(String content);
    public abstract Patent parse(File file);
    public abstract void parseDir(String path, DatabaseConnector db);
}