package com.insightdev.both;

public class OpenMatchActivityEvent {
    private final String username;

    public OpenMatchActivityEvent(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
