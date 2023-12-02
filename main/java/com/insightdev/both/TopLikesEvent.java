package com.insightdev.both;

public class TopLikesEvent {
    private final String action;

    public TopLikesEvent(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }
}
