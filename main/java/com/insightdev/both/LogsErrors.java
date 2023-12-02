package com.insightdev.both;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;


import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

public class LogsErrors {

    public static void addError(Error error, Context context){
        ArrayList<String> array = getErrorsArray(context);
        array.add(new Gson().toJson(error));
        saveErrors(array,context);
    }

    public static void removeErrors(Context context){
        saveErrors(new ArrayList<>(),context);
    }

    public static ArrayList<String> getErrorsArray(Context context){

        String errors = loadErrors(context);
        ArrayList<String> array = new ArrayList<>();
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(errors);
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

    private static void saveErrors(ArrayList<String> errors, Context context){
        SharedPreferences preferences = null;
        try {
            preferences = Tools.getEncryptedSharedPreferences(context,
                    Tools.getString(R.string.errors,context));
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Tools.getString(R.string.errors,context), errors.toString());
        editor.apply();
    }

    private static String loadErrors(Context context){

        SharedPreferences preferences = null;
        try {
            preferences = Tools.getEncryptedSharedPreferences(context,
                    Tools.getString(R.string.errors,context));
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

       
        return preferences != null ? preferences.getString(Tools.getString(R.string.errors, context), "[]") : null;

    }

}
