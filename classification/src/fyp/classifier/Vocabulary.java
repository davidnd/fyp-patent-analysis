package fyp.classifier;

import java.util.HashSet;
import java.io.*;

public class Vocabulary{
    public static HashSet <String> vocabulary = new HashSet <String>();
    public static final int size = 277207;
    public static final String saveDir = "../models/vocabulary.ser";
    public static void printVocabulary(){
        System.out.println("VOCABULARY: ");
        for(String s: vocabulary){
            System.out.println(s);
        }
    }
    public static void save(){
        try{
            File file = new File(saveDir);
            file.getParentFile().mkdirs();
            if(!file.exists()){
                file.createNewFile();
            }
            FileOutputStream fo = new FileOutputStream(file, false);
            ObjectOutputStream out = new ObjectOutputStream(fo);
            out.writeObject(Vocabulary.vocabulary);
            out.close();
            fo.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}