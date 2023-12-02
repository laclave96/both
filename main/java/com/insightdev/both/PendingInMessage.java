package com.insightdev.both;

import org.jivesoftware.smackx.delay.packet.DelayInformation;

public class PendingInMessage {
    private final String from;
    private final String body;
    private final String stanzaId;
    private final DelayInformation inf;


    public PendingInMessage(String from, String body, String stanzaId, DelayInformation inf) {
        this.from = from;
        this.body = body;
        this.stanzaId = stanzaId;
        this.inf = inf;
    }

    public String getFrom() {
        return from;
    }

    public String getBody() {
        return body;
    }

    public String getStanzaId() {
        return stanzaId;
    }

    public DelayInformation getInf() {
        return inf;
    }
}
