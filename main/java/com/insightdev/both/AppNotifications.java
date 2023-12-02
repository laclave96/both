package com.insightdev.both;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;


public class AppNotifications extends Application {
    static NotificationManager notificationManager;
    static int cantLikes = 0;
    static int cantMatches = 0;

    public static void Notify(Context context, int id, String type, Bitmap bitmap, String title, String body) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = null;
        PendingIntent pendingIntent;

        if ((type.contentEquals("match") || type.contentEquals("like")) && !AppHandler.likesChannel)
            return;
        if ((!type.contentEquals("match") && !type.contentEquals("like")) && !AppHandler.appChannel)
            return;

        if (type.contentEquals("match"))
            cantMatches++;
        if (type.contentEquals("like"))
            cantLikes++;

        if (cantLikes > 3) {
            id = -5;
            title = "Tienes " + cantLikes + " nuevos likes";
            body = "¿Qué esperas para hacer matchs?";
            bitmap = ((BitmapDrawable) context.getDrawable(R.drawable.mini_heart)).getBitmap();
        }
        if (cantMatches > 3) {
            id = -6;
            title = "Tienes " + cantMatches + " nuevos matchs";
            body = "¿Qué esperas para decir hola?";
            bitmap = ((BitmapDrawable) context.getDrawable(R.drawable.mini_heart)).getBitmap();
        }

        if (type.contentEquals("push")) {
            String url = Tools.getValue(body, "url");
            body = Tools.getValue(body, "text");
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        } else if (Profile.person != null) {
            intent = new Intent(context, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("notification", true);
            bundle.putString(Tools.getString(R.string.type, context), type);
            bundle.putString(Tools.getString(R.string.id, context), id + "");
            intent.putExtras(bundle);
        } else {
            intent = new Intent(context, Opening.class);
        }
        pendingIntent = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = AppHandler.APP_CHANNEL;
            if (type.contentEquals("match") || type.contentEquals("like"))
                channelId = AppHandler.ACTIVITY_CHANNEL;
            builder = new NotificationCompat.Builder(context, channelId);

        } else {
            builder = new NotificationCompat.Builder(context);

        }
        builder.setSmallIcon(R.drawable.both_mini2);
        builder.setLargeIcon(bitmap);
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setColorized(true);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        if (id == -1010) {
            builder.setSilent(true);
            // builder.setSound(null);
            Log.d("aslkdja", "WHAT?");
        }
        Notification notification = builder.build();
        notificationManager.notify(id, notification);

    }

    public static void newNotify(Context context, Bitmap bitmap, String title, String body) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent;
        PendingIntent pendingIntent;

        intent = new Intent(context, Opening.class);
        pendingIntent = PendingIntent.getActivity(context, 1212, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE  );

        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder = new NotificationCompat.Builder(context, AppHandler.ACTIVITY_CHANNEL);

        } else {
            builder = new NotificationCompat.Builder(context);

        }

        builder.setSmallIcon(R.drawable.both_mini2);
        builder.setLargeIcon(bitmap);
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setColorized(true);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        Notification notification = builder.build();


        notificationManager.notify(1212, notification);


    }




}
