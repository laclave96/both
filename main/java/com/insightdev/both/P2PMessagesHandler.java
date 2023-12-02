package com.insightdev.both;

import android.content.Context;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.Collection;

public abstract class P2PMessagesHandler {

    private static final ChatManager chatManager = null;
    private static final IncomingChatMessageListener incomingListener = null;

    public P2PMessagesHandler() {

    }

    public void removeIncomingListener() {
        chatManager.removeListener(incomingListener);
    }

    public void chatListener() {
        XMPPMessageServer.getConnection().addSyncStanzaListener(new StanzaListener() {
            @Override
            public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException, SmackException.NotLoggedInException {
                Message message = (Message) packet;
                String body = message.getBody();
                String from = StringUtils.substringBetween("$" + message.getFrom().toString(), "$", "/");
                String id = message.getStanzaId();
                DelayInformation inf = null;
                inf = message.getExtension(DelayInformation.ELEMENT, DelayInformation.NAMESPACE);
                if (body != null)
                    incomingMessage(from, body, id, inf);

            }

        }, MessageTypeFilter.NORMAL_OR_CHAT);

    }


    public void loadReceived() {
        DeliveryReceiptManager receiptManager = DeliveryReceiptManager.getInstanceFor(XMPPMessageServer.getConnection());
        receiptManager.setAutoReceiptMode(DeliveryReceiptManager.AutoReceiptMode.always);
        receiptManager.autoAddDeliveryReceiptRequests();
        receiptManager.setAutoReceiptMode(DeliveryReceiptManager.AutoReceiptMode.always);
        receiptManager.addReceiptReceivedListener(new ReceiptReceivedListener() {
            @Override
            public void onReceiptReceived(Jid fromJid, Jid toJid, String receiptId, Stanza receipt) {
                Log.d("ReceivedFrom: ", "YESSSS");
                String from = StringUtils.substringBetween("$" + fromJid.toString(), "$", "/");
                receiptReceived(receiptId, from);


            }
        });

    }

    protected abstract void incomingMessage(String from, String body, String id, DelayInformation inf);

    protected abstract void receiptReceived(String receiptId, String from);

    public static boolean sendMessage(String message, String entityBareId, String id) {

        EntityBareJid jid = null;
        try {
            jid = JidCreate.entityBareFrom(entityBareId);
        } catch (XmppStringprepException e) {

            e.printStackTrace();
            return false;
        }

        Log.d("send_Message: ", "for_send");
        if (XMPPMessageServer.getConnection() != null) {
            Message newMessage = new Message();
            newMessage.setTo(jid);
            newMessage.setBody(message);
            newMessage.setStanzaId(id);
            Log.d("send_Message: ", "conectado");
            try {
                XMPPMessageServer.getConnection().sendStanza(newMessage);
                DeliveryReceiptRequest.addTo(newMessage);
                Log.d("send_Message: ", "enviado");
                Log.d("send_Message: ", entityBareId + "//" + message);

                return true;

            } catch (SmackException.NotConnectedException | InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;

    }

    public void rosterListener(Context context) {
        Roster roster = Roster.getInstanceFor(XMPPMessageServer.getConnection());
        roster.addRosterListener(new RosterListener() {
            @Override
            public void entriesAdded(Collection<Jid> addresses) {

            }

            @Override
            public void entriesUpdated(Collection<Jid> addresses) {

            }

            @Override
            public void entriesDeleted(Collection<Jid> addresses) {

            }

            @Override
            public void presenceChanged(Presence presence) {
                Log.d("PresenceChanged_", "a" + presence);
                if (XMPPMessageServer.getConnection() != null)
                    AppHandler.updateStatusByPresence(presence, context);
            }
        });
    }

    public void addRosterEntry(String username, String name) {
        if (XMPPMessageServer.getConnection() != null) {
            Roster roster = Roster.getInstanceFor(XMPPMessageServer.getConnection());
            BareJid jid = null;
            try {
                jid = JidCreate.bareFrom(username);
            } catch (XmppStringprepException e) {
                e.printStackTrace();
            }
            try {
                roster.createEntry(jid, name, null);
            } catch (SmackException.NotLoggedInException e) {
                e.printStackTrace();
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeRosterEntry(String username) {
        Roster roster = Roster.getInstanceFor(XMPPMessageServer.getConnection());
        BareJid jid = null;
        try {
            jid = JidCreate.bareFrom(username);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        RosterEntry rosterEntry = roster.getEntry(jid);
        try {
            roster.removeEntry(rosterEntry);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
