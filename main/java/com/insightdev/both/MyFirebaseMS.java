package com.insightdev.both;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.insightdev.both.viewmodels.HomeViewModel;
import com.squareup.picasso.Picasso;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smackx.ping.PingManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;


public class MyFirebaseMS extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);


        Handler mainHandler = new Handler(Looper.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {


                if (getApplicationContext() != null) {
                    Tools.saveSettings("is_token_upload", "nop", getApplicationContext());
                    String id = Tools.getSettings("ProfileId", getApplicationContext());
                    if (id != null)
                        AppHandler.uploadToken(getApplicationContext(), s, id);
                }

                //   Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();


            } // This is your code
        };
        mainHandler.post(myRunnable);


    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("alskda", "reciveFcm" + remoteMessage.getData());


        Map<String, String> data = remoteMessage.getData();

        Handler mainHandler = new Handler(Looper.getMainLooper());

        String title, body;

        if (!data.containsKey("type"))
            return;

        if (Objects.equals(data.get("type"), "0")) {

            int cantNewLikes = Integer.parseInt(data.get("cantLikes"));

            if (cantNewLikes < 2) {
                title = getString(R.string.fcm_title, data.get("name"));
                body = getString(R.string.fcm_body);
            } else {
                title = getString(R.string.fcm_title2, data.get("name"));
                body = getString(R.string.fcm_body2, String.valueOf(cantNewLikes - 1));
            }

            Bitmap bitmap = null;
            try {
                bitmap = Picasso.get().
                        load(data.get("image")).transform(new RoundedCornersTransformation(60, 0))
                        .centerCrop().resize(180, 180).get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Bitmap finalBitmap = bitmap;
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {

                    if (getApplicationContext() != null) {

                        AppNotifications.newNotify(getApplicationContext(), finalBitmap, title, body);
                        if (Tools.isMyServiceRunning(MainService.class, getApplicationContext())) {
                            String id = Tools.getSettings("ProfileId", getApplicationContext());
                            if (!id.isEmpty())
                                AppHandler.loadContactsFromExternalDatabase(id, getApplicationContext());
                        }
                    }
                }
            };
            mainHandler.post(myRunnable);
        } else if (Objects.equals(data.get("type"), "1")) {


            AppNotifications.Notify(getApplicationContext(), -1010, "chat", ((BitmapDrawable) getApplicationContext().getDrawable(R.drawable.both_round_150)).getBitmap(), "Both", "Comprobando si hay nuevos mensajes");


            boolean flagPing = false;
            boolean flagService = Tools.isMyServiceRunning(MainService.class, getApplicationContext());
            PingManager pingManager;
            if (XMPPMessageServer.getConnection() != null) {
                pingManager = PingManager.getInstanceFor(XMPPMessageServer.getConnection());
                try {
                    flagPing = pingManager.pingMyServer();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            AppHandler.runService(getApplicationContext());

            //if (flagService && !flagPing)


            //  AppHandler.reconnectIfNoPing(getApplicationContext());

         /*   Log.d("alskda", data.get("name"));


            androidx.core.app.Person.Builder builder = new androidx.core.app.Person.Builder();
            try {
                builder.setIcon(IconCompat.createWithBitmap(Picasso.get().
                        load(data.get("image"))
                        .transform(new RoundedCornersTransformation(60, 0)).centerCrop().resize(180, 180).get()));
            } catch (IOException e) {
                //Toast.makeText(MainActivity.this, "Error ", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            builder.setName(data.get("name"));
            Person person = builder.build();

            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {

                    if (getApplicationContext() != null) {

                        ChatNotifications.Notify(getApplicationContext(), Integer.valueOf(data.get("id")), data.get("msg"), Long.valueOf(data.get("time")), person);
                    }
                }
            };
            mainHandler.post(myRunnable);*/

        } else if (Objects.equals(data.get("type"), "2")) {


            title = getString(R.string.fcm_expDate_title);
            body = getString(R.string.fcm_expDate_body, data.get("name"));

            Log.d("asjdalkd", data + "");


            Bitmap bitmap = null;
            try {
                bitmap = Picasso.get().
                        load(data.get("image")).transform(new RoundedCornersTransformation(60, 0))
                        .centerCrop().resize(180, 180).get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Bitmap finalBitmap = bitmap;
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {

                    if (getApplicationContext() != null) {

                        AppNotifications.newNotify(getApplicationContext(), finalBitmap, title, body);

                        Tools.saveSettings("is_new_exp_date", data.get("id"), getApplicationContext());

                        getContactData(getApplicationContext(), data.get("id"));


                        //String idNotSeen = Tools.getSettings("is_new_exp_date", getApplicationContext());


                    }
                }
            };
            mainHandler.post(myRunnable);

        } else if (Objects.equals(data.get("type"), "3")) {
            AppNotifications.Notify(getApplicationContext(), -3033, "IceB", ((BitmapDrawable) getApplicationContext().getDrawable(R.drawable.iceb_noty)).getBitmap(), "Tienes un nuevo admirador", data.get("name") + " te ha enviado un cumplido");


        } else if (Objects.equals(data.get("type"), "4")) {

            String lowRes, highRes, link;

            lowRes = data.get("lowResImg");

            highRes = data.get("highResImg");

            link = data.get("link");

            BothAdModel bothAdModel = new BothAdModel(lowRes, highRes, link);

            String bothAdJson = new Gson().toJson(bothAdModel);

            if (getApplicationContext() != null)
                Tools.saveSettings("new_both_ad", bothAdJson, getApplicationContext());


        } else if (Objects.equals(data.get("type"), "5")) {

            if (getApplicationContext() != null)
                Tools.saveSettings("renew_premium", "ren", getApplicationContext());

        }


    }

    public void getContactData(Context context, String id) {

        if (id.isEmpty())
            return;


        CRequest request = new CRequest(context, AppHandler.getContactWithId(), 15);
        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>("auth", AppHandler.pass()));
        pairs.add(new Pair<>("id", id));


        request.makeRequest(pairs);
        request.setOnResponseListener(new CRequest.OnResponseListener() {
            @Override
            public void onResponse(String response) {


                response = Tools.putValue(response, "status", "");
                response = Tools.putValue(response, "type", "expDate");

                Contact contact = new Gson().fromJson(response, Contact.class);

                if (!ContactsManager.checkAddContact(contact, getApplicationContext()))

                    ContactsManager.addType(contact.getChatUsername(getApplicationContext()), "expDate", getApplicationContext());


                AppHandler.loadPendingInMessages(getApplicationContext());

                Intent intent = new Intent(getApplicationContext(), ExpDateActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                intent.putExtra("expDate", id);

                startActivity(intent);


            }

            @Override
            public void onError(String error) {

                Log.d("añlskdañd", error);

            }
        });

    }


}
