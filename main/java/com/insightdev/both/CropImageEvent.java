package com.insightdev.both;

public class CropImageEvent {
    String path;

    public CropImageEvent(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
