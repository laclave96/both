package com.insightdev.both;

import android.net.Uri;

public class PremiumLikeItem {

    Uri image;
    String  name, likes ;

    public PremiumLikeItem(Uri image, String name, String likes) {
        this.image = image;
        this.name = name;
        this.likes = likes;
    }

    public Uri getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getLikes() {
        return likes;
    }
}
