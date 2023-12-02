package com.insightdev.both;

public class Post {
    private final String  person;
    private final String comment;

    public Post(String person, String comment) {
        this.person = person;
        this.comment = comment;
    }

    public String getPerson() {
        return person;
    }

    public String getComment() {
        return comment;
    }


}
