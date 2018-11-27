package com.liyyang.yunnote.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by leeyang on 16/7/19.
 */
public class MyJsonUtil extends JSONObject {

    public JSONObject tJson(JSONArray array,JSONObject jb){
        try {
            jb.put("cat","ynote");
            jb.put("note",array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
       return jb;
    }

    public JSONArray mJson(JSONArray array,String title, String content, String size, String dataid, String data){
        try {

            JSONObject lan1 = new JSONObject();

            lan1.put("title",title);
            lan1.put("content",content);
            lan1.put("size",size);
            lan1.put("dataid",dataid);
            lan1.put("data",data);

            array.put(lan1);

            return array;


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
