import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
public class Crawler{
    private String destination;
    private String urlStr;
    
    public String getContent(){
        URL url;
        InputStream is = null;
        BufferedReader br = null;
        String line = null;
        StringBuilder sb = null;
        try{
            url = new URL(this.urlStr);
            is = url.openStream();
            sb = new StringBuilder();
            br = new BufferedReader(new InputStreamReader(is));
            while((line = br.readLine()) != null){
                sb.append(line);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            try{
                if(is != null) is.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}