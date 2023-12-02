package com.insightdev.both;

public class InMms {

    private final String data, path, time;

    public InMms(String data, String path, String time) {
        this.data = data;
        this.path = path;
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public String getData() {
        return data;
    }

    public String getTime() {
        return time;
    }
}
