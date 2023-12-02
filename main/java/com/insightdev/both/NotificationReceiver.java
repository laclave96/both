package com.insightdev.both;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.Person;
import androidx.core.app.RemoteInput;
import androidx.core.graphics.drawable.IconCompat;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int id = intent.getIntExtra("id",-1);
        Contact contact =ContactsManager.getContact(id);
        if (contact == null)
            return;
        String user = contact.getChatUsername(context);
        String action = intent.getStringExtra("action");

        if (action.contentEquals("response")){
            String response = Objects.requireNonNull(getTextMessage(intent)).toString();
            new Thread(){
                @Override
                public void run(){
                    Person.Builder builder = new Person.Builder();
                    try {
                        builder.setIcon(IconCompat.createWithBitmap(Picasso.get().
                                load(Profile.getProfilePhotoLow(context)).transform(new RoundedCornersTransformation(60, 0))
                                .centerCrop().resize(180,180).get()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    builder.setName("Tú");
                    ChatNotifications.Notify(context,id,response,new Date().getTime(),builder.build());
                    Chat chat = Conversations.getChat(user);
                    String msg_id = chat.getMessages().size()+"";
                    String encrypted_response = null, encryptedId = null;
                    String body;
                    try {
                        encrypted_response = Encryption.encryptTextUsingAES(response,chat.getMyAESKey());
                        encryptedId = Encryption.encryptTextUsingAES(id+"",chat.getMyAESKey());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (chat.isPendingSendAesKey()){
                        String plainAesKey = chat.getMyAESKey();
                        String encryptedAesKey = null;
                        try {
                            encryptedAesKey = Encryption.encryptText(plainAesKey,chat.getPublicKey());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        body = Tools.getJsonMsg(encrypted_response,encryptedId,encryptedAesKey);
                    }else
                        body = Tools.getJsonMsg(encrypted_response, encryptedId);

                    String status = "sent";
                    if(!ChatHandler.sendMessage(body,user,msg_id)){
                        status = "pending";
                        Conversations.addPendingToSendMessage(new PendingMessage(body,user,msg_id));
                    }
                    Msg msg = new Msg(response,msg_id,status,"out", new Date().getTime(), false);
                    Conversations.addMsgToChat(user,msg,context);
                }
            }.start();

        }else {
            ChatNotifications.notificationManager.cancel(intent.getIntExtra("id", 0));
            ChatNotifications.removeNotificationsFromList(id);
        }
        ChatHandler.sendReadState(user);
        Conversations.setPendingReading(user,false);


    }
    private CharSequence getTextMessage(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(ChatNotifications.KEY_TEXT_REPLY);
        }
        return null;
    }
}
