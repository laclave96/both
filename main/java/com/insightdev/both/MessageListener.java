package com.insightdev.both;

public interface MessageListener {
    /**
     * To call this method when new message received and send back
     * @param message Message
     */
    void messageReceived(String beneficiario, String monto);
}
