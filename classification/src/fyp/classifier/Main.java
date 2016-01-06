package fyp.classifier;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;

import fyp.utils.Helper;
import fyp.models.Result;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.net.InetSocketAddress;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import java.net.URLDecoder;
public class Main{
    public static int TOP_K_CLASS = 10;
    public static Classifier classifier;
    public static void main(String[] args) throws Exception{
        classifier = new Classifier();
        // classifier.train();
        classifier.loadModels();
        // Scanner in = new Scanner(System.in);
        // System.out.println("Run (0/1): ");
        // while(in.nextInt() != 0){
        //     List <Result> res = classifier.classify(Helper.readFile("/Users/phucnguyen/programming/fyp-patent-analysis/classification/src/fyp/utils/test.txt"));
        //     for (int i = 0; i < TOP_K_CLASS; i++) {
        //         System.out.println(res.get(i).getClassSymbol() + " " + res.get(i).getResult());
        //     }
        //     System.out.println("Run again (0/1): ");
        // }
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/classify", new MyHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("server is running on port 8000");
    }
    static class MyHandler implements HttpHandler{
        public void handle(HttpExchange t) throws IOException{
            InputStream is = t.getRequestBody();
            Map <String, Object> params = new HashMap <String, Object>();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();
            parseQuery(query, params);
            for (Map.Entry <String, Object> entry: params.entrySet() ) {
                String key = entry.getKey();
                Object value = entry.getValue();
                System.out.println("Key = " + key + "; Value = " + value);
            }
            List <Result> res = null;
            if(params.containsKey("text")){
                String text = (String) params.get("text");
                res = Main.classifier.classify(text);
            }
            JSONObject response = new JSONObject();
            JSONArray list = new JSONArray();
            for(int i = 0; i < Main.TOP_K_CLASS && i < res.size(); i++){
                JSONObject temp = new JSONObject();
                temp.put("class", res.get(i).getClassSymbol());
                temp.put("probability", res.get(i).getResult());
                list.add(temp);
            }
            response.put("results", list);
            t.sendResponseHeaders(200, response.toJSONString().length());
            OutputStream os = t.getResponseBody();
            os.write(response.toJSONString().getBytes());
            os.close();
        }
    }
    static void parseQuery(String query, Map <String, Object> params) {
        try{
            if(query != null){
                String pairs [] = query.split("[&]");
                for (String pair: pairs) {
                    String param[] = pair.split("[=]");
                    String key = null;
                    String value = null;
                    if(param.length > 0){
                        key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));
                    }
                    if(param.length > 1){
                        value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
                    }
                    if(params.containsKey(key)){
                        Object ob = params.get(key);
                        if(ob instanceof List<?>){
                            List <String> values = (List<String>) ob;
                            values.add(value);
                        }
                        if(ob instanceof String){
                            List <String> values = new ArrayList<String>();
                            values.add((String)ob);
                            values.add(value);
                            params.put(key, values);
                        }
                    }
                    else{
                        params.put(key, value);
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}