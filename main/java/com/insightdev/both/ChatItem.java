package com.insightdev.both;

public class ChatItem {
    private String name, username;
    private String photo;
    private String time;
    private String lastMessage;
    private String msgStatus;
    private String status;
    private String lastType, personType;
    private boolean isPending;
    private boolean isMuted;
    private int cantPending;

    public ChatItem(String name, String username, String photo, String time, String lastMessage, String msgStatus, String status, String lastType, String personType, boolean isPending, boolean isMuted, int cantPending) {
        this.name = name;
        this.username = username;
        this.photo = photo;
        this.time = time;
        this.lastMessage = lastMessage;
        this.msgStatus = msgStatus;
        this.status = status;
        this.lastType = lastType;
        this.personType = personType;
        this.isPending = isPending;
        this.isMuted = isMuted;
        this.cantPending = cantPending;
    }

    public String getPersonType() {
        return personType;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPhoto() {
        return photo;
    }

    public String getTime() {
        return time;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getMsgStatus() {
        return msgStatus;
    }

    public String getStatus() {
        return status;
    }

    public String getLastType() {
        return lastType;
    }

    public boolean isPending() {
        return isPending;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public int getCantPending() {
        return cantPending;
    }
}
