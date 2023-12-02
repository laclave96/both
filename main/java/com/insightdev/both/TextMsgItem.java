package com.insightdev.both;

public class TextMsgItem extends MsgItem{
    private String text;

    public TextMsgItem(String text, String status, String type, String time, boolean dayVisibility, String dayTime, boolean isReply, String textReply, String replyId) {
        super(status, type, time, dayVisibility, dayTime, isReply, textReply, replyId);
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
