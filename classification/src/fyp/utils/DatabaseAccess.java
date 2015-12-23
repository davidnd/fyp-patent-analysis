package fyp.utils;
import fyp.models.Patent;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;
import fyp.models.ClassModel;
import fyp.classifier.Classifier;
public class DatabaseAccess{
    public DatabaseAccess(){
    }

    public static List <String> getAllSubclasses(){
        List <String> classes = new ArrayList <String>();
        DatabaseConnector connector = new DatabaseConnector();  
        String sql = "SELECT distinct subclass from wipo.train";
        try(
            Connection conn = connector.createConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
        ){
            while(rs.next()){
                classes.add(rs.getString("subclass"));
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            connector.close();
        }
        return classes;
    }
    public static int getPatentCount(){
        String sql = "SELECT count(*) from wipo.train";
        DatabaseConnector connector = new DatabaseConnector();
        try(Connection conn = connector.createConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
        ){
            if(rs.next()){
                return rs.getInt(1);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        finally{
            connector.close();
        }
        return -1;
    }
    public static void buildWordMap(ClassModel model){
        String symbol = model.getSymbol();
        // String sql = "SELECT title from wipo.train where subclass=?";
        String sql = "SELECT title, abstract, text, claims from wipo.train where subclass = ?";
        DatabaseConnector connector = new DatabaseConnector();
        int count = 0;
        try(
            Connection conn = connector.createConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
        ){
            stmt.setString(1, symbol);
            conn.setAutoCommit(false);
            stmt.setFetchSize(200);
            try(
                ResultSet rs = stmt.executeQuery();
            ){
                while(rs.next()){
                    count++;
                    String title = rs.getString("title");
                    String abs = rs.getString("abstract");
                    String text = rs.getString("text");
                    String claims = rs.getString("claims");
                    Patent p = new Patent();
                    p.setTitle(title);
                    p.setText(text);
                    p.setAbstract(abs);
                    p.setClaims(claims);
                    model.process(p);
                }
                model.setPrior(count * 1.0 /Classifier.patentCount);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            connector.close();
        }
    }
}