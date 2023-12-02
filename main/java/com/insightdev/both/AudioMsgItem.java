package com.insightdev.both;

public class AudioMsgItem extends MsgItem{
    private String duration;
    private boolean isListen;
    private int[] randomWave;


    public AudioMsgItem(String duration, boolean isListen, int[] randomWave, String status, String type, String time, boolean dayVisibility, String dayTime, boolean isReply, String textReply, String replyId) {
        super(status, type, time, dayVisibility, dayTime, isReply, textReply, replyId);
        this.duration = duration;
        this.isListen = isListen;
        this.randomWave = randomWave;
    }

    public String getDuration() {
        return duration;
    }

    public boolean isListen() {
        return isListen;
    }

    public void setListen(boolean listen) {
        isListen = listen;
    }

    public int[] getRandomWave() {
        return randomWave;
    }
}
