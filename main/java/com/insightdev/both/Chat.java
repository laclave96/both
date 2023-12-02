package com.insightdev.both;

import android.util.Base64;

import org.apache.commons.lang3.StringUtils;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class Chat implements Comparable<Chat> {
    public String name;
    private String username;
    private ArrayList<Msg> messages;
    private PublicKey publicKey;
    private String myAESKey;
    private String yourAESKey;
    private boolean pendingReading;
    private boolean pendingSendAesKey;
    private boolean isMuted;

    public Chat(String name, String username, ArrayList<Msg> messages, PublicKey publicKey, String myAESKey, String yourAESKey, boolean pendingReading, boolean pendingSendAesKey, boolean isMuted) {
        this.name = name;
        this.username = username;
        this.messages = messages;
        this.publicKey = publicKey;
        this.myAESKey = myAESKey;
        this.yourAESKey = yourAESKey;
        this.pendingReading = pendingReading;
        this.pendingSendAesKey = pendingSendAesKey;
        this.isMuted = isMuted;

    }

    public int getId() {
        return Integer.parseInt(StringUtils.substringBetween("$" + username, "$", "@"));
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<Msg> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Msg> messages) {
        this.messages = messages;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }


    public String getPublicKeyAsString() {
        if (publicKey == null)
            return "";
        return Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT);
    }

    public String getMyAESKey() {
        return myAESKey;
    }

    public void setMyAESKey(String myAESKey) {
        this.myAESKey = myAESKey;
    }

    public String getYourAESKey() {
        return yourAESKey;
    }

    public void setYourAESKey(String yourAESKey) {
        this.yourAESKey = yourAESKey;
    }

    public boolean isPendingReading() {
        return pendingReading;
    }

    public void setPendingReading(boolean pendingReading) {
        this.pendingReading = pendingReading;
    }

    public boolean isPendingSendAesKey() {
        return pendingSendAesKey;
    }

    public void setPendingSendAesKey(boolean pendingSendAesKey) {
        this.pendingSendAesKey = pendingSendAesKey;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    public int getPendingReading() {
        int cont = 0;
        for (Msg msg : messages) {
            if (msg.getType().contentEquals("in") && msg.getStatus().contentEquals("received"))
                cont++;
        }
        return cont;
    }

    @Override
    public int compareTo(Chat o) {
        long time = this.getMessages().get(this.getMessages().size() - 1).getTime();
        long xtime = o.getMessages().get(o.getMessages().size() - 1).getTime();
        return (int)(time - xtime);
    }
}
