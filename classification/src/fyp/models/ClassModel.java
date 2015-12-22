package fyp.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import fyp.classifier.Vocabulary; 
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.File;
import java.io.Serializable;

public class ClassModel implements Serializable{
    private Map <String, Integer> wordCount;
    private String classSymbol;
    private int count;
    private static final int TITLE_WEIGHT = 20;
    private static final int ABSTRACT_WEIGHT = 3;
    private static final int TEXT_WEIGHT = 1;
    private static final int CLAIM_WEIGHT = 1;
    public static final String saveDir = "../models/";
    public ClassModel(String s){
        this.classSymbol = s;
        wordCount = new HashMap <String, Integer> ();
        count = 0;
    }

    public void save(){
        try{
            File file = new File(saveDir + classSymbol + ".ser");
            file.getParentFile().mkdirs();
            if(!file.exists()){
                file.createNewFile();
            }
            FileOutputStream fo = new FileOutputStream(file, false);
            ObjectOutputStream out = new ObjectOutputStream(fo);
            out.writeObject(this);
            out.close();
            fo.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public String getSymbol(){
        return this.classSymbol;
    }
    public void process(Patent p){
        String [] title = p.getTitle().split("\\s+");
        for (String w: title) {
            if(this.wordCount.containsKey(w))
                this.wordCount.put(w, this.wordCount.get(w) + TITLE_WEIGHT);
            else this.wordCount.put(w, TITLE_WEIGHT);
            count+= TITLE_WEIGHT;
        }

        String [] abs = p.getAbstract().split("\\s+");
        for (String w: abs) {
            if(this.wordCount.containsKey(w))
                this.wordCount.put(w, this.wordCount.get(w) + ABSTRACT_WEIGHT);
            else this.wordCount.put(w, ABSTRACT_WEIGHT);
            count += ABSTRACT_WEIGHT;
        }
        String [] text = p.getText().split("\\s+");
        for (String w: text) {
            if(this.wordCount.containsKey(w))
                this.wordCount.put(w, this.wordCount.get(w) + TEXT_WEIGHT);
            else this.wordCount.put(w, TEXT_WEIGHT);
            count += TEXT_WEIGHT;
        }
        String [] claims = p.getClaims().split("\\s+");
        for (String w: claims) {
            if(this.wordCount.containsKey(w))
                this.wordCount.put(w, this.wordCount.get(w) + CLAIM_WEIGHT);
            else this.wordCount.put(w, CLAIM_WEIGHT);
            count += CLAIM_WEIGHT;
        }
        for(Iterator<Map.Entry <String, Integer>> it = this.wordCount.entrySet().iterator(); it.hasNext();){
            Map.Entry <String, Integer> entry = it.next();
            if(entry.getValue() < 2){
                it.remove();
                count -= entry.getValue();
                continue;
            }
            Vocabulary.vocabulary.add(entry.getKey());
        }
    }
    public Map <String, Integer> getWordCount(){
        return this.wordCount;
    }
    public void printWordCount(){
        for(Map.Entry <String, Integer> entry: this.wordCount.entrySet()){
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
    public int getCount(){
        return this.count;
    }
}