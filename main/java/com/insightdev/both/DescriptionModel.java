package com.insightdev.both;

public class DescriptionModel {

    int img, back, part;

    String text;

    public DescriptionModel(int img, int back, int part, String text) {
        this.img = img;
        this.back = back;
        this.part = part;
        this.text = text;
    }

    public int getImg() {
        return img;
    }

    public int getPart() {
        return part;
    }

    public int getBack() {
        return back;
    }

    public String getText() {
        return text;
    }
}
