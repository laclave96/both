package com.insightdev.both;

public class UpdatePublicPhotoEvent {
    private boolean isReload;
    public UpdatePublicPhotoEvent(boolean isReload) {
        this.isReload = isReload;
    }

    public boolean isReload() {
        return isReload;
    }
}
