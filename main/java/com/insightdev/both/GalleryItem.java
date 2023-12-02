package com.insightdev.both;

import android.net.Uri;

public class GalleryItem {

    private Uri image;
    private boolean isChecked = false;

    public GalleryItem(Uri image) {
        this.image = image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public Uri getImage() {
        return image;
    }

    public boolean isChecked() {
        return isChecked;
    }
}
