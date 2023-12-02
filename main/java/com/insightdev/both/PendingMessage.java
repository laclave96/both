package com.insightdev.both;

public class PendingMessage {
    private String body;
    private String to;
    private String id;

    public PendingMessage(String body, String to, String id) {
        this.body = body;
        this.to = to;
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
