package fyp.utils;

import java.sql.*;
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
    public void query(String sql){
        // this.stmt = connection.createStatement();

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
    private void close(){
        try{
            if(this.rs != null) this.rs.close();
            if(this.stmt != null) this.stmt.close();
            if(this.pStmt != null) this.pStmt.close();
            if(this.connection != null) this.connection.close();
        }
        catch(Exception e){
            System.out.println("Could not close the connection!");
        }
    }
}