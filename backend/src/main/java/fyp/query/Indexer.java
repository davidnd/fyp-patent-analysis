package fyp.query;

import fyp.utils.Helper;
import fyp.utils.DatabaseConnector;
import fyp.models.Patent;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.*;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Indexer{

    private IndexWriter writer;

    public Indexer(String indexDir) throws IOException{
        File file = new File(indexDir);
        file.getParentFile().mkdirs();
        Directory indexDirectory = FSDirectory.open(Paths.get(indexDir));
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        writer = new IndexWriter(indexDirectory, config);
    }

    public void close() throws CorruptIndexException, IOException{
        writer.close();
    }

    private void indexPatent(Patent p) throws IOException{
        Document doc = createDocument(p);
        writer.addDocument(doc);
    }
    private Document createDocument(Patent p){
        Document doc = new Document();
        doc.add(new TextField("title", p.getTitle(), Field.Store.YES));
        doc.add(new TextField("abstract", p.getAbstract(), Field.Store.YES));
        doc.add(new TextField("text", p.getText(), Field.Store.YES));
        doc.add(new TextField("claims", p.getClaims(), Field.Store.NO));
        doc.add(new TextField("text", p.getText(), Field.Store.YES));
        doc.add(new StringField("inventor", p.getInventor(), Field.Store.YES));
        doc.add(new TextField("company", p.getCompany(), Field.Store.YES));
        doc.add(new StringField("a_city", p.getCity(), Field.Store.YES));
        doc.add(new StringField("a_country", p.getCountry(), Field.Store.YES));
        return doc;
    }
    public int createIndex(){
        String sql = "SELECT * from patent_query limit 1000";
        DatabaseConnector connector = new DatabaseConnector("patent");
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        int count = 0;
        try{
            conn = connector.createConnection();
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setFetchSize(1000);
            rs = stmt.executeQuery(sql);
            rs.beforeFirst();
            while(rs.next()){
                String title = rs.getString("title");
                String abs = rs.getString("abstract");
                String text = rs.getString("description");
                String claims = rs.getString("claims");
                Date date = rs.getDate("date");
                String orgname = rs.getString("orgname");
                String a_city = rs.getString("city");
                String a_country = rs.getString("country");
                String i_lastname = rs.getString("lastname");
                String i_firstname = rs.getString("firstname");
                String i_city = rs.getString("inventor_city");
                String i_country = rs.getString("inventor_country");
                Patent p = new Patent();
                p.setTitle(title);
                p.setAbstract(abs);
                p.setText(text);
                p.setClaims(claims);
                p.setCompany(orgname);
                p.setInventor(i_firstname + " " + i_lastname);
                p.setCity(a_city);
                p.setCountry(a_country);
                p.setDate(date);
                indexPatent(p);
                count++;
            }
        }
        catch(Exception e){
            System.out.println("Failed in creating index");
            e.printStackTrace();
        }
        finally{
            if(rs != null)try{rs.close();}catch(Exception e){e.printStackTrace();}
            if( stmt != null )try{stmt.close();}catch(Exception e){e.printStackTrace();}
            if(connector!= null) connector.close();
        }
        return count;
    }
}