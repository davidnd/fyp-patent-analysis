package fyp.utils;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import fyp.models.Patent;

public class DatabaseConnector{
    private String URL = "jdbc:mysql://localhost/wipo";
    private String USER = "root";
    private String PASS = "root";
    private Connection connection;
    private Statement stmt;
    private PreparedStatement pStmt;
    private ResultSet rs;
    public DatabaseConnector(String url, String user, String pass){
        this.URL = url;
        this.USER = user;
        this.PASS = pass;
    }
    public DatabaseConnector(){};
    public void connect(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection(this.URL, this.USER, this.PASS);
            this.stmt = this.connection.createStatement();
        }
        catch(Exception e){
            e.printStackTrace();
            close();
        }
    }
    public Connection createConnection(){
        if(this.connection != null){
            return this.connection;
        }
        else{
            connect();
            return this.connection;
        }
    }
    public List <Patent> queryPatent(String sql){
        List <Patent> res = new ArrayList <Patent>();
        try{
            this.stmt = connection.createStatement();
            this.rs = this.stmt.executeQuery(sql);
            while(this.rs.next()){
                Patent p = new Patent();
                p.setTitle(this.rs.getString("title"));
                p.setText(this.rs.getString("text"));
                p.setClaims(this.rs.getString("claims"));
                p.setAbstract(this.rs.getString("abstract"));
                res.add(p);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            close();
        }
        return res;
    }
    public void insertPatent(Patent p){
        String sql = "INSERT into wipo.train(title, abstract, text, claims, section, class, subclass, maingroup) VALUES(?,?,?,?,?,?,?,?)";
        try{
            this.pStmt = this.connection.prepareStatement(sql);
            pStmt.setString(1, p.getTitle());
            pStmt.setString(2, p.getAbstract());
            pStmt.setString(3, p.getText());
            pStmt.setString(4, p.getClaims());
            pStmt.setString(5, p.getSectionCode());
            pStmt.setString(6, p.getClassCode());
            pStmt.setString(7, p.getSubclassCode());
            pStmt.setString(8, p.getGroupCode());
            this.pStmt.executeUpdate();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            close();
        }
    }
    public void close(){
        if(this.rs != null) 
            try{
                this.rs.close();
            } 
            catch(Exception e){e.printStackTrace();};
        if(this.stmt != null) 
            try{
                this.stmt.close();
            } 
            catch(Exception e){e.printStackTrace();};
        if(this.pStmt != null) 
            try{
                this.pStmt.close();
            } 
            catch(Exception e){e.printStackTrace();};
        if(this.connection != null) 
            try{
                this.connection.close();
            } 
            catch(Exception e){e.printStackTrace();};
    }
}