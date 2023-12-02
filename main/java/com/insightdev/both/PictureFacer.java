package com.insightdev.both;

import android.net.Uri;

import java.io.File;

public class PictureFacer {

    private String picturName;
    private String picturePath;
    private String pictureSize;
    private Uri imageUri;
    private Boolean selected = false;

    public PictureFacer() {

    }

    public PictureFacer(String picturName, String picturePath, String pictureSize) {
        this.picturName = picturName;
        this.picturePath = picturePath;
        this.pictureSize = pictureSize;
        this.imageUri = Uri.fromFile(new File(picturePath));
    }


    public String getPicturName() {
        return picturName;
    }

    public void setPicturName(String picturName) {
        this.picturName = picturName;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
        imageUri = Uri.fromFile(new File(picturePath));
    }

    public String getPictureSize() {
        return pictureSize;
    }

    public void setPictureSize(String pictureSize) {
        this.pictureSize = pictureSize;
    }


    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}