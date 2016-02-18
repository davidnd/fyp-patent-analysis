package fyp.classifier;

import java.util.List;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.File;
import java.sql.*;
import java.util.HashSet;
import java.util.Collections;
import java.util.Comparator;

import fyp.utils.DatabaseConnector;
import fyp.utils.DatabaseAccess;
import fyp.models.*;
import fyp.utils.Helper;

public class Classifier{
    public static int patentCount;
    public static int THRES_HOLD = 10;
    String dir = "../models/";
    List <ClassModel> models;
    public Classifier(){
        this.models = new ArrayList <ClassModel> ();
        // loadModels();
    }
    public void train(){
        List <String> subclasses = DatabaseAccess.getAllSubclasses();
        int total = DatabaseAccess.getPatentCount();
        if(total < 0){
            System.out.println("Failed to get patent count");
            System.exit(0);
        }
        else{
            Classifier.patentCount = total;
        }
        ClassModel model = null;
        for(String sb: subclasses){
            System.out.println("Processing subclass: " + sb);
            model = new ClassModel(sb);
            DatabaseAccess.buildWordMap(model);
            model.save();
            System.out.println("Word count: " + model.getCount());
            System.out.println("Vocabulary size: " + Vocabulary.vocabulary.size());
        }
        Vocabulary.save();
    }
    public List<Result> classify(String text){
        text = Helper.removeNewLine(text);
        text = Helper.removeStopWords(text);
        List <Result> results = new ArrayList <Result> ();
        for (ClassModel model: this.models) {
            double s = model.getPrior();
            for (String w: text.split("\\s+")) {
                Integer count = model.getWordCount().get(w);
                if(count == null) count = 0;
                s += Math.log( ((double)(count + 1)) / (model.getCount() + Vocabulary.size) );
            }
            results.add(new Result(s, model.getSymbol()));
        }
        Collections.sort(results, new Comparator <Result> (){
            public int compare(Result a, Result b){
                if(b.getResult() > a.getResult()) return 1;
                if(b.getResult() < a.getResult()) return -1;
                return 0;
            }
        });
        return results;
    }
    public void update(List <TestResult> rs){
        PreparedStatement pstmt = null;
        DatabaseConnector connector = new DatabaseConnector();
        try{
            Connection conn = connector.createConnection();
            String sql = "UPDATE wipo.test set classified = ? where id = ?";
             pstmt = conn.prepareStatement(sql);
            for (TestResult r: rs ) {
                int id = r.getId();
                String classified = r.getClassifiedLabel();
                pstmt.setString(1, classified);
                pstmt.setInt(2, id);
                pstmt.executeUpdate();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(pstmt!=null) try{pstmt.close();}catch(Exception e){e.printStackTrace();}
            if(connector!=null) try{connector.close();}catch(Exception e){e.printStackTrace();}
        }
        
    }
    public void test(){
        int total = 28923, id = 0;
        String sql = "SELECT * from wipo.test where classified is null limit 100";
        DatabaseConnector connector = new DatabaseConnector();
        Statement stmt = null;
        ResultSet rs = null;
        while(id < total){
            List <TestResult> testResults = new ArrayList <TestResult> ();
            try{
                Connection conn = connector.createConnection();
                // conn.setAutoCommit(false);
                stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                stmt.setFetchSize(1000);
                rs = stmt.executeQuery(sql);
                rs.beforeFirst();
                while (rs.next()){
                    id = rs.getInt("id");
                    System.out.println("Testing patent id = " + id);
                    String title = rs.getString("title");
                    String abs = rs.getString("abstract");
                    String text = rs.getString("text");
                    String claims = rs.getString("claims");
                    String allText = title + " " + abs + " " + text + " " + claims;
                    List <Result> results = classify(allText);
                    TestResult a = new TestResult(id);
                    a.setResults(results.subList(0, Classifier.THRES_HOLD));
                    testResults.add(a);
                    // rs.updateRow();
                }
            }
            catch(Exception e){
                e.printStackTrace();
                connector.close();
            }finally{
                if(rs != null)try{rs.close();}catch(Exception e){e.printStackTrace();connector.close();}
                if( stmt != null )try{stmt.close();}catch(Exception e){e.printStackTrace();connector.close();}
                update(testResults);
            }
        }
        
    }
    public void loadModels(){
        File folder = new File(this.dir);
        File [] files = folder.listFiles();
        for (int i = 0; i < files.length ; i++ ) {
            if(files[i].isFile() && Helper.isModel(files[i])){
                load(files[i].getAbsolutePath());
            }
        }
    }
    public void load(String filePath){
        try{
            FileInputStream in = new FileInputStream(filePath);
            ObjectInputStream oi = new ObjectInputStream(in);
            ClassModel newModel = (ClassModel) oi.readObject();
            System.out.println("Loaded " + newModel.getSymbol());
            // System.out.println("Prior p: " + newModel.getPrior());
            this.models.add(newModel);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}