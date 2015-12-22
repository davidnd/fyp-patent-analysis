package fyp.utils;
import fyp.models.Patent;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;
import fyp.models.ClassModel;
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
    public static void buildWordMap(ClassModel model){
        String symbol = model.getSymbol();
        // String sql = "SELECT title from wipo.train where subclass=?";
        String sql = "SELECT title, abstract, text, claims from wipo.train where subclass = ?";
        DatabaseConnector connector = new DatabaseConnector();
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