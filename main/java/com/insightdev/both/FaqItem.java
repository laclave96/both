package com.insightdev.both;

public class FaqItem {

    String quest, answ;
    int image;


    public FaqItem(String quest, String answ, int image) {
        this.quest = quest;
        this.answ = answ;
        this.image = image;
    }

    public String getQuest() {
        return quest;
    }

    public String getAnsw() {
        return answ;
    }

    public int getImage() {
        return image;
    }
}
