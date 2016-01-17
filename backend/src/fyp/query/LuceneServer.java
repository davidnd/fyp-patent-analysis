package fyp.query;
import java.io.IOException;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.queryparser.classic.ParseException;


public class LuceneServer{
    String indexDir = "../index";
    Indexer indexer;
    Searcher searcher;
    public static void main(String[] args) {
        LuceneServer server;
        try{
            server = new LuceneServer();
            // server.createIndex();
            server.search();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void createIndex() throws IOException{
        indexer = new Indexer(indexDir);
        int count = indexer.createIndex();
        System.out.println("Indexed " + count + " patents");
        indexer.close();
    }
    public void search() throws IOException, ParseException{
        String query = "david";
        searcher = new Searcher(indexDir);
        TopDocs hits = searcher.search(query);
        System.out.println("Hits length: " + hits.totalHits);
        ScoreDoc [] docs = hits.scoreDocs;
        for (int i = 0; i<docs.length ; i++) {
            int docid = docs[i].doc;
            Document d = searcher.getDocument(docid);
            System.out.println((i+1) + " " + d.get("title"));
        }
        searcher.close();
    }
}