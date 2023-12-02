package com.insightdev.both;

public class ChangePersonEvent {
    private final int position;

    public ChangePersonEvent(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
