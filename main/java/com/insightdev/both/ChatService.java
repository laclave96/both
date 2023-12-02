package com.insightdev.both;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class ChatService extends Service {


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);


        Log.d("alskda", "startingChatService");
        AppHandler.setStartingChatService(true);
        AppHandler.loadConfig(getApplicationContext());
        Profile.Load(getApplicationContext());
        if (Profile.person != null) {
            Profile.loadOrCreateRSAKeys(getApplicationContext());

            Tools.initConfig(getApplicationContext());

            Conversations.loadDatabaseDrivers(getApplicationContext());
            AppHandler.loadInternalContacts(getApplicationContext());
            AppHandler.loadMessagesFromInternalDatabase();
            if (Profile.isPremium(getApplicationContext()))
                AppHandler.checkPremiumIsOver(getApplicationContext());

            if (!Tools.isMyServiceRunning(MainService.class, getApplicationContext()) && !AppHandler.isStartingWorker())
                startService(new Intent(getApplicationContext(), MainService.class));
        }
        AppHandler.setStartingChatService(false);
        return START_STICKY;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
