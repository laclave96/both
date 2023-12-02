package com.insightdev.both;

public class ShowPost {
    private final String primaryUrl;
    private final String secondaryUrl;
    private final String title;
    private final String description;

    public ShowPost(String primaryUrl, String secondaryUrl, String title, String description) {
        this.primaryUrl = primaryUrl;
        this.secondaryUrl = secondaryUrl;
        this.title = title;
        this.description = description;
    }


    public String getPrimaryUrl() {
        return primaryUrl;
    }

    public String getSecondaryUrl() {
        return secondaryUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
