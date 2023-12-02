package com.insightdev.both;

public class DeleteChatEvent {
    private final String username;

    public DeleteChatEvent(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

}
