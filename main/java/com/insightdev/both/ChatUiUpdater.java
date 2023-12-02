package com.insightdev.both;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.util.Pair;

import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public class ChatUiUpdater extends Application {

    public static void newChat(Chat chat, Context context) {

        Contact chatContact = ContactsManager.getContact(chat.getUsername(), context);

        if (chatContact == null)
            return;

        String username = chat.getUsername();
        EventBus.getDefault().post(new NewChatEvent(username));
        Pair<Boolean, String> inside = Conversations.getInsideChat();
        if (inside.second.contentEquals(username)) {
            return;
        }

        Log.d("adñlaksñldada", chatContact.getType().trim());
        if (chatContact.getType().trim().contains("iceb"))
            return;
        if (!chat.isMuted()) {
            int id = chat.getId();
            Msg last_message = chat.getMessages().get(chat.getMessages().size() - 1);
            String text = last_message.getBody();

            if (last_message instanceof AudioMessage) {
                text = "Mensaje de voz (" + ((AudioMessage) last_message).getDuration() + ")";
            }

            long time = last_message.getTime();
            String name = Tools.getFirstWords(chat.getName(), 1);
            String finalText = text;
            new Thread() {
                @Override
                public void run() {
                    Person.Builder builder = new Person.Builder();
                    try {
                        builder.setIcon(IconCompat.createWithBitmap(Picasso.get()
                                .load(ContactsManager.getContact(id).getProfilePhotoLow(context)).transform(new RoundedCornersTransformation(60, 0)).centerCrop().resize(180, 180).get()));
                    } catch (IOException e) {
                        //Toast.makeText(MainActivity.this, "Error ", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    builder.setName(name);
                    Person person = builder.build();
                    AppHandler.executor.submit(new Runnable() {
                        @Override
                        public void run() {
                            ChatNotifications.Notify(context, id, finalText, time, person);
                        }
                    });


                }
            }.start();
        }
    }

    public static void addMessage(String with, boolean isOut, Context context) {

        Contact chatContact = ContactsManager.getContact(with, context);

        if (chatContact == null)
            return;

        Pair<Boolean, String> inside_chat = Conversations.getInsideChat();
        EventBus.getDefault().post(new UpdateChatsEvent());
        Log.d("AddMessage", "isOut" + isOut + "//" + inside_chat.second);
        if (isOut) {
            if (inside_chat.second.contentEquals(with)) {
                EventBus.getDefault().post(new SendMessageEvent());
            }
            return;
        }

        if (inside_chat.second.contentEquals(with)) {
            EventBus.getDefault().post(new ReceiveMessageEvent());
            return;
        }


        Chat chat = Conversations.getChat(with);


        if (chatContact.getType().trim().contains("iceb"))
            return;

        if (!chat.isMuted()) {
            Log.d("maslñdaksñd", " notify00");
            int id = chat.getId();
            Msg last_message = chat.getMessages().get(chat.getMessages().size() - 1);
            String text = last_message.getBody();

            if (last_message instanceof AudioMessage) {
                text = "Mensaje de voz (" + ((AudioMessage) last_message).getDuration() + ")";
            }
            long time = last_message.getTime();
            String name = Tools.getFirstWords(chat.getName(), 1);
            String finalText = text;
            new Thread() {
                @Override
                public void run() {
                    Person.Builder builder = new Person.Builder();
                    try {
                        builder.setIcon(IconCompat.createWithBitmap(Picasso.get().
                                load(ContactsManager.getContact(id).getProfilePhotoLow(context))
                                .transform(new RoundedCornersTransformation(60, 0)).centerCrop().resize(180, 180).get()));
                    } catch (IOException e) {
                        //Toast.makeText(MainActivity.this, "Error ", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    builder.setName(name);
                    Person person = builder.build();
                    AppHandler.executor.submit(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("maslñdaksñd", " prev");
                            ChatNotifications.Notify(context, id, finalText, time, person);
                        }
                    });

                }
            }.start();
        }
    }

    public static void changePendingReading(int pos) {
        EventBus.getDefault().post(new UpdateChatItemEvent(pos));

    }

    public static void changeMuted(int pos) {
        EventBus.getDefault().post(new UpdateChatItemEvent(pos));

    }

    public static void changeMessageStatus(String with, int pos, String status) {
        Pair<Boolean, String> inside_chat = Conversations.getInsideChat();
        if (inside_chat.second.contentEquals(with)) {
            EventBus.getDefault().post(new UpdateMessageEvent(pos, status));
        }
        EventBus.getDefault().post(new UpdateChatItemEvent(Conversations.getPosByUser(with)));

    }

    public static void changeContactStatus(String with, String status) {
        Pair<Boolean, String> inside_chat = Conversations.getInsideChat();
        if (inside_chat.second.contentEquals(with)) {
            EventBus.getDefault().post(new UpdateContactStatusEvent(status));
        }
        EventBus.getDefault().post(new UpdateChatItemEvent(Conversations.getPosByUser(with)));
    }

    public static void deleteChat(String username) {
        EventBus.getDefault().post(new ChatListEvent());
        EventBus.getDefault().post(new DeleteChatEvent(username));
    }

    public static void cleanMessages(String username) {
        EventBus.getDefault().post(new ChatListEvent());
        EventBus.getDefault().post(new CleanMessagesEvent(username));
    }

    public static void loadChats() {
        EventBus.getDefault().post(new ChatListEvent());
    }


}
