package com.insightdev.both;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

public class Person {

    private final String id, name, username, email, phone, server, birth, gender, search_by, orientation,
            preferences, occupation, about_me, survey, social_networks, latitude, longitude, last_update;
    private String public_key, city, country, country_code, premium;
    private int is_photo_upload, is_setup_complete, cant_photos;
    private int show_orientation, region;

    public static native String pathPhoto();

    static {
        System.loadLibrary("native-lib");
    }

    public Person(String id, String name, String email, String username, String phone, String server, String city, String country, String country_code, int region, String birth, String gender, String search_by, String orientation, String preferences, String occupation, String about_me, String survey, String social_networks, String premium, String latitude, String longitude, String public_key, String last_update, int show_orientation, int is_photo_upload, int is_setup_complete, int cant_photos) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.server = server;
        this.city = city;
        this.country = country;
        this.country_code = country_code;
        this.region = region;
        this.birth = birth;
        this.gender = gender;
        this.search_by = search_by;
        this.orientation = orientation;
        this.preferences = preferences;
        this.occupation = occupation;
        this.about_me = about_me;
        this.survey = survey;
        this.social_networks = social_networks;
        this.premium = premium;
        this.latitude = latitude;
        this.longitude = longitude;
        this.public_key = public_key;
        this.last_update = last_update;
        this.show_orientation = show_orientation;
        this.is_photo_upload = is_photo_upload;
        this.is_setup_complete = is_setup_complete;
        this.cant_photos = Math.min(cant_photos, 5);
    }

    public int getCant_photos() {
        return cant_photos;
    }

    public void setCant_photos(int cant_photos) {
        this.cant_photos = cant_photos;
    }

    public String getCountry() {
        return country;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPremium(String premium) {
        this.premium = premium;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public int getRegion() {
        return region;
    }

    public String getId() {
        return id;
    }

    public String getUsername(Context context) {
        return username == null ? "" : username;
    }

    public String getChatUsername(Context context) {
        return getId() + "@" + getServer() + "." + Tools.getString(R.string.domain, context);
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email == null ? "" : email;
    }

    public String getPhone() {
        return phone;
    }

    public String getServer() {
        return server;
    }

    public String getCity() {
        return city == null ? "" : city;
    }

    public String getBirth() {
        return (birth == null || birth.length() != 8) ? "" : birth;
    }

    public int getAge() {
        try {
            return Tools.getAge(getYear(), getMonth(), getDay());
        } catch (Exception ignored) {
        }
        return 0;
    }

    public int getYear() {
        try {
            //  return Integer.parseInt(Tools.getValue(birth, "year"));
            return Integer.parseInt(birth.substring(0, 4));
        } catch (Exception ignored) {
        }
        return -1;
    }

    public int getMonth() {
        try {
            // return Integer.parseInt(Tools.getValue(birth, "month"));
            return Integer.parseInt(birth.substring(4, 6));
        } catch (Exception ignored) {
        }
        return -1;
    }

    public int getDay() {
        try {
            // return Integer.parseInt(Tools.getValue(birth, "day"));
            return Integer.parseInt(birth.substring(6, 8));
        } catch (Exception ignored) {
        }
        return -1;
    }

    public String getGender() {
        return gender == null ? "" : gender;
    }

    public String getSearchBy() {
        return search_by == null ? "" : search_by;
    }

    public String getOrientation() {
        return orientation == null ? "" : orientation;
    }

    public boolean isShowOrientation() {
        show_orientation = show_orientation == -1 ? 1 : show_orientation;
        return show_orientation == 1;
    }

    public String getPreferences() {
        return preferences == null ? "" : preferences;
    }

    public String getOccupation() {
        return occupation == null ? "" : occupation;
    }

    public String getAboutMe() {
        return about_me == null ? "" : about_me;
    }

    public String getSurvey() {
        return survey == null ? "" : survey;
    }

    public String getLookingFor() {
        return Tools.getValue(getSurvey(), "question1");
    }

    public String getHighlight() {

        return Tools.getValue(getSurvey(), "question2");
    }

    public String getDislike() {

        return Tools.getValue(getSurvey(), "question3");
    }

    public String getTravel() {

        return Tools.getValue(getSurvey(), "question4");
    }

    public String getSocialNetworks() {
        return social_networks == null ? "" : social_networks;
    }

    public String getInstagram() {
        return Tools.getValue(getSocialNetworks(), "instagram");
    }

    public String getPremium() {
        return premium == null ? "" : premium;
    }

    public boolean isPremium(Context context) {
        Log.d("Premium_", "pemium" + premium);
        Log.d("Premium_", "getpemium" + getPremium());
        if (!getPremium().isEmpty())
            return Tools.getValue(getPremium(), Tools.getString(R.string.premium, context)).contentEquals("1");
        return false;
    }

    public boolean isFreePremiumConsumed(Context context) {
        if (!getPremium().isEmpty())
            return Tools.getValue(getPremium(), Tools.getString(R.string.test_period, context)).contentEquals("1");
        return false;
    }

    public String getLatitude() {
        return latitude == null ? "" : latitude;
    }

    public String getLongitude() {
        return longitude == null ? "" : longitude;
    }

    public String getProfilePhotoMedium(Context context) {
        Log.d("Log_", "serverrGetPhoto" + server);
        return Tools.getString(R.string.http, context) + Tools.getAddress(server, context) + "/"
                + pathPhoto() + "/" + getId() + "/" + Tools.getString(R.string.profile, context) + "_"
                + Tools.getString(R.string.medium, context) + Tools.getString(R.string.jpeg, context);
    }


    public String getProfilePhotoLow(Context context) {
        return Tools.getString(R.string.http, context) + Tools.getAddress(server, context) + "/"
                + pathPhoto() + "/" + getId() + "/" + Tools.getString(R.string.profile, context) + "_"
                + Tools.getString(R.string.low, context) + Tools.getString(R.string.jpeg, context);
    }

    public int getCantPublicPhotos() {
        return cant_photos == -1 ? 0 : cant_photos;
    }

    public boolean isPhotoUpload() {
        return is_photo_upload == 1;
    }

    public boolean isSetupComplete() {
        return is_setup_complete == 1;
    }

    public ArrayList<String> getPublicPhotos(Context context) {
        ArrayList<String> array = new ArrayList<>();
        for (int i = 0; i < getCantPublicPhotos(); i++) {
            String url = Tools.getString(R.string.http, context) + Tools.getAddress(server, context) + "/"
                    + pathPhoto() + "/" + getId() + "/" + Tools.getString(R.string.public_photo, context) + (i + 1)
                    + Tools.getString(R.string.jpeg, context);

            array.add(url);
        }
        return array;
    }

    public Date getLastUpdate() {
        return Tools.getDateFromTIMESTAMP(last_update);

    }


    public String getPublicKeyAsString() {
        return public_key;
    }

    public void setPublicKey(String public_key) {
        this.public_key = public_key;
    }


}
