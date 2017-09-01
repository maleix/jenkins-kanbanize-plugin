/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maleix.kanbanizeplugin;

import com.google.gson.Gson;
import java.util.HashMap;

/**
 *
 * @author collcoca
 */
public class Kanbanize {
    private HttpsRequest request;
    private Gson gson;
    
    public Kanbanize(String subdomain, String apikey) {
        request = new HttpsRequest(subdomain, apikey);
        gson = new Gson();
    }
    
    public HashMap create_new_task(HashMap details) throws Exception {
        return request.sendPost("create_new_task", gson.toJson(details));
    }

    public HashMap get_projects_and_boards() throws Exception {
        return request.sendPost("get_projects_and_boards", null);
    }

	public void delete_task(HashMap details) throws Exception {
        request.sendPost("delete_task", gson.toJson(details));
	}
}

