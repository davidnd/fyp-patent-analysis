package fyp.query;

import fyp.models.Patent;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
// import java.net.MalformedURLException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

public class ESIndexer {
    public ESIndexer(){

    }
    public static boolean index (Patent p){
        JSONObject obj = new JSONObject();
        obj.put("title", p.getTitle());
        obj.put("abstract", p.getAbstract());
        obj.put("text", p.getText());
        obj.put("claims", p.getClaims());
        obj.put("date", p.getDate().toString());
        JSONArray companyList = new JSONArray();
        if(p.getCompany() != null){
            String [] companies = p.getCompany().split(";");
            for(int i = 0; i<companies.length; i++){
                companyList.add(companies[i]);
            }
        }
        obj.put("company", companyList);
        JSONArray inventorList = new JSONArray();
        if(p.getInventor() != null){
            String [] inventors = p.getInventor().split(";");
            for(int i = 0; i<inventors.length; i++){
                inventorList.add(inventors[i]);
            }    
        }
        obj.put("inventors", inventorList);
        obj.put("city", p.getCity());
        obj.put("country", p.getCountry());
        JSONArray ipcList = new JSONArray();
        if(p.getIPC() != null){
            String [] ipcs = p.getIPC().split(";");
            for(int i = 0; i<ipcs.length; i++){
                ipcList.add(ipcs[i]);
            }
        }
        obj.put("ipcs", ipcList);
        String putUrl = "http://localhost:9200/patents/uspto/";
        try{
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPut putReq = new HttpPut(putUrl + p.getDocId());
            System.out.println(obj.toJSONString());
            StringEntity input = new StringEntity(obj.toJSONString());
            input.setContentType("application/json");
            putReq.setEntity(input);
            HttpResponse response = httpClient.execute(putReq);
            if(response.getStatusLine().getStatusCode() != 201){
                System.out.println("Post request failed");
                System.out.println(response);
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String output;
            while((output = br.readLine())!= null){
                System.out.println(output);
            }
            httpClient.getConnectionManager().shutdown();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
    public static void index (List <Patent> patents){
        for(Patent p: patents){
            index(p);
        }
    }
}