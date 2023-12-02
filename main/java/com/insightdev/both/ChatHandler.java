package com.insightdev.both;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.jivesoftware.smackx.delay.packet.DelayInformation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ChatHandler extends P2PMessagesHandler {
    Context context;

    public static native String getPublicKey();

    public static native String pass();

    static {
        System.loadLibrary("native-lib");
    }

    public ChatHandler(Context context) {
        this.context = context;

    }


    @Override
    protected void incomingMessage(String from, String body, String stanzaID, DelayInformation inf) {
        processMessage(from, body, stanzaID, inf);
    }

    @Override
    protected void receiptReceived(String receiptId, String from) {
        changeToReceived(receiptId, from);

    }


    public void changeToReceived(String id, String from) {
        Log.d("ReceivedFrom: ", from);
        Log.d("ReceivedFId: ", id);
        Conversations.changeMessageStatus(from, id, "received", "out");

    }


    public void processMessage(String from, String body, String stanzaID, DelayInformation inf) {
        boolean isMms = false;
        boolean isReply = false;
        Chat chatWith = Conversations.getChat(from);
        Log.d("LogX_", body + "//" + from);
        if (body.contains("read")) {
            if (chatWith != null) {
                Log.d("newIncomingMessage: ", "Read");
                String id = StringUtils.substringBetween(body, "read", "@");
                Conversations.changeMessageStatus(from, id, "read", "out");
                Conversations.setPendingToSendAesKey(from, false);
            }
        } else if (body.contains("resend")) {
            if (chatWith != null) {
                String id = StringUtils.substringBetween(body, "resend", "@");
                resendMessage(from, id, context);
            }
        } else if (body.contains("listen")) {
            if (chatWith != null) {
                String id = StringUtils.substringBetween(body, "listen", "@");
                Conversations.setToListenAudio(from, id, true);
            }
        } else {

            if (body.contains("contact")) {

                Contact contact = ContactsManager.getContact(Integer.parseInt(from.substring(0, from.indexOf("@"))));
                try {
                    Log.d("newIncomingMessage", "contact " + contact.getName());
                } catch (Exception e) {
                    Log.d("newIncomingMessage", "error " + e.getMessage());
                }

                Contact contactToAdd = new Gson().fromJson(Tools.getValue(body, "contact"), Contact.class);

                if (contact == null)
                    ContactsManager.addContact(contactToAdd);
                else
                    ContactsManager.addType(contact.getChatUsername(context), contactToAdd.getType(), context);


                AppHandler.executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        Profile.setData(Tools.getString(R.string.contacts, context), ContactsManager.getSerializedContacts(), context);
                    }
                });
            }

            Contact contact = ContactsManager.getContact(from, context);

            if (contact == null) {
/*
                String pendigContact = Tools.getSettings("exp_date_pending_contact", context);

                if(pendigContact.)

                Gson gson = new Gson();

                PendingInMessage msg = new PendingInMessage(from, body, stanzaID, inf);
                Profile.setData(Tools.getString(R.string.pending_in_messages, context), gson.toJson(msg), context);
                //AppHandler.loadContactsFromExternalDatabase(Profile.getId(), context);*/
                return;
            }

            String type = contact.getType();

            Log.d("asnñdlaksd", type);

            if (type.contentEquals(Tools.getString(R.string.like, context)) || type.contentEquals(Tools.getString(R.string.mylike, context))) {
                Gson gson = new Gson();
                PendingInMessage msg = new PendingInMessage(from, body, stanzaID, inf);
                Profile.setData(Tools.getString(R.string.pending_in_messages, context), gson.toJson(msg), context);
                AppHandler.loadContactsFromExternalDatabase(Profile.getId(), context);
                return;
            }

            Log.d("calsjdk", "containswee");
            String yourPlainAesKey = null;
            String plainText = null;
            String encryptedText = Tools.getValue(body, "message");
            String encryptedId = Tools.getValue(body, "x");

            Chat chat = Conversations.getChat(from);
            if (body.contains("key")) {
                Log.d("newIncomingMessage: ", "WITHKEYYYYY");

                String yourEncryptedAesKey = Tools.getValue(body, "key");
                Log.d("newIncomingMessage: ", " keyyy " + yourEncryptedAesKey);
                Conversations.setPendingToSendAesKey(from, false);
                try {
                    yourPlainAesKey = Encryption.decryptText(yourEncryptedAesKey, Profile.privateKey);
                    Log.d("newIncomingMessage: ", "my private key " + Profile.privateKey);
                    Log.d("asñlkasñdasd", "public enemi " + Base64.encodeToString(contact.getPublicKey().getEncoded(), Base64.DEFAULT));
                    Log.d("newIncomingMessage: ", "update key " + yourPlainAesKey);
                    Conversations.updateYourAesKey(from, yourPlainAesKey);
                } catch (Exception e) {
                    e.printStackTrace();
                    requestResendMessage(stanzaID, from);
                    Log.d("newIncomingMessage: ", " keyExc " + e.getMessage());
                    return;
                }
            } else {
                if (chat != null)
                    yourPlainAesKey = Conversations.getChat(from).getYourAESKey();
            }
            Log.d("newIncomingMessage: ", "plainAesKey" + yourPlainAesKey);
            try {
                String plainId = Encryption.decryptTextUsingAES(encryptedId, yourPlainAesKey);
                Log.d("newIncomingMessage: ", "plainID" + plainId);

                if (!plainId.contentEquals(Profile.getId())) {
                    Log.d("newIncomingMessage: ", "resend");
                    requestResendMessage(stanzaID, from);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("newIncomingMessage: ", e.getMessage());
                requestResendMessage(stanzaID, from);
                return;
            }
            Log.d("LogXnewIncoming ", "encrypteText" + encryptedText);
            Log.d("LogXnewIncoming ", "plainAesKey" + yourPlainAesKey);
            try {
                plainText = Encryption.decryptTextUsingAES(encryptedText, yourPlainAesKey);
                Log.d("newIncomingMessage: ", "plainTEXT" + plainText);
            } catch (Exception e) {
                Log.d("newIncomingMessage: ", "error" + e.getMessage());
                e.printStackTrace();
            }
            String bodyWithOutReply = plainText;

            if (!Tools.getValue(plainText, "reply").isEmpty()) {
                isReply = true;
                bodyWithOutReply = Tools.getValue(plainText, "body");
            }

            if (!Tools.getValue(bodyWithOutReply, "audio").isEmpty())
                isMms = true;

            Date date = new Date();
            if (inf != null)
                date = inf.getStamp();
            Log.d("newIncomingMessage: ", "Process");
            if (chat == null) {
                String myAESKey = null;
                try {
                    myAESKey = Encryption.getSecretAESKeyAsString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ArrayList<Msg> messages = new ArrayList<>();
                if (isMms)
                    messages.add(new AudioMessage(plainText, stanzaID, "received", "in", date.getTime(), isReply));
                else
                    messages.add(new Msg(plainText, stanzaID, "received", "in", date.getTime(), isReply));
                Log.d("newIncomingMessage: ", "addMessage");
                Log.d("newIncomingMessage: ", "contacts" + ContactsManager.getContacts().size());
                Log.d("newIncomingMessage: ", "contact" + contact);

                Log.d("newIncomingMessage: ", "createChatObject");
                Conversations.addConversation(new Chat(contact.getName(), contact.getChatUsername(context), messages, contact.getPublicKey(), myAESKey, yourPlainAesKey, true, true, false), context);
                Log.d("newIncomingMessage: ", "NEWCHAT");
            } else {
                Conversations.setPendingReading(from, true);
                if (isMms)
                    Conversations.addMsgToChat(from, new AudioMessage(plainText, stanzaID, "received", "in", date.getTime(), isReply), context);
                else
                    Conversations.addMsgToChat(from, new Msg(plainText, stanzaID, "received", "in", date.getTime(), isReply), context);
            }


        }

    }

    public void sendPendingMessages() {
        ArrayList<PendingMessage> pending = Conversations.getPendingToSend();
        ArrayList<PendingMessage> sent = new ArrayList<>();
        for (PendingMessage msg : pending) {
            String body = msg.getBody();
            String to = msg.getTo();
            String id = msg.getId();
            if (sendMessage(body, to, id)) {
                if (!body.contentEquals("read"))
                    Conversations.changeMessageStatus(to, id, "sent", "out");
                sent.add(msg);
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        if (!Tools.isUserOnline(to))
                            AppHandler.sendPushRequest(context, to.substring(0, to.indexOf("@")));
                    }
                }).start();
            }
        }
        for (PendingMessage msg : sent) {
            pending.remove(msg);
            Conversations.removePendingToSendMessage(msg);
        }

    }


    public static void sendCommand(String command, String user) {
        String id = Tools.generateRandom(0, 1000) + "";
        if (!sendMessage(command, user, id)) {
            Conversations.addPendingToSendMessage(new PendingMessage(command, user, id));
        }

    }

    public static void sendReadState(String username) {
        ArrayList<String> ids = Conversations.changeReceivedToRead(username);
        for (String id : ids) {
            sendCommand("read" + id + "@", username);

        }

    }

    public static void requestResendMessage(String id, String username) {
        sendCommand("resend" + id + "@", username);

    }

    public static void resendMessage(String username, String id, Context context) {
        CRequest requestPublicKey = new CRequest(context, getPublicKey(),
                15);
        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>("id", ContactsManager.getContact(username, context).getId() + ""));
        pairs.add(new Pair<>("auth", pass()));
        requestPublicKey.makeRequest(pairs);
        Log.d("Resend_: ", "makeRequest");
        requestPublicKey.setOnResponseListener(new CRequest.OnResponseListener() {
            @Override
            public void onResponse(String response) {
                response = response.trim();
                Conversations.updateYourPublicKey(username, response, context);
                Chat chat = Conversations.getChat(username);
                Log.d("Resend_: ", "response" + response);
                if (chat != null) {
                    Contact contact = ContactsManager.getContact(username, context);
                    List<Msg> messages = chat.getMessages();
                    String message = "";
                    for (Msg msg : messages) {
                        if (msg.getType().contentEquals("out") && msg.getId().contentEquals(id))
                            message = msg.getBody();
                    }
                    Log.d("Resend_: ", "resendMessagePlain" + message);
                    if (message.isEmpty())
                        return;
                    String myAESKey, fullMessage, encryptedMessage = null, encryptedId = null;
                    String encryptedAESKey = null;
                    myAESKey = chat.getMyAESKey();
                    Log.d("Resend_: ", "isPending_publicKey" + response);
                    Log.d("Resend_: ", "aeskey" + myAESKey);
                    try {
                        encryptedAESKey = Encryption.encryptText(myAESKey, contact.getPublicKey());
                        encryptedMessage = Encryption.encryptTextUsingAES(message, myAESKey);
                        encryptedId = Encryption.encryptTextUsingAES(contact.getId() + "", myAESKey);
                    } catch (Exception e) {
                        Log.d("Resend_: ", "error" + e.getMessage());
                        e.printStackTrace();

                    }
                    fullMessage = Tools.getJsonMsg(encryptedMessage, encryptedId, encryptedAESKey);

                    Log.d("Send_", "meessage" + fullMessage);

                    if (!sendMessage(fullMessage, username, id)) {
                        Conversations.addPendingToSendMessage(new PendingMessage(fullMessage, username, id));
                    }
                }
            }

            @Override
            public void onError(String error) {
                Log.d("Resend_: ", "error" + error);
            }
        });

    }


}
