package com.insightdev.both;

import android.util.Log;

import com.google.gson.Gson;

import java.util.Map;
import java.util.TreeMap;


public class GsonMsgUtils {

    public static String serialize(Object object){
        return new Gson().toJson(object);
    }

    public static Msg deserialize(String str){
        //Log.d("XXXX_","msg"+str);
        Map<String, Class> map = new TreeMap<String, Class>();
        map.put("Msg", Msg.class);
        map.put("AudioMessage", AudioMessage.class);
        String type = Tools.getValue(str,"isA");
        Class c = map.get(type);
        return (Msg) new Gson().fromJson(str, c);
    }
}
