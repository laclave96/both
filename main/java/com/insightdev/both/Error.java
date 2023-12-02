package com.insightdev.both;

public class Error {
    private final String cause;
    private final String source;

    public Error(String cause, String source) {
        this.cause = cause;
        this.source = source;
    }

    public String getCause() {
        return cause;
    }

    public String getSource() {
        return source;
    }
}
