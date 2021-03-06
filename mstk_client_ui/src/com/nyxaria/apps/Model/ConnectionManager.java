package com.nyxaria.apps.Model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nyxaria.apps.Controller.HttpUtility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConnectionManager {

    public static final String baseUrl = "http://machinetimed_server:6000";
    public static String apikey;
    public static String urlWtihApiKey;

    public static HashMap<String, String> login(String rfid) {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", rfid);

        return POST(baseUrl + "/machine", params);

    }

    public static HashMap<String, String> finaliseJob(String rfid, String timeSec) {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", rfid);
        params.put("jobtime", timeSec);

        return POST(baseUrl + "/machine/job", params);
    }

    public static ArrayList<HashMap<String, String>> getMeta() {
        return GET(baseUrl + "/environment");
    }

    public static ArrayList<HashMap<String, String>> getHistory(String rfid, int pageIndex) {
        return GET(baseUrl + "/machine/job?uuid="+rfid+"&page="+(pageIndex+1));
    }

    public static ArrayList<HashMap<String, String>> GET(String url) {
        ArrayList<HashMap<String, String>> result = new ArrayList<>();
        if (url.indexOf('?') >= 0) {
            urlWtihApiKey = url.concat("&apikey=" + apikey);
        } else {
            urlWtihApiKey = url.concat("?apikey=" + apikey);
        }
        try {
            HttpUtility.sendGetRequest(urlWtihApiKey);
            String response = HttpUtility.readSingleLineRespone().replace("]","");
            System.out.println(response);
            ArrayList<String> historyRaw = new ArrayList<>();
            while(response.contains("}")) {
                historyRaw.add(response.substring(response.indexOf("{"), response.indexOf("}")+1));
                //System.out.println(response.substring(response.indexOf("{"), response.indexOf("}")+1));
                response = response.substring(response.indexOf("}")+1);
            }
            for(String s : historyRaw) {
                result.add(new ObjectMapper().readValue(s, HashMap.class));
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        HttpUtility.disconnect();

        return result;
    }

    public static HashMap<String, String> POST(String url, Map<String, String> params) {
        HashMap<String,String> result = null;
        params.put("apikey", apikey);

        try {
            HttpUtility.sendPostRequest(url, params);
            String response = HttpUtility.readSingleLineRespone();
            System.out.println(response);
            result = new ObjectMapper().readValue(response, HashMap.class);
//            for(Map.Entry<String, String> entry : result.entrySet()) {
//                System.out.println(entry.getKey() + " = " + entry.getValue());
//            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        HttpUtility.disconnect();

        return result;
    }
}
