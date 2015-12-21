package fyp.utils;

import java.util.Iterator;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.Arrays;
import java.io.FilenameFilter;

public class Helper{
    public static String [] listDir(String addr){
        //list  all directories in a given directory
        File file = new File(addr);
        String [] dirs = file.list(new FilenameFilter(){
            public boolean accept(File current, String name){
                return new File(current, name).isDirectory();
            }
        });
        return dirs;
    }
    public static String removeStopWords(String s){
        StringBuilder b = new StringBuilder();
        for(String word: s.split("\\W+")){
            if(!StopWords.isStopWord(word) && !isNumeric(word) && !containsNumber(word)){
                b.append(word.toLowerCase() + " ");
            }
        }
        return b.toString();
    }
    public static boolean isNumeric(String word){
        return word.matches("-?\\d+(\\.\\d+)?");
    }
    public static boolean containsNumber(String w){
        return w.matches(".*\\d.*");
    }
    public static String removeNewLine(String s){
        return s.replaceAll("\\r\\n|\\r|\\n", " ");
    }
    public static void writeLog(String s){
        try{
            File file = new File("log");
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file.getName(), true);
            BufferedWriter bw = new BufferedWriter(writer);
            bw.write(s);
            bw.newLine();
            bw.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}