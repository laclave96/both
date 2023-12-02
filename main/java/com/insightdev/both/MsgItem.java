package com.insightdev.both;

public class MsgItem {
    private String status;
    private String type;
    private String time;
    private boolean dayVisibility;
    private String dayTime;
    private boolean isReply;
    private String textReply;
    private String replyId;

    public MsgItem(String status, String type, String time, boolean dayVisibility, String dayTime, boolean isReply, String textReply, String replyId) {
        this.status = status;
        this.type = type;
        this.time = time;
        this.dayVisibility = dayVisibility;
        this.dayTime = dayTime;
        this.isReply = isReply;
        this.textReply = textReply;
        this.replyId = replyId;
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

    public String getTime() {
        return time;
    }

    public boolean isDayVisible() {
        return dayVisibility;
    }

    public String getDayTime() {
        return dayTime;
    }

    public boolean isReply() {
        return isReply;
    }

    public String getTextReply() {
        return textReply;
    }

    public String getReplyId() {
        return replyId;
    }
}
