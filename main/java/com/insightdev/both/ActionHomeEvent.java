package com.insightdev.both;

public class ActionHomeEvent {
    private final String actionResponse;

    public ActionHomeEvent(String actionResponse) {
        this.actionResponse = actionResponse;
    }

    public String getActionResponse() {
        return actionResponse;
    }
}
