package com.insightdev.both;


import com.google.gson.Gson;

import java.security.PublicKey;


public class Contact extends Person {
    private String status;
    private String type;

    public Contact(String id, String name, String username, String email, String phone, String server, String city, String country, String country_code, int region, String birth, String gender, String search_by, String orientation, String preferences, String occupation, String about_me, String survey, String social_networks, String premium, String latitude, String longitude, String public_key, String last_update, int show_orientation, int is_photo_upload, int is_setup_complete, int cant_photos, String type) {
        super(id, name, username, email, phone, server, city, country, country_code, region, birth, gender, search_by, orientation, preferences, occupation, about_me, survey, social_networks, premium, latitude, longitude, public_key, last_update, show_orientation, is_photo_upload, is_setup_complete, cant_photos);
        this.type = type != null ? type : "";
        this.status = "";
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PublicKey getPublicKey() {
        return Tools.getPublicKeyFromString(getPublicKeyAsString());
    }


}
