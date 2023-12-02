package com.insightdev.both;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;

import java.util.ArrayList;


public class ChatNotifications extends Application {
    public static NotificationManager notificationManager = null;
    private static ArrayList<NotifyController> activeNotifications = new ArrayList<>();

    public static final String KEY_TEXT_REPLY = "key_text_reply";

    public static void loadNotificationManager(Context context) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @SuppressLint("WrongConstant")
    public static void Notify(Context context, int id, String text, long time, Person user) {
        loadNotificationManager(context);
        Intent intent;
        PendingIntent pendingIntent;

        Log.d("maslñdaksñd", AppHandler.chatChannel + " channel");
        if (!AppHandler.chatChannel)
            return;
        Log.d("maslñdaksñd", " notify1");
        int[] intentsIds = getUniqueIntentsIds();
        if (Exist(id))
            intentsIds = getIntentsIds(id);

        intent = new Intent(context, ChatRoom.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("notification", true);
        bundle.putString("username", ContactsManager.getContact(id).getChatUsername(context));
        intent.putExtras(bundle);


        Log.d("maslñdaksñd", " notify22");
        pendingIntent = PendingIntent.getActivity(context, intentsIds[0], intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        Log.d("maslñdaksñd", " intent111");
       /* if(AppHandler.isMainActivityOpen()){
            pendingIntent = PendingIntent.getActivity(context,intentsIds[0],intent,PendingIntent.FLAG_UPDATE_CURRENT);
        }else{
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntentWithParentStack(intent);
            pendingIntent  = stackBuilder.getPendingIntent(intentsIds[1], PendingIntent.FLAG_UPDATE_CURRENT);
        }*/
        Log.d("maslñdaksñd", " intent000");
        Intent broadcast_read = new Intent(context, NotificationReceiver.class);
        broadcast_read.putExtra("id", id);
        broadcast_read.putExtra("action", "read");
        @SuppressLint("WrongConstant") PendingIntent read = PendingIntent.getBroadcast(context,
                intentsIds[2], broadcast_read, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);


        Log.d("maslñdaksñd", " intent0");
        String replyLabel = "Responder";
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel(replyLabel)
                .build();

        Intent broadcast_reply = new Intent(context, NotificationReceiver.class);
        broadcast_reply.putExtra("id", id);
        broadcast_reply.putExtra("action", "response");
        @SuppressLint("WrongConstant") PendingIntent replyPendingIntent = PendingIntent.getBroadcast(context,
                intentsIds[3], broadcast_reply, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        Log.d("maslñdaksñd", " intent1");
        NotificationCompat.Action actionReply =
                new NotificationCompat.Action.Builder(0,
                        "RESPONDER", replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .setAllowGeneratedReplies(true)
                        .build();

        NotificationCompat.MessagingStyle.Message message =
                new NotificationCompat.MessagingStyle.Message(text, time, user);

        NotificationCompat.Builder builder;

        Log.d("maslñdaksñd", " build0");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(context, AppHandler.APP_CHANNEL);

        } else {
            builder = new NotificationCompat.Builder(context);

        }
        builder.setSmallIcon(R.drawable.both_mini2);

        Log.d("maslñdaksñd", " notify");

        NotificationCompat.MessagingStyle messagingStyle = getMessaging(id);
        messagingStyle.addMessage(message);
        builder.setStyle(messagingStyle);
        builder.setColorized(true);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        Log.d("maslñdaksñd", " notifyprevBuild");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            builder.addAction(actionReply);
        else
            builder.addAction(0, "RESPONDER", pendingIntent);

        builder.addAction(0, "MARCAR COMO LEIDO", read);

        Log.d("maslñdaksñd", " notifypostBuild");
        if (user.getName().toString().contentEquals("Tú"))
            builder.setSilent(true);
        Log.d("maslñdaksñd", " notifyll");

        try {
            Notification notification = builder.build();
            notificationManager.notify(id, notification);

            if (!Exist(id))
                activeNotifications.add(new NotifyController(messagingStyle, id, getUniqueIntentsIds()));

        }catch (Exception e){
            Log.d("maslñdaksñd", " error "+e.getMessage());
        }

        Log.d("maslñdaksñd", " notifyPP");
        try {
            notificationManager.cancel(-1010);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("maslñdaksñd", " notifyError "+e.getMessage());
        }


    }

    private static boolean Exist(int id) {
        for (NotifyController controller : activeNotifications) {
            if (controller.getId() == id)
                return true;
        }
        return false;
    }

    private static int[] getUniqueIntentsIds() {
        int[] ids = new int[4];
        int cont = 0;
        ArrayList<Integer> activeIds = new ArrayList<>();
        for (NotifyController controller : activeNotifications) {
            for (int i = 0; i < 4; i++) {
                activeIds.add(controller.getIntentIds()[i]);
            }
        }
        while (cont < 4) {
            int rand = Tools.generateRandom(0, 1000);
            if (!activeIds.contains(rand))
                ids[cont++] = rand;
        }
        return ids;
    }

    private static NotificationCompat.MessagingStyle getMessaging(int id) {
        for (NotifyController controller : activeNotifications) {
            if (controller.getId() == id)
                return controller.getStyle();
        }
        return new NotificationCompat.MessagingStyle("Messages");
    }

    private static int[] getIntentsIds(int id) {
        for (NotifyController controller : activeNotifications) {
            if (controller.getId() == id)
                return controller.getIntentIds();
        }
        return new int[4];
    }

    public static void removeNotificationsFromList(int id) {
        for (int i = 0; i < activeNotifications.size(); i++) {
            if (activeNotifications.get(i).getId() == id) {
                activeNotifications.remove(i);
                break;
            }

        }
    }

}
