package fyp.utils;

import java.util.Iterator;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.Arrays;
import java.io.FilenameFilter;
import java.io.BufferedReader;
import java.io.FileReader;

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
        if(s == null) return s;
        StringBuilder b = new StringBuilder();
        for(String word: s.split("\\W+")){
            if(!StopWords.isStopWord(word) && !isNumeric(word) && !containsNumber(word)){
                b.append(word.toLowerCase() + " ");
            }
        }
        return b.toString();
    }
    public static boolean isNumeric(String word){
        if(word == null) return false;
        return word.trim().toLowerCase().matches("-?\\d+(\\.\\d+)?");
    }
    public static boolean containsNumber(String w){
        if(w == null) return false;
        return w.trim().toLowerCase().matches(".*\\d.*");
    }
    public static String removeNewLine(String s){
        if(s == null) return s;
        return s.trim().replaceAll("\\r\\n|\\r|\\n", " ");
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
    public static boolean isXML(String fileName){
        if(fileName == null) return false;
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            String extension = fileName.substring(i+1);
            if(extension.equals("xml")) return true;
        }
        return false;
    }
    public static boolean isModel(File file){
        String name = file.getName();
        int i = name.lastIndexOf('.');
        if(i > 0){
            String ext = name.substring(i+1);
            String prefix = name.substring(0, i);
            if(ext.equals("ser") && prefix.length() == 4){
                return true;
            }
        }
        return false;
    }
    public static String readFile(String addr){
        String content = null;
        try(
            BufferedReader br = new BufferedReader(new FileReader(addr));
        ){
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while(line != null){
                sb.append(line);
                line = br.readLine();
            }
            content = sb.toString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }
}