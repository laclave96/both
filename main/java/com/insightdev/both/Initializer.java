package com.insightdev.both;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.os.Build;

import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.UploadServiceConfig;

import java.nio.channels.Channel;

public class Initializer extends Application {


    private static String notificationChannelID = "Upload";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        UploadServiceConfig.initialize(this, notificationChannelID, BuildConfig.DEBUG);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(notificationChannelID, "Upload service", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setSound(null, null);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

}