package com.insightdev.both;

import android.net.Uri;

public class BothAdModel {

    String lowResUri, highResUri;
    boolean hasWebLink;
    String link;

    public BothAdModel(String lowResUri, String highResUri, String link) {
        this.lowResUri = lowResUri;
        this.highResUri = highResUri;
        this.link = link;
        hasWebLink = !link.isEmpty();
    }

    public String getLowResUri() {
        return lowResUri;
    }

    public String getHighResUri() {
        return highResUri;
    }

    public boolean isHasWebLink() {
        return hasWebLink;
    }

    public String getLink() {
        return link;
    }
}
