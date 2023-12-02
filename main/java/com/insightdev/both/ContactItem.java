package com.insightdev.both;

public class ContactItem {
    private String name, id;
    private String photo;


    public ContactItem(String name, String id, String photo) {
        this.name = name;
        this.photo = photo;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
