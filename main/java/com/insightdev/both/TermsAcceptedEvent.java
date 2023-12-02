package com.insightdev.both;

public class TermsAcceptedEvent {
    private String sourceSignUp;

    public TermsAcceptedEvent(String sourceSignUp) {
        this.sourceSignUp = sourceSignUp;
    }

    public String sourceSignUp() {
        return sourceSignUp;
    }
}
