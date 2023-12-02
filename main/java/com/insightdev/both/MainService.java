package com.insightdev.both;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.ping.PingManager;

public class MainService extends Service {


    public MainService(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!");
    }

    public MainService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.d("alskda", "startingMain");
        AppHandler.setStartingWorker(true);


        Profile.Load(getApplicationContext());
        Profile.loadOrCreateRSAKeys(getApplicationContext());
        Tools.initConfig(getApplicationContext());
        Log.d("Connection", "LoadingDrivers");
        Conversations.loadDatabaseDrivers(getApplicationContext());
        ContactsManager.loadContactDBDriver(getApplicationContext());
        Log.d("Connection", "LoadingContactsD");
        AppHandler.loadInternalContacts(getApplicationContext());
        Log.d("Connection", "LoadingMessages");
        try {
            AppHandler.loadMessagesFromInternalDatabase();
        } catch (Exception e) {
            for (Chat chat : Conversations.getConversations()) {
                Conversations.deleteChat(chat.getUsername());
            }
        }
        AppHandler.appChannel = !Tools.getSettings("app_channel", getApplicationContext()).contentEquals("0");
        AppHandler.likesChannel = !Tools.getSettings("likes_channel", getApplicationContext()).contentEquals("0");
        AppHandler.chatChannel = !Tools.getSettings("chat_channel", getApplicationContext()).contentEquals("0");
        if (Profile.isPremium(getApplicationContext())) {
            AppHandler.checkPremiumIsOver(getApplicationContext());

            
        } else {
            if (Tools.isAllFreePremium(getApplicationContext()))
                AppHandler.changeToPremium(getApplicationContext(), 1, false);
        }
        Log.d("Connection", "StartingWorker");
        AppHandler.loadContactsFromExternalDatabase(Profile.getId(), getApplicationContext());
        XMPPMessageServer.setUSERNAME(Profile.getId());
        XMPPMessageServer.setPASS(Profile.getPass().substring(0, 15));
        XMPPMessageServer.setADDRESS(Tools.getAddress(Profile.getServer(), getApplicationContext()));
        XMPPMessageServer.setSERVICE(Profile.getServer() + "." + Tools.getString(R.string.domain, getApplicationContext()));

        if (XMPPMessageServer.getConnection() == null) {
            Log.d("Connection", "ConnectedFromWorker");
            XMPPMessageServer.Connect(getApplicationContext());
        }


        /*

        AppHandler.setStartingWorker(false);

        if (!AppHandler.isOpeningActivityOpen() && !AppHandler.isMainActivityOpen() && !AppHandler.isChatActivityOpen()) {
            if (XMPPMessageServer.getConnection() != null) {
                Log.d("Connection", "NotActivity Disconnect");
                XMPPMessageServer.Disconnect();
            }
        }
        Log.d("Connection", "LongSleep");

     */

        /*
        WorkManager.getInstance(getApplicationContext()).enqueueUniqueWork(
                "handler_connection",
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                AppHandler.getXMPPConnectionWork());



        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                if (XMPPMessageServer.getConnection() != null) {
                    PingManager pingManager=PingManager.getInstanceFor(XMPPMessageServer.getConnection());
                    try {
                        Log.d("aklsd", pingManager.pingMyServer()+"");
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                handler.postDelayed(this, 1000);
            }
        });*/


        return START_STICKY;
    }


    @Override
    public boolean stopService(Intent name) {

        // XMPPMessageServer.Disconnect();
        return super.stopService(name);

    }

    @Override
    public void onDestroy() {
        Log.d("aklsd", "serviceStopdd");
        super.onDestroy();
        if (XMPPMessageServer.getConnection() != null)
            XMPPMessageServer.Disconnect();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
