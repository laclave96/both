package com.insightdev.both;

public class Ask {

    String question, answ1, answ2, answ3;

    int image;

    public Ask(String question, String answ1, String answ2, String answ3, int image) {
        this.question = question;
        this.answ1 = answ1;
        this.answ2 = answ2;
        this.answ3 = answ3;
        this.image = image;
    }

    public int getImage() {
        return image;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnsw1() {
        return answ1;
    }

    public String getAnsw2() {
        return answ2;
    }

    public String getAnsw3() {
        return answ3;
    }
}
