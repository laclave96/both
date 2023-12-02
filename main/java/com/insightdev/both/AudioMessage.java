package com.insightdev.both;


public class AudioMessage extends Msg {
    private String audio;
    private int millis;
    private String duration;
    private boolean isListen;
    private int[] randomWave;

    public AudioMessage(String message, String id, String status, String type, long time, boolean isReply) {
        super(message, id, status, type, time, isReply);
        randomWave = Tools.generateRandomArray(20);
        isListen = false;
        millis = getDurationMillis(getBody());
        duration =  Tools.convertMillisToMinutes(millis);
        audio = getStrAudio(getBody());
        isA = "AudioMessage";
    }


    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
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

    public void setRandomWave(int[] randomWave) {
        this.randomWave = randomWave;
    }

    public int getMillis() {
        return millis;
    }

    public void setMillis(int millis) {
        this.millis = millis;
    }

    private static String getStrAudio(String msg) {
        return Tools.getValue(msg, "audio");
    }

    public static int getDurationMillis(String msg) {
        return Integer.parseInt(Tools.getValue(msg, "duration"));
    }

}
