package com.insightdev.both;

public class ExpDateContact {
    int id;
    long timestamp;

    public ExpDateContact(int id, long timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }


    public int getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
