package com.insightdev.both;

public class UpdateMessageEvent {
    private int position;
    private String status;

    public UpdateMessageEvent(int position, String status) {
        this.position = position;
        this.status = status;
    }

    public int getPosition() {
        return position;
    }

    public String getStatus() {
        return status;
    }
}
