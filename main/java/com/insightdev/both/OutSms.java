package com.insightdev.both;

public class OutSms {
    private final String body, time;

    public OutSms(String body, String time) {
        this.body = body;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public String getBody() {
        return body;
    }
}
