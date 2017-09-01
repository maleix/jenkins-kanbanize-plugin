 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maleix.kanbanizeplugin;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import javax.net.ssl.HttpsURLConnection;



/**
 *
 * @author collcoca
 */
public class HttpsRequest {
    private String subdomain;
    private String apikey;
    
    public HttpsRequest(String subdomain, String apikey) {
        this.subdomain = subdomain;
        this.apikey = apikey;
    }

    //Check internet connection in case the http request fails
    
   public HashMap sendPost(String function, String parameters) throws Exception {
        String url = "https://" + subdomain + ".kanbanize.com/index.php/api/kanbanize/" + function + "/format/json";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        
        //add request header
        con.setRequestMethod("POST");
        con.setRequestProperty("apikey", apikey);
        con.setRequestProperty("content-type", "application/json");

        con.setDoOutput(true);
        
        System.out.println(parameters);
        if (parameters != null) {
            System.out.println("Not null");
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(parameters);
            wr.flush();
            wr.close();
        }

        int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + parameters);
            System.out.println("Response Code : " + responseCode);

            
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        //print result
        System.out.println(response.toString());

        HashMap responseFromServer = new HashMap();
        //responseFromServer.put("responseCode", responseCode);
        responseFromServer.put("response", response);

        return responseFromServer;
    }
}
