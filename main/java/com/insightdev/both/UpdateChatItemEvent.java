package com.insightdev.both;

public class UpdateChatItemEvent {
    private int position;

    public UpdateChatItemEvent(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
