package com.insightdev.both;

public class CleanMessagesEvent {
    private final String username;

    public CleanMessagesEvent(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
