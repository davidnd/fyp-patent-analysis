package fyp.query;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

public class Searcher{
    IndexReader reader;
    IndexSearcher searcher;
    QueryParser queryParser;
    Query query;
    public Searcher(String indexDir) throws IOException{
        reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDir)));
        searcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer();
        queryParser = new QueryParser("company", analyzer);
    }
    public TopDocs search(String queryString) throws IOException, ParseException{
        query = queryParser.parse(queryString);
        return searcher.search(query, 10);
    }
    public void close() throws IOException{
        reader.close();
    }
    public Document getDocument (int id) throws IOException{

        return searcher.doc(id);
    }
}