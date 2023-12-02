package com.insightdev.both;

import java.util.ArrayList;

public class PersonsStoredEvent {
    private ArrayList<Object> profileItems = new ArrayList<>();
    private ArrayList<Object> showProfileItems = new ArrayList<>();
    private int pos = 0;

    public PersonsStoredEvent(ArrayList<Object> profileItems, ArrayList<Object> showProfileItems, int pos) {
        this.profileItems = profileItems;
        this.showProfileItems = showProfileItems;
        this.pos = pos;
    }

    public ArrayList<Object> getProfileItems() {
        return profileItems;
    }

    public ArrayList<Object> getShowProfileItems() {
        return showProfileItems;
    }

    public int getPos() {
        return pos;
    }
}
