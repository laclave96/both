package com.insightdev.both;

import android.util.Log;

import java.text.SimpleDateFormat;

public class Msg {
    private String id;
    private String message;
    private String status;
    private String type;
    private long time;
    private String hours;
    private boolean isReply;
    private String textReply;
    private String replyId;
    public String isA;

    public Msg(String message, String id, String status, String type, long time, boolean isReply) {
        this.message = message;
        this.id = id;
        this.status = status;
        this.type = type;
        this.isReply = isReply;
        if (this.isReply) {
            String reply = Tools.getValue(message, "reply");
            replyId = Tools.getValue(reply, "id");
            textReply = Tools.getValue(reply, "text");
            Log.d("LogX", "ReplyID" + replyId);
            this.message = Tools.getValue(message, "body");
        }
        this.time = time;
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        this.hours = sdf.format(time);
        this.isA = "Msg";

    }

    public String getBody() {
        return message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public long getTime() {
        return time;
    }


    public boolean isReply() {
        return isReply;
    }

    public void setReply(boolean reply) {
        isReply = reply;
    }

    public String getHours() {
        return hours;
    }

    public String getTextReply() {
        return textReply;
    }

    public String getReplyId() {
        return replyId;
    }


}
