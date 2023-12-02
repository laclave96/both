package com.insightdev.both;

public class OutMms {
    private final String data, path, time;
    private final int[] wave;
    private final float progress;
    private final int duration;

    public OutMms(String data, String path, String time, int[] wave, float progress, int duration) {
        this.data = data;
        this.path = path;
        this.time = time;
        this.wave = wave;
        this.progress = progress;
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public float getProgress() {
        return progress;
    }

    public int[] getWave() {
        return wave;
    }

    public String getData() {
        return data;
    }

    public String getPath() {
        return path;
    }

    public String getTime() {
        return time;
    }
}
