package com.insightdev.both;

import androidx.core.app.NotificationCompat;

public class NotifyController {
    private final NotificationCompat.MessagingStyle style;
    private final int id;
    private final int[] intentIds;

    public NotifyController(NotificationCompat.MessagingStyle style, int id, int[] intentIds) {
        this.style = style;
        this.id = id;
        this.intentIds = intentIds;
    }

    public NotificationCompat.MessagingStyle getStyle() {
        return style;
    }

    public int getId() {
        return id;
    }

    public int[] getIntentIds() {
        return intentIds;
    }
}
