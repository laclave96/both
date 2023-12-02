package com.insightdev.both;

public class UpdateContactStatusEvent {
    private String status;

    public UpdateContactStatusEvent(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
