package fyp.utils;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.io.StringWriter;
import java.io.PrintWriter;
import fyp.models.Patent;

public class DatabaseConnector{
    private String DATABASE = "wipo";
    private String URL = "jdbc:mysql://localhost/";
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
    public DatabaseConnector(String database){
        this.DATABASE = database;
        this.URL += database;
    }
    public DatabaseConnector(){
        this.URL += this.DATABASE;
    };
    public void connect(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection(this.URL, this.USER, this.PASS);
        }
        catch(Exception e){
            e.printStackTrace();
            close();
        }
    }
    public void setAutoCommit (boolean b) throws SQLException{
        if(this.connection == null) return;
        this.connection.setAutoCommit(b);
    }
    public void commit() throws SQLException{
        if(this.connection == null) return;
        this.connection.commit();
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
    public void insertPatent(List <Patent> patents, String table){
        if(connection == null){
            System.out.println("No connection");
            return;
        }
        if(patents.size() == 0){
            System.out.println("Empty patents array");
            return;
        }
        String sql = "INSERT into " + this.DATABASE + "." + table 
        + "(docid, title, abstract, description, claims, ipccode, date, inventors, assignees, city, country) " 
        + "VALUES(?,?,?,?,?,?,?,?,?,?,?)";
        try{
            this.connection.setAutoCommit(false);
            this.pStmt = this.connection.prepareStatement(sql);
            for(Patent p: patents){
                if(p.getTitle() != null && p.getTitle().length() > 250) p.setTitle(p.getTitle().substring(0,250));
                if(p.getInventor() != null && p.getInventor().length() > 1025){
                    String []in = p.getInventor().split(";");
                    String res = "";
                    for(int i = 0; i<in.length; i++){
                        if(res.length() + in[i].length() > 1025){
                            break;
                        }
                        res += in[i];
                    }
                    p.setInventor(res);
                }
                if(p.getCompany() != null && p.getCompany().length() > 1025){
                    String []in = p.getCompany().split(";");
                    String res = "";
                    for(int i = 0; i<in.length; i++){
                        if(res.length() + in[i].length() > 1025){
                            break;
                        }
                        res += in[i];
                    }
                    p.setCompany(res);
                }
                if(p.getIPC() != null && p.getIPC().length() > 1025){
                    String []in = p.getIPC().split(";");
                    String res = "";
                    for(int i = 0; i<in.length; i++){
                        if(res.length() + in[i].length() > 1025){
                            break;
                        }
                        res += in[i];
                    }
                    p.setIPC(res);
                }
                pStmt.setString(1, p.getDocId());
                pStmt.setString(2, p.getTitle());
                pStmt.setString(3, p.getAbstract());
                pStmt.setString(4, p.getText());
                pStmt.setString(5, p.getClaims());
                pStmt.setString(6, p.getIPC());
                pStmt.setDate(7, p.getDate());
                pStmt.setString(8, p.getInventor());
                pStmt.setString(9, p.getCompany());
                pStmt.setString(10, p.getCity());
                pStmt.setString(11, p.getCountry());
                this.pStmt.addBatch();
            }
            pStmt.executeBatch();
            this.connection.commit();
        }
        catch(Exception e){
            e.printStackTrace();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            Helper.writeLog("fyp/log/dbstacktrace.log", sw.toString(), true);
        }
        finally{
            if(this.pStmt != null){
                try{this.pStmt.close();}
                catch(Exception e){e.printStackTrace();}
            }
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