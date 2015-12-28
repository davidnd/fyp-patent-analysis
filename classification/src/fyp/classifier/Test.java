package fyp.classifier;
import fyp.utils.DatabaseConnector;
import fyp.models.Report;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Test{
    public static void main(String[] args) {
        Classifier classifier = new Classifier();
        classifier.loadModels();
        classifier.test();
        report();
    }
    public static void report(){
        System.out.println("Producing report...");
        DatabaseConnector connector = new DatabaseConnector();
        Statement stmt = null;
        ResultSet rs = null;
        int correct1 = 0, wrong1=0, correct3 = 0, wrong3 = 0, correct5 = 0, wrong5 = 0, correct10=0, wrong10=0;
        double rate1, rate3, rate5, rate10;
        List <Report> reports = new ArrayList <Report>();
        String sql = "SELECT subclass, classified from wipo.test where classified is not null";
        try{
            Connection conn = connector.createConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            Report temp;
            String label;
            String classified;
            List <String> predict = new ArrayList <String> ();
            while(rs.next()){
                label = rs.getString("subclass");
                classified = rs.getString("classified");
                predict = Arrays.asList(classified.split("\\s*;\\s*"));
                temp = new Report(label, predict);
                reports.add(temp);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        finally{
            if(stmt != null) try{stmt.close();} catch(Exception e){e.printStackTrace();}
            if(rs != null) try{rs.close();} catch(Exception e){e.printStackTrace();}
            connector.close();
        }
        for (Report r: reports) {
            if(r.check(3)) correct3++; else wrong3++;
            if(r.check(1)) correct1++; else wrong1++;
            if(r.check(5)) correct5++; else wrong5++;
            if(r.check(10)) correct10++; else wrong10++;
        }
        System.out.println("Size of testing: " + reports.size() + " documents");
        rate1 = (double) correct1 / (correct1 + wrong1);
        rate3 = (double) correct3 / (correct3 + wrong3);
        rate5 = (double) correct5 / (correct5 + wrong5);
        rate10 = (double) correct10 / (correct10 + wrong10);
        System.out.println("Top 1: " + rate1);
        System.out.println("Top 3: " + rate3);
        System.out.println("Top 5: " + rate5);
        System.out.println("Top 10: " + rate10);
    }
}