package com.insightdev.both;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

public class Actions {

    private static String actions = "{}";

    public static void addActions(String key, String value,Context context){

        ArrayList<Pair<String,String>> data = new ArrayList<>();
        data.add(new Pair<>(Tools.getString(R.string.key,context),key));
        data.add(new Pair<>(Tools.getString(R.string.value,context),value));

        ArrayList<String> array = getActionsArray(context);
        boolean exist = false;
        for (int i = 0; i < array.size(); i++) {
            if (Tools.getValue(array.get(i), Tools.getString(R.string.value,context)).contentEquals(value)){
                array.set(i, Tools.getJson(data));
                exist = true;
                break;
            }
        }
        if(!exist)
            array.add(Tools.getJson(data));

        Gson gson = new Gson();
        actions = gson.toJson(array);
        saveActions(context);
    }

    public static void removeActions(Context context){
        actions = "{}";
        saveActions(context);
    }

    public static ArrayList<String> getActionsArray(Context context){

        actions = loadJsonActions(context);
        ArrayList<String> array = new ArrayList<>();
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(actions);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < (jsonArray != null ? jsonArray.length() : 0); i++) {
            try {
                array.add(jsonArray.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return array;
    }

    private static void saveActions(Context context){
        SharedPreferences preferences = null;
        try {
            preferences = Tools.getEncryptedSharedPreferences(context,
                    Tools.getString(R.string.actions,context));
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Tools.getString(R.string.actions,context), actions);

        editor.apply();
    }

    private static String loadJsonActions(Context context){
        SharedPreferences preferences = null;
        try {
            preferences = Tools.getEncryptedSharedPreferences(context,Tools.getString(R.string.actions,context));
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        return preferences.getString(Tools.getString(R.string.actions,context),"{}");

    }


}
