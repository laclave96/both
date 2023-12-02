package com.insightdev.both;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Map;

public class Profile extends Application {

    public static Person person;
    public static String profile = "{}";
    public static String pass;
    public static PublicKey publicKey = null;
    public static PrivateKey privateKey = null;

    public static void Load(Context context) {
        profile = getJsonProfile(context);
        String strPerson = Tools.getValue(profile, Tools.getString(R.string.person, context));
        person = new Gson().fromJson(strPerson, Person.class);
        pass = Tools.getValue(profile, Tools.getString(R.string.pass, context));

    }

    public static void Load(String json, String pass, Context context) {
        person = new Gson().fromJson(json, Person.class);
        profile = Tools.putValue(profile, Tools.getString(R.string.person, context), new Gson().toJson(person));
        profile = Tools.putValue(profile, Tools.getString(R.string.pass, context), pass);
        saveJsonProfile(context);
        String cant_today = Tools.getValue(json, "cant_today");
        String timestamp = Tools.getValue(json, "timestamp");
        String isPendidExpDate = Tools.getValue(json, "find_regions");

        Log.d("nasñlkdañsmd", json);


        Log.d("nasldñasd", isPendidExpDate.equals("null") + "/");
        if (!cant_today.equals("null") && !cant_today.isEmpty())
            Tools.saveSettings("iceb_cant", cant_today, context);

        if (!timestamp.equals("null") && !timestamp.isEmpty())
            Tools.saveSettings("iceb_timestamp", timestamp, context);

        if (!isPendidExpDate.equals("null") && !isPendidExpDate.isEmpty())
            Tools.saveSettings("pending_expDate", "true", context);

        if (person != null)
            Tools.saveSettings("ProfileId", getId(), context);

    }

    public static void loadOrCreateRSAKeys(Context context) {
        Pair<PublicKey, PrivateKey> keys = Tools.getRSAKeys(context);
        if (keys == null)
            return;
        if (keys.first == null) {
            Map<String, Object> newKeys = null;
            try {
                newKeys = Encryption.generateRSAKeys();
            } catch (Exception e) {
                e.printStackTrace();
            }

            privateKey = (PrivateKey) newKeys.get("private");
            publicKey = (PublicKey) newKeys.get("public");

            Tools.saveRsaKeys(publicKey, privateKey, context);

        } else {
            publicKey = keys.first;
            privateKey = keys.second;
        }


    }


    public static String getId() {
        return person.getId();
    }

    public static String getUser(Context context) {
        return person.getUsername(context);
    }

    public static String getChatUser(Context context) {
        return person.getChatUsername(context);
    }

    public static String getPass() {
        return pass;
    }

    public static String getName() {
        return person.getName();
    }

    public static String getEmail() {
        return person.getEmail();
    }

    public static String getPhone() {
        return person.getPhone();
    }

    public static String getServer() {
        return person.getServer();
    }

    public static String getCity() {
        return person.getCity();
    }

    public static String getCountry() {
        return person.getCountry();
    }

    public static String getCountryCode() {
        return person.getCountry_code();
    }

    public static String getBirth() {
        return person.getBirth();
    }

    public static String getGender() {
        return person.getGender();
    }

    public static String getSearchBy() {
        return person.getSearchBy();
    }

    public static String getOrientation() {
        return person.getOrientation();
    }

    public static boolean isShowOrientation() {
        return person.isShowOrientation();
    }

    public static String getPreferences() {
        return person.getPreferences();
    }

    public static String getOccupation() {
        return person.getOccupation();
    }

    public static String getAboutMe() {
        return person.getAboutMe();
    }

    public static String getSurvey() {
        return person.getSurvey();
    }

    public static String getLatitude() {
        return person.getLatitude();
    }

    public static String getLongitude() {
        return person.getLongitude();
    }

    public static String getPremium() {
        return person.getPremium();
    }

    public static boolean isPremium(Context context) {
        return person.isPremium(context);
    }

    public static boolean isFreePremiumConsumed(Context context) {
        return person.isFreePremiumConsumed(context);
    }


    public static String getProfilePhotoMedium(Context context) {
        return person.getProfilePhotoMedium(context);
    }

    public static String getProfilePhotoLow(Context context) {
        return person.getProfilePhotoLow(context);
    }

    public static String getSocialNetworks() {
        return person.getSocialNetworks();
    }

    public static boolean isPhotoUpLoad() {
        return person.isPhotoUpload();
    }

    public static boolean isSetupComplete() {
        return isPhotoUpLoad() && !getBirth().isEmpty() && !getCity().isEmpty()
                && !getGender().isEmpty() && !getSearchBy().isEmpty();
    }

    public static boolean getSetupComplete() {
        return person.isSetupComplete();
    }

    public static ArrayList<String> getPublicPhotos(Context context) {
        return person.getPublicPhotos(context);
    }

    public static int getCantPublicPhotos() {
        return person.getCantPublicPhotos();
    }

    public static ArrayList<String> getPendingUpdates(Context context) {
        String json = Tools.getValue(profile, Tools.getString(R.string.pending_updates, context));
        ArrayList<String> array = new ArrayList<>();
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(json);
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

    public static ArrayList<PendingInMessage> getPendingInMessages(Context context) {
        String json = Tools.getValue(profile, Tools.getString(R.string.pending_in_messages, context));
        ArrayList<PendingInMessage> array = new ArrayList<>();
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < (jsonArray != null ? jsonArray.length() : 0); i++) {
            try {
                array.add(new Gson().fromJson(jsonArray.getString(i), PendingInMessage.class));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return array;
    }

    public static void setData(String key, String value, Context context) {
        if (context == null)
            return;


        if (!key.contentEquals(Tools.getString(R.string.contacts, context)) && !key.contentEquals(Tools.getString(R.string.pending_in_messages, context))) {
            addPendingUpdates(key, value, context);
            person = new Gson().fromJson(Tools.putValue(new Gson().toJson(person), key, value), Person.class);
            profile = Tools.putValue(profile, Tools.getString(R.string.person, context), new Gson().toJson(person));
        } else if (key.contentEquals(Tools.getString(R.string.pending_in_messages, context))) {
            addPendingInMessage(new Gson().fromJson(value, PendingInMessage.class), context);
        } else {
            profile = Tools.putValue(profile, key, value);
            Log.d("kasdadad", "add m" + key + value.contains("Eduardo"));
        }


        saveJsonProfile(context);
    }


    public static void updateProfileData(Context context) {


        profile = Tools.putValue(profile, Tools.getString(R.string.person, context), new Gson().toJson(person));

        saveJsonProfile(context);
    }

    public static String getContacts(Context context) {
        return Tools.getValue(profile, Tools.getString(R.string.contacts, context));
    }

    public static String getPublicKeyAsString() {
        if (publicKey != null)
            return Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT);
        return null;
    }

    private static void saveJsonProfile(Context context) {
        SharedPreferences preferences = null;
        try {
            preferences = Tools.getEncryptedSharedPreferences(context, Tools.getString(R.string.profile, context));
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Tools.getString(R.string.json, context), profile);
        editor.apply();
    }

    public static void saveSettings(String key, String value, Context context) {
        if (context == null)
            return;
        SharedPreferences preferences = null;
        try {
            preferences = Tools.getEncryptedSharedPreferences(context, Tools.getString(R.string.profile, context));
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();

    }

    public static String getJsonProfile(Context context) {
        SharedPreferences preferences = null;
        try {
            preferences = Tools.getEncryptedSharedPreferences(context, Tools.getString(R.string.profile, context));
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        if (preferences != null) {
            return preferences.getString(Tools.getString(R.string.json, context), "{}");
        }

        return "{}";

    }

    private static void addPendingUpdates(String key, String value, Context context) {
        ArrayList<Pair<String, String>> data = new ArrayList<>();
        data.add(new Pair<>(Tools.getString(R.string.key, context), key));
        data.add(new Pair<>(Tools.getString(R.string.value, context), value));
        ArrayList<String> array = getPendingUpdates(context);
        boolean exist = false;
        for (int i = 0; i < array.size(); i++) {
            if (Tools.getValue(array.get(i), Tools.getString(R.string.key, context)).contentEquals(key)) {
                array.set(i, Tools.getJson(data));
                exist = true;
                break;
            }
        }
        if (!exist)
            array.add(Tools.getJson(data));
        Gson gson = new Gson();
        Log.d("Putvalue", "addpen" + gson.toJson(array));
        profile = Tools.putValue(profile, Tools.getString(R.string.pending_updates, context), gson.toJson(array));
        saveJsonProfile(context);

    }

    public static void removePendingUpdates(Context context) {
        profile = Tools.putValue(profile, Tools.getString(R.string.pending_updates, context), "");
        saveJsonProfile(context);

    }

    static public void setCantPhotos(int cantPhotos) {

        person.setCant_photos(cantPhotos);
    }

    private static void addPendingInMessage(PendingInMessage msg, Context context) {
        ArrayList<PendingInMessage> array = getPendingInMessages(context);
        Log.d("cnaskldnalsda", "add peding");
        array.add(msg);
        profile = Tools.putValue(profile, Tools.getString(R.string.pending_in_messages, context), new Gson().toJson(array));
        saveJsonProfile(context);

    }

    public static void removePendingInMessage(String body, Context context) {
        ArrayList<PendingInMessage> array = getPendingInMessages(context);
        int pos = -1;
        for (int i = 0; i < array.size(); i++) {
            PendingInMessage msg = array.get(i);
            if (msg.getBody().contentEquals(body)) {
                pos = i;
                break;
            }
        }
        if (pos != -1)
            array.remove(pos);

        profile = Tools.putValue(profile, Tools.getString(R.string.pending_in_messages, context), new Gson().toJson(array));
        saveJsonProfile(context);
    }


}
