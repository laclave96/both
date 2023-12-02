package com.insightdev.both;

public class ApplyFiltersEvent {

    private int from;

    public ApplyFiltersEvent(int from) {
        this.from = from;
    }

    public int getFrom() {
        return from;
    }
}
