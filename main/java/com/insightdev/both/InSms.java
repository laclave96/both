package com.insightdev.both;

public class InSms {

    private final String body, time;

    public InSms(String body, String time) {
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
