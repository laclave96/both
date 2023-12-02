package com.insightdev.both;

import android.app.Application;
import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class XMPPMessageServer extends Application {
    private static AbstractXMPPConnection connection = null;
    private static String ADDRESS = "";
    private static String SERVICE = "";
    private static String TYPE_USER = "";
    private static String USERNAME = "";
    private static String PASS = "";
    private static ChatHandler chatHandler;
    private static Roster roster;

    static PingManager pingManager;


    public static void Connect(Context context) {
        XMPPMessageServer.chatHandler = new ChatHandler(context);

        AppHandler.setClosingConnection(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                InetAddress address = null;
                try {
                    address = InetAddress.getByName(getADDRESS());
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                HostnameVerifier verifier = new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return false;
                    }
                };
                DomainBareJid serviceName = null;
                try {
                    serviceName = JidCreate.domainBareFrom(getSERVICE());
                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                }
                XMPPTCPConnectionConfiguration config = null;

                try {
                    config = XMPPTCPConnectionConfiguration.builder()
                            .setUsernameAndPassword(getUSERNAME(), getPASS())
                            .setPort(5222)
                            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                            .setXmppDomain(serviceName)
                            .setHostnameVerifier(verifier)
                            .setHostAddress(address)
                            //.setDebuggerEnabled(true)
                            .setSendPresence(false)
                            //.setCompressionEnabled(true)
                            .setResource("last")
                            .build();
                } catch (XmppStringprepException e) {
                    Log.d("Ñañara", "configfailed");
                    e.printStackTrace();
                }
                XMPPTCPConnection mConnection = new XMPPTCPConnection(config);
                mConnection.setPacketReplyTimeout(10000);
                try {
                    mConnection.connect();

                    mConnection.addConnectionListener(new ConnectionListener() {
                        @Override
                        public void connected(XMPPConnection connection) {
                            Log.d("Ñañara", "connListener");
                        }

                        @Override
                        public void authenticated(XMPPConnection connection, boolean resumed) {
                            Log.d("Ñañara", "AuthListener");
                        }

                        @Override
                        public void connectionClosed() {

                        }

                        @Override
                        public void connectionClosedOnError(Exception e) {

                        }

                        @Override
                        public void reconnectionSuccessful() {
                            Log.d("Ñañara", "connLis");
                        }

                        @Override
                        public void reconnectingIn(int seconds) {
                            Log.d("Ñañara", "connLis");
                        }

                        @Override
                        public void reconnectionFailed(Exception e) {

                        }
                    });
                    //       while (!mConnection.isConnected()) {
                    //     }
                    Log.d("Ñañara", "config" + config.getUsername() + "//" + config.getPassword() + "//" + config.getXMPPServiceDomain());
                    mConnection.login();
                    //    while (!mConnection.isAuthenticated()) {
                    //  }
                    Log.d("Ñañara", "auth");

                    try {
                        AppHandler.loadPendingInMessages(context);
                    } catch (Exception ignored) {

                    }
                    setConnection(mConnection);
                    if (XMPPMessageServer.getConnection() != null)
                        setupConnection(context);
                } catch (SmackException e) {
                    Log.d("Ñañara", e.getMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.d("ÑañaraIO", e.getMessage());
                    e.printStackTrace();
                } catch (XMPPException e) {
                    Log.d("ÑañaraXmpp", e.getMessage());
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    Log.d("ÑañaraIE", e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private static synchronized void setupConnection(Context context) {
        AppHandler.setStartingWorker(false);
        connectionListener(context);
        chatHandler.chatListener();
        chatHandler.loadReceived();

        ArrayList<Contact> contacts = ContactsManager.getContacts();

        synchronized (contacts) {

            for (int i = 0; i < contacts.size(); i++) {
                if (contacts.get(i) == null || contacts.get(i).getType() == null)
                    continue;
                if (contacts.get(i).getType().contentEquals(Tools.getString(R.string.match, context))) {
                    chatHandler.addRosterEntry(contacts.get(i).getChatUsername(context), contacts.get(i).getName());
                }
            }

        }

        if (XMPPMessageServer.getConnection() != null) {
            chatHandler.rosterListener(context);
            sendPresenceAndMessages(context);
        }

    }

    private static void sendPresenceAndMessages(Context context) {
        chatHandler.sendPendingMessages();
        String status = "offline/" + new Date().getTime() + "/";

        if (AppHandler.isMainActivityOpen() || AppHandler.isChatActivityOpen() || AppHandler.isOpeningActivityOpen())
            status = "en línea";

        roster = Roster.getInstanceFor(connection);
        roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
        AppHandler.sendPresence(status);

        String with = Conversations.getInsideChat().second;
        if (!with.isEmpty()) {
            BareJid jid = null;
            try {
                jid = JidCreate.bareFrom(with);
            } catch (XmppStringprepException e) {
                e.printStackTrace();
            }
            Log.d("presence:", "InsideChatwith" + with + "//" + jid);
            AppHandler.updateStatusByPresence(roster.getPresence(jid), context);
        }
        AppHandler.loadPendingInMessages(context);
    }

    public static void Disconnect() {
        Log.d("Connection", "disconnecting");
        AppHandler.setClosingConnection(true);
        connection.disconnect();
        connection = null;
    }

    public static AbstractXMPPConnection getConnection() {
        return connection;
    }

    public static void setConnection(XMPPTCPConnection connection) {
        XMPPMessageServer.connection = connection;
    }

    public static String getADDRESS() {
        return ADDRESS;
    }

    public static void setADDRESS(String ADDRESS) {
        XMPPMessageServer.ADDRESS = ADDRESS;
    }

    public static String getSERVICE() {
        return SERVICE;
    }

    public static void setSERVICE(String SERVICE) {
        XMPPMessageServer.SERVICE = SERVICE;
    }

    public static String getTypeUser() {
        return TYPE_USER;
    }

    public static void setTypeUser(String typeUser) {
        TYPE_USER = typeUser;
    }

    public static String getUSERNAME() {
        return USERNAME;
    }

    public static void setUSERNAME(String USERNAME) {
        XMPPMessageServer.USERNAME = USERNAME;
    }

    public static String getPASS() {
        return PASS;
    }

    public static void setPASS(String PASS) {
        XMPPMessageServer.PASS = PASS;
    }

    public static ChatHandler getChatHandler() {
        return chatHandler;
    }

    public static void setChatHandler(ChatHandler chatHandler) {
        XMPPMessageServer.chatHandler = chatHandler;
    }

    public static Roster getRoster() {
        return roster;
    }

    private static void connectionListener(Context context) {
        XMPPMessageServer.getConnection().addConnectionListener(new ConnectionListener() {
            @Override
            public void connected(XMPPConnection connection) {
                Log.d("Connection", "ConnectedLis");
                Log.d("Ñañara", "conn");
            }

            @Override
            public void authenticated(XMPPConnection connection, boolean resumed) {
                Log.d("Ñañara", "auth lis");
            }

            @Override
            public void connectionClosed() {
                if (!AppHandler.isClosingConnection()) {
                    Log.d("Ñañara", "Reconnecting in 5");
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (!AppHandler.isNewWorkerConnecting())
                                reconnect(context);
                            else
                                AppHandler.setNewWorkerConnecting(false);
                        }
                    }, 5000);
                }

            }

            @Override
            public void connectionClosedOnError(Exception e) {
                Log.d("Ñañara", "closeError " + e.getMessage());
                if (e.getMessage().contains("conflict"))
                    return;
                Log.d("Connection", "Reconnecting in 5");
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!AppHandler.isNewWorkerConnecting())
                            reconnect(context);
                        else
                            AppHandler.setNewWorkerConnecting(false);
                    }
                }, 5000);
            }

            @Override
            public void reconnectionSuccessful() {
            }

            @Override
            public void reconnectingIn(int seconds) {
            }

            @Override
            public void reconnectionFailed(Exception e) {
                Log.d("Ñañara", "error " + e.getMessage());
                ;
            }
        });

    }

    public static void reconnect(Context context) {
        Log.d("ñañasere", "tryRecomect ");
        EventBus.getDefault().post(new UpdateContactStatusEvent(""));
        Log.d("Connection", "isClosed");
        if (Tools.isConnected(context) && !getUSERNAME().isEmpty()) {
            if (connection != null)
                Disconnect();
            Log.d("Connection", "Reconnecting");
            Connect(context);
        }
    }

}
