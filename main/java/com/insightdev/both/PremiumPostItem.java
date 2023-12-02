package com.insightdev.both;

public class PremiumPostItem {

    String image, userImage, name;


    public PremiumPostItem(String image, String userImage, String name) {
        this.image = image;
        this.userImage = userImage;
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public String getUserImage() {
        return userImage;
    }

    public String getName() {
        return name;
    }
}
