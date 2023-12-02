package com.insightdev.both;

public class PriceItem {

    private final double price;
    private final int duration;
    private final int savings;

    public PriceItem(double price, int duration, int savings) {
        this.price = price;
        this.duration = duration;
        this.savings = savings;
    }

    public double getPrice() {
        return price;
    }

    public int getDuration() {
        return duration;
    }

    public int getSavings() {
        return savings;
    }
}
