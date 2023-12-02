package com.insightdev.both;

public class NewChatEvent {
    private String with;


    public NewChatEvent(String with) {
        this.with = with;
    }

    public String getWith() {
        return with;
    }

    public void setWith(String with) {
        this.with = with;
    }
}
