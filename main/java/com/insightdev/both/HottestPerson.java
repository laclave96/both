package com.insightdev.both;

public class HottestPerson extends Person implements Comparable<HottestPerson> {

    private int likes_today;

    public HottestPerson(String id, String name, String email, String username, String phone, String server, String city, String country,String country_code, int region, String birth, String gender, String search_by, String orientation, String preferences, String occupation, String about_me, String survey, String social_networks, String premium, String latitude, String longitude, String public_key, String last_update, int show_orientation, int is_photo_upload, int is_setup_complete, int cant_photos, int likes_today) {
        super(id, name, email, username, phone, server, city, country,country_code, region, birth, gender, search_by, orientation, preferences, occupation, about_me, survey, social_networks, premium, latitude, longitude, public_key, last_update, show_orientation, is_photo_upload, is_setup_complete, cant_photos);
        this.likes_today = likes_today;
    }

    public int getLikes_today() {
        return likes_today;
    }

    @Override
    public int compareTo(HottestPerson o) {
        long likes = this.getLikes_today();
        long olikes = o.getLikes_today();
        return (int) (likes - olikes);
    }
}
