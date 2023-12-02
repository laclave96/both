package com.insightdev.both;

public class LocationEvent {

    private LocationModel model;

    public LocationEvent(LocationModel model) {
        this.model = model;
    }

    public LocationModel getModel() {
        return model;
    }
}
