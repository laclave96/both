package com.insightdev.both;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

public class PersonsToday {

    public static boolean loadPersonsToday(Context context){
        Log.d("PosPerson_","loadPersonToday");
        String persons;
        SharedPreferences preferences = null;
        try {
            preferences = Tools.getEncryptedSharedPreferences(context,Tools.getString(R.string.persons_today,context));
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        persons = preferences.getString(Tools.getString(R.string.persons_today,context),"{}");

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(persons);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<Object> profileItems = new ArrayList<>();
        ArrayList<Object> showProfileItems = new ArrayList<>();
        Gson gson = new Gson();
        Person person;
        Post post;
        String str, p, comment, address, postUrl, name, title;
        int age;
        ShowPost showPost;
        for (int i = 0; i < (jsonArray != null ? jsonArray.length() : 0); i++) {
            try {
                str = jsonArray.getString(i);
                if (str.contains("null"))
                    continue;
                p = Tools.getValue(str,"person");
                if (p.isEmpty()) {
                    person = gson.fromJson(str, Person.class);
                    profileItems.add(person);
                    showProfileItems.add(person);
                    MainActivity.publicPhotos.add(person.getPublicPhotos(context));
                }else{
                    post = gson.fromJson(str,Post.class);
                    person = gson.fromJson(post.getPerson(),Person.class);
                    profileItems.add(post);
                    comment = post.getComment();
                    address = Tools.getAddress(person.getServer(), context);
                    postUrl = Tools.getString(R.string.http, context) + address + Tools.getString(R.string.photo_path, context)
                            + person.getId() + "/" + Tools.getString(R.string.post, context) + Tools.getString(R.string.jpeg, context);
                    name = person.getName();
                    age = person.getAge();
                    title = name + ", " + age;
                    showPost = new ShowPost(postUrl,person.getProfilePhotoLow(context),title,comment);
                    showProfileItems.add(showPost);
                    MainActivity.publicPhotos.add(person.getPublicPhotos(context));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        int currentPos = PersonsToday.getPersonPosition(context);
        Log.d("PosPerson_","pos"+currentPos);
        EventBus.getDefault().post(new PersonsStoredEvent(profileItems,showProfileItems,currentPos));

        if (jsonArray != null) {
            return jsonArray.length() > 0;
        }
        return false;
    }

    public static int getPersonPosition(Context context){
        SharedPreferences preferences = null;
        try {
            preferences = Tools.getEncryptedSharedPreferences(context,Tools.getString(R.string.persons_today,context));
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        return preferences.getInt("position",0);
    }

    public static int getLastDate(Context context){
        SharedPreferences preferences = null;
        try {
            preferences = Tools.getEncryptedSharedPreferences(context,Tools.getString(R.string.persons_today,context));
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        return preferences.getInt("lastDate",-1);
    }

    public static void savePersonsToday(Context context, ArrayList<Object> persons){
        SharedPreferences preferences = null;
        try {
            preferences = Tools.getEncryptedSharedPreferences(context,
                    Tools.getString(R.string.persons_today,context));
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Tools.getString(R.string.persons_today,context), new Gson().toJson(persons));
        editor.apply();
    }

    public static void saveCurrentPos(Context context, int currentPos){
        SharedPreferences preferences = null;
        Log.d("nahsld124ada", "savePos "+currentPos);
        try {
            preferences = Tools.getEncryptedSharedPreferences(context,
                    Tools.getString(R.string.persons_today,context));
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        Log.d("PosPerson_","save"+currentPos);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("position", currentPos);
        editor.apply();
    }

    public static void saveLastDate(Context context, int lastDate){
        Tools.saveNotifiedReload(context);
        SharedPreferences preferences = null;
        try {
            preferences = Tools.getEncryptedSharedPreferences(context,
                    Tools.getString(R.string.persons_today,context));
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("lastDate", lastDate);
        editor.apply();
    }


}
