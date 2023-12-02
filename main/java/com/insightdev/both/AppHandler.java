package com.insightdev.both;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.ping.PingManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.security.PublicKey;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AppHandler extends Application {

    private static boolean MainActivityOpen = false;
    private static boolean ChatActivityOpen = false;
    private static boolean OpeningActivityOpen = false;
    private static boolean startingWorker = false;
    private static boolean startingChatService = false;
    private static boolean newWorkerConnecting = false;
    private static boolean closingConnection = false;
    private static boolean openFromChat = false;
    private static boolean updatingActions = false;
    private static BillingClient billingClient;

    private static boolean configLoaded = false;
    static long lastPushNotificationTimestamp = 0;
    static Date dbTime = new Date();

   

    public static String[] adsArray = null;
    public static int limitAds = 0;
    public static int cantAds = 0;
    public static String MESSAGING_CHANNEL = "BOTH_MESSAGE_CHANNEL";
    public static String APP_CHANNEL = "BOTH_APP_CHANNEL";
    public static String ACTIVITY_CHANNEL = "BOTH_ACTIVITY_CHANNEL";
    public static boolean chatChannel = true;
    public static boolean appChannel = true;
    public static boolean likesChannel = true;
    public static ExecutorService executor = Executors.newSingleThreadExecutor();

    public static native String loadPersons();

    public static native String loadContacts();

    public static native String setData();

    public static native String actions();

    public static native String payScript();

    public static native String getPrices();

    public static native String chargeUser();

    public static native String getTopLikes();

    public static native String getConfig();

    public static native String getLastTime();

    public static native String updateAutoBilling();

    public static native String logsErrors();

    public static native String sendReason();

    public static native String getTrueDate();

    public static native String checkPremium();

    public static native String uploadToken();

    public static native String updateIceb();

    public static native String deleteAccount();

    public static native String deleteAllPhotos();

    public static native String pass();

    public static native String findExpDate();

    public static native String sendPushNotification();

    public static native String sendPushNotificationIceB();

    public static native String passPhoto();

    public static native String pathPhoto();

    public static native String deletePost();

    public static native String cancelExpDate();

    public static native String deletePostString();

    public static native String getContactWithId();

    public static native String updateCantViews();

    public static native String updateRegion();

    static {
        System.loadLibrary("native-lib");
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static PeriodicWorkRequest getAppNotificationWork() {
        return new PeriodicWorkRequest.Builder(AppNotificationWorker.class, 4, TimeUnit.HOURS)
                .setBackoffCriteria(
                        BackoffPolicy.LINEAR,
                        PeriodicWorkRequest.MAX_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS)
                .setInitialDelay(20, TimeUnit.SECONDS)
                .addTag("app_notification")
                .setConstraints(new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .setRequiresCharging(false)
                        .setRequiresBatteryNotLow(false)
                        .build())
                .build();
    }

    public static void addWorkerAppNotification(Context context) {
        List<WorkInfo> workInfo = new ArrayList<>();
        try {
            workInfo = WorkManager.getInstance(context).getWorkInfosByTag("app_notification").get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        WorkInfo.State state = WorkInfo.State.ENQUEUED;
        if (!workInfo.isEmpty()) {
            state = workInfo.get(0).getState();
            Log.d("Worker_", "state:" + state);
        }
        if (workInfo.isEmpty() || state.isFinished() || state == WorkInfo.State.BLOCKED) {
            Log.d("Worker_", "addWorker");
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    "app_notification",
                    ExistingPeriodicWorkPolicy.REPLACE,
                    AppHandler.getAppNotificationWork());
        }
    }


    public static void cancelWorker(Context context) {
        WorkManager.getInstance(context).cancelAllWork();
    }

    public static void runService(Context context) {

        if (!Tools.isMyServiceRunning(MainService.class, context) && !AppHandler.isStartingWorker())
            context.startService(new Intent(context, MainService.class));
    }

    public static void reconnectIfNoPing(Context context) {
        if (context == null)
            return;

        if (Tools.isMyServiceRunning(MainService.class, context) && !AppHandler.isStartingWorker()) {
            boolean pingFlag = false;
            PingManager pingManager;
            if (XMPPMessageServer.getConnection() != null) {
                pingManager = PingManager.getInstanceFor(XMPPMessageServer.getConnection());
                try {
                    pingFlag = pingManager.pingMyServer();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (!pingFlag && Tools.isMyServiceRunning(MainService.class, context))
                XMPPMessageServer.reconnect(context);
        }

    }

    public static void loadContactsFromExternalDatabase(String id, Context context) {
        Log.d("ahlskdjalksdnñalkd", "loadContacts");
        CRequest request = new CRequest(context, loadContacts(),
                20);

        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>(context.getResources().getString(R.string.id), id));
        pairs.add(new Pair<>(context.getResources().getString(R.string.auth), pass()));
        request.makeRequest(pairs);
        request.setOnResponseListener(new CRequest.OnResponseListener() {
            @Override
            public void onResponse(String response) {

                response = response.trim();

                JSONObject jsonObject;

                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }


                if (response.contentEquals("blacklist")) {
                    Tools.saveBlacklist(context);
                    return;
                }

                JSONArray json = null;

                try {
                    json = new JSONArray(jsonObject.getString("new_likes"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("ahlskdjalksdnñalkd", "json array null");
                }
                for (int i = 0; i < (json != null ? json.length() : 0); i++) {

                    String str = null;
                    try {
                        str = json.get(i).toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    String strContact = Tools.getValue(str, Tools.getString(R.string.person, context));


                    Contact contact = new Gson().fromJson(strContact, Contact.class);

                    if (contact == null)
                        continue;
                    contact.setStatus("");
                    Contact c = ContactsManager.getContact(contact.getChatUsername(context), context);


                    if (c == null)
                        ContactsManager.addContact(contact);
                    else {
                        Log.d("ashañljñlawd", "update");
                        ContactsManager.updateContact(contact, context);
                    }


                }
                 /*   new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            ContactsManager.verifyingMatches(context, false);
                        }
                    }, 1500);*/


                String dislikes = "";
                try {
                    dislikes = jsonObject.getString("dislikes");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                final Type typeArrayString = new TypeToken<List<String>>() {
                }.getType();
                final List<String> dislikes_array = new Gson().fromJson(dislikes, typeArrayString);
                Log.d("UpdateC_", "dislikesArray" + dislikes_array);


                Log.d("ahlskdjalksdnñalkd", "disloke " + dislikes_array);
                if (dislikes_array != null)
                    for (String dislike : dislikes_array) {
                        Log.d("ahlskdjalksdnñalkd", dislike + "");
                        Contact contact = ContactsManager.getContact(Integer.parseInt(dislike));
                        if (contact != null)
                            dislike(contact.getChatUsername(context), context, false);
                    }


                Log.d("UpdateX", "XXX" + ContactsManager.getContacts().toString());
                AppHandler.executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        Profile.setData(Tools.getString(R.string.contacts, context), ContactsManager.getSerializedContacts(), context);
                    }
                });
                ContactsUiUpdater.changeContacts();
                loadPendingInMessages(context);


            }

            @Override
            public void onError(String error) {

                if (error.contains("TimeoutError"))
                    loadContactsFromExternalDatabase(Profile.getId(), context);
            }
        });


    }

    public static void loadInternalContacts(Context context) {
        ArrayList<Contact> contacts = new ArrayList<>();
        JSONArray json = null;
        try {
            json = new JSONArray(Profile.getContacts(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < (json != null ? json.length() : 0); i++) {
            // Log.d("NNLOG", i + " ");
            try {
                // Log.d("NNLOG", "contactsInternal" + json.get(i).toString().length() + " cosasaa");

                Contact contact = new Gson().fromJson(json.get(i).toString(), Contact.class);
                if (contact == null)
                    continue;
                contact.setStatus("");
                contacts.add(contact);

                // Log.d("NNLOG", "contactsInternal" + json.get(i).toString());
            } catch (JSONException e) {
                //Log.d("NNLOG", i + " " + e.getMessage());
            }

        }

        ContactsManager.setContacts(contacts);
        Log.d("ContactsInternal", "changeContacts");
        ContactsUiUpdater.changeContacts();
    }

    public static void loadMessagesFromInternalDatabase() {
        Conversations.loadConversations();
        Conversations.loadPendingToSend();
    }

    public static void loadPendingInMessages(Context context) {

        ArrayList<PendingInMessage> array = Profile.getPendingInMessages(context);

        ArrayList<Contact> matches = ContactsManager.getMatches(context);

        matches.addAll(ContactsManager.getExpDates(context));

        for (int i = 0; i < array.size(); i++) {
            PendingInMessage msg = array.get(i);
            for (Contact match : matches) {
                if (msg.getFrom().contentEquals(match.getChatUsername(context))) {

                    if (XMPPMessageServer.getChatHandler() != null) {
                        XMPPMessageServer.getChatHandler().processMessage(msg.getFrom(), msg.getBody(), msg.getStanzaId(), msg.getInf());

                        Profile.removePendingInMessage(msg.getBody(), context);
                        break;
                    }
                }
            }
        }
    }

    public static void sendPresence(String status) {
        Presence presence = new Presence(Presence.Type.available);
        presence.setStatus(status);
        presence.setMode(Presence.Mode.available);
        Log.d("SendPresence_", "Status" + status);
        AbstractXMPPConnection connection = XMPPMessageServer.getConnection();
        if (connection != null) {

            if (connection.isConnected() && connection.isAuthenticated()) {

                try {
                    XMPPMessageServer.getConnection().sendStanza(presence);
                    Log.d("SendPresence_", "p" + presence);

                } catch (SmackException.NotConnectedException | InterruptedException e) {
                    Log.d("SendPresence_", e.getMessage());

                    e.printStackTrace();
                }
            }
        }
    }

    public static void updateStatusByPresence(Presence presence, Context context) {

        String status = presence.getStatus();
        String from = presence.getFrom().toString();
        String without_resource = StringUtils.substringBetween("$" + presence.getFrom().toString(), "$", "/");
        if (without_resource != null)
            from = without_resource;
        if (presence.getType() == Presence.Type.available) {
            if (status.contains("offline")) {
                String lastActivity = StringUtils.substringBetween(status, "offline/", "/");
                ContactsManager.updateStatus(from, lastActivity, context);
            } else if (!status.contains("action")) {
                ContactsManager.updateStatus(from, status, context);
            } else if (Tools.getValue(status, "to").contentEquals(Profile.getChatUser(context))) {
                ContactsManager.updateStatus(from, Tools.getValue(status, "action"), context);
            }
        } else {
            if (Tools.isConnected(context)) {
                CRequest request = new CRequest(context, getLastTime(),
                        15);
                ArrayList<Pair<String, String>> pairs = new ArrayList<>();
                pairs.add(new Pair<>(Tools.getString(R.string.id, context), ContactsManager.getContact(from, context).getId()));
                pairs.add(new Pair<>(Tools.getString(R.string.auth, context), pass()));
                request.makeRequest(pairs);
                Log.d("Log_", "makeRequestLastTime");
                String finalFrom = from;
                request.setOnResponseListener(new CRequest.OnResponseListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Log_", "responselastTime" + response);
                        response = response.trim();
                        ContactsManager.updateStatus(finalFrom, Tools.getDateFromDb(response).getTime() + "", context);

                    }

                    @Override
                    public void onError(String error) {
                        ContactsManager.updateStatus(finalFrom, "", context);
                    }
                });

            } else
                ContactsManager.updateStatus(from, "", context);

        }
    }

    public static void updatePendingData(Context context) {
        Log.d("Log_", "updating pending" + Profile.getPendingUpdates(context));
        if (!Profile.getPendingUpdates(context).isEmpty() && Tools.isConnected(context)) {
            Log.d("Log_", "pending" + Profile.getPendingUpdates(context).toString());
            CRequest request = new CRequest(context, setData(),
                    20);

            ArrayList<Pair<String, String>> pairs = new ArrayList<>();
            pairs.add(new Pair<>("data", Profile.getPendingUpdates(context).toString()));
            pairs.add(new Pair<>(Tools.getString(R.string.id, context), Profile.getId()));
            pairs.add(new Pair<>(Tools.getString(R.string.auth, context), pass()));
            request.makeRequest(pairs);
            Log.d("Log_", "makeRequest");
            request.setOnResponseListener(new CRequest.OnResponseListener() {
                @Override
                public void onResponse(String response) {

                    Log.d("Log_", "responsepending" + response);
                    if (response.trim().contentEquals("updated"))
                        Profile.removePendingUpdates(context);

                }

                @Override
                public void onError(String error) {
                    Log.d("Log_", "error " + error);
                }
            });
        }
    }

    public static void updatePendingActions(Context context) {
        Log.d("ashañljñlawd", "bool " + updatingActions);
        if (updatingActions)
            return;


        Log.d("ashañljñlawd", "updating actions" + Actions.getActionsArray(context));
        if (!Actions.getActionsArray(context).isEmpty() && Tools.isConnected(context)) {
            CRequest request = new CRequest(context, actions(),
                    20);

            ArrayList<Pair<String, String>> pairs = new ArrayList<>();
            pairs.add(new Pair<>("actions", Actions.getActionsArray(context).toString()));
            pairs.add(new Pair<>(Tools.getString(R.string.id, context), Profile.getId()));
            pairs.add(new Pair<>("name", Profile.getName()));
            pairs.add(new Pair<>(Tools.getString(R.string.auth, context), pass()));
            request.makeRequest(pairs);
            updatingActions = true;

            Log.d("Log_", "makeRequestActions");
            request.setOnResponseListener(new CRequest.OnResponseListener() {
                @Override
                public void onResponse(String response) {
                    updatingActions = false;
                    Log.d("ashañljñlawd", "responseactions" + response);
                    if (response.trim().isEmpty())
                        Actions.removeActions(context);


                }

                @Override
                public void onError(String error) {
                    updatingActions = false;
                    Log.d("ashañljñlawd", "error" + error);
                }
            });
        }
    }

    public static void loadPersons(Context context, boolean nextPage, int cantViews) {

        Log.d("anskldnañgla", "cant views " + cantViews);
        CRequest request = new CRequest(context, loadPersons(),
                20);
        String min_age = Tools.getSettings("min_age", context);
        String max_age = Tools.getSettings("max_age", context);
        String gender = Tools.getSettings("gender", context);
        String regions = Tools.getSettings("regions", context);
        String country = Profile.getCountry(), timeZone;


        if (gender.isEmpty())
            gender = "2";
        String distance = Tools.getSettings("distance", context);

        if (min_age.isEmpty()) {
            min_age = "18";
            max_age = "40";
        }
        if (distance.isEmpty())
            distance = "1000";

        if (regions.isEmpty())
            regions = "0";


        Log.d("baslkdjaslda", regions);

        ArrayList<Pair<String, String>> pairs = new ArrayList<>();

        ArrayList<Object> profileItems = new ArrayList<>();
        ArrayList<Object> showProfileItems = new ArrayList<>();

        String lat, lon;
        if (MainActivity.latitude != -500) {
            lat = MainActivity.latitude + "";
            lon = MainActivity.longitude + "";
        } else {
            lat = Profile.getLatitude();
            lon = Profile.getLongitude();
        }


        if (lat.isEmpty())
            lat = "37.4218521";

        if (lon.isEmpty())
            lon = "-122.0841308";


        timeZone = TimezoneMapper.latLngToTimezoneString(Double.parseDouble(lat), Double.parseDouble(lon));


        pairs.add(new Pair<>("latitude", lat));
        pairs.add(new Pair<>("longitude", lon));
        pairs.add(new Pair<>("min_age", min_age));
        pairs.add(new Pair<>("max_age", max_age));
        pairs.add(new Pair<>("distance", distance));
        pairs.add(new Pair<>("gender", gender));
        pairs.add(new Pair<>("regions", regions));
        pairs.add(new Pair<>("country", country));
        pairs.add(new Pair<>("timeZone", timeZone));
        pairs.add(new Pair<>("viewsOk", String.valueOf(cantViews)));
        pairs.add(new Pair<>(Tools.getString(R.string.id, context), Profile.getId()));
        pairs.add(new Pair<>(Tools.getString(R.string.auth, context), pass()));
        request.makeRequest(pairs);
        Log.d("LogXX", "LoadPersons" + pairs.toString());
        request.setOnResponseListener(new CRequest.OnResponseListener() {
            @Override
            public void onResponse(String response) {

                Log.d("aslkdjasñjld", response);

                response = response.trim();
                if (response.contentEquals("-1")) {
                    PersonsToday.saveLastDate(context, new Date().getDate());
                    EventBus.getDefault().post(new LoadingDismissEvent());
                    EventBus.getDefault().post(new ActionHomeEvent("limit"));
                } else if (response.contentEquals("[]")) {
                    EventBus.getDefault().post(new ActionHomeEvent("newConfig"));
                    EventBus.getDefault().post(new LoadingDismissEvent());
                } else {
                    Log.d("Response_", response);

                    JSONArray json = null;
                    try {
                        json = new JSONArray(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    for (int i = 0; i < (json != null ? json.length() : 0); i++) {
                        try {
                            String str = json.get(i).toString();
                            String strPerson = Tools.getValue(str, Tools.getString(R.string.person, context));
                            String post = Tools.getValue(str, Tools.getString(R.string.post, context));

                            String comment = Tools.getValue(post, Tools.getString(R.string.comment, context));

                            Person person = gson.fromJson(strPerson, Person.class);
                            if (post.isEmpty())
                                profileItems.add(person);
                            else {
                                profileItems.add(new Post(strPerson, comment));
                                Log.d("LogXXX", "response" + comment + " " + strPerson);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    Person person;
                    ShowPost showPost;
                    String post_url, address, name, title;
                    int age;
                    Collections.shuffle(profileItems);
                    for (Object obj : profileItems) {
                        if (obj instanceof Person) {
                            showProfileItems.add(obj);
                            MainActivity.publicPhotos.add(((Person) obj).getPublicPhotos(context));
                        } else {
                            person = gson.fromJson(((Post) obj).getPerson(), Person.class);
                            address = Tools.getAddress(person.getServer(), context);
                            post_url = Tools.getString(R.string.http, context) + address + "/" + pathPhoto() + "/"
                                    + person.getId() + "/" + Tools.getString(R.string.post, context) + Tools.getString(R.string.jpeg, context);
                            name = person.getName();
                            age = person.getAge();
                            if (name.length() > 17)
                                name = Tools.getFirstWords(name, 1);
                            title = name + ", " + age;
                            showPost = new ShowPost(post_url, person.getProfilePhotoLow(context), title, ((Post) obj).getComment());
                            showProfileItems.add(showPost);
                            MainActivity.publicPhotos.add(person.getPublicPhotos(context));
                        }
                    }
                    if (!Profile.isPremium(context)) {
                        //MainActivity.profileItems.clear();
                        //MainActivity.showProfileItems.clear();
                        MainActivity.profileItems.addAll(profileItems);
                        MainActivity.showProfileItems.addAll(showProfileItems);
                        PersonsToday.savePersonsToday(context, MainActivity.profileItems);
                        PersonsToday.saveLastDate(context, new Date().getDate());

                    } else {
                        MainActivity.profileItems.addAll(profileItems);
                        MainActivity.showProfileItems.addAll(showProfileItems);
                    }
                    EventBus.getDefault().post(new PersonsAddedEvent(nextPage));
                }


            }

            @Override
            public void onError(String error) {


                Log.d("klasjdjaldk", error + "/");
                EventBus.getDefault().post(new ActionHomeEvent("timeOut"));
                EventBus.getDefault().post(new LoadingDismissEvent());

            }
        });
    }

    public static void loadTopLikes(Context context, int time) {
        if (!Tools.isConnected(context)) {
            EventBus.getDefault().post(new TopLikesEvent("disconnect"));
            return;
        }
        CRequest request = new CRequest(context, getTopLikes(),
                15);
        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>(Tools.getString(R.string.auth, context), pass()));
        pairs.add(new Pair<>("time", String.valueOf(time)));

        request.makeRequest(pairs);
        Log.d("Log_", "makeRequestPremium");
        request.setOnResponseListener(new CRequest.OnResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.d("Log_", "response" + response);
                if (response.trim().isEmpty() || !response.trim().contains("woman")) {
                    EventBus.getDefault().post(new LoadingDismissEvent());
                    return;
                }
                ArrayList<HottestPerson> topWoman = new ArrayList<>();
                ArrayList<HottestPerson> topMan = new ArrayList<>();

                if (!response.isEmpty()) {

                    JSONObject jsonObject = null;

                    try {
                        jsonObject = new JSONObject(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        EventBus.getDefault().post(new LoadingDismissEvent());
                        return;
                    }


                    JSONArray json = null;
                    try {
                        json = new JSONArray(jsonObject.getString("woman"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < (json != null ? json.length() : 0); i++) {
                        try {
                            String str = json.get(i).toString();
                            HottestPerson person = new Gson().fromJson(str, HottestPerson.class);
                            topWoman.add(person);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    json = null;
                    try {
                        json = new JSONArray(jsonObject.getString("man"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    for (int i = 0; i < (json != null ? json.length() : 0); i++) {
                        try {
                            String str = json.get(i).toString();
                            HottestPerson person = new Gson().fromJson(str, HottestPerson.class);
                            topMan.add(person);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }


                    MainActivity.topFull.clear();
                    MainActivity.topWoman.clear();
                    MainActivity.topMan.clear();
                    MainActivity.topFull.addAll(topWoman);
                    MainActivity.topFull.addAll(topMan);

                    Collections.sort(MainActivity.topFull, Collections.reverseOrder());
                    /*while (MainActivity.topFull.size() > 10)
                        MainActivity.topFull.remove(MainActivity.topFull.size() - 1);*/
                    MainActivity.topWoman.addAll(topWoman);
                    MainActivity.topMan.addAll(topMan);

                    EventBus.getDefault().post(new TopLikesEvent("notify"));

                }
            }

            @Override
            public void onError(String error) {
                EventBus.getDefault().post(new TopLikesEvent("timeOut"));
            }
        });
    }

    public static void actionSendMsg(String msg, Contact contact, Context context) {
        String username = contact.getChatUsername(context);
        Chat chat = Conversations.getChat(username);
        String message;
        String id = Tools.getSha256(msg + "" + new Date().getTime());
        String myAESKey = null;
        String encryptedMessage = null;
        String encryptedId = null;
        final ArrayList<Msg>[] messages = new ArrayList[]{new ArrayList<>()};


        if (chat == null) {
            String encryptedAESKey = null;
            try {
                myAESKey = Encryption.getSecretAESKeyAsString();

                encryptedAESKey = Encryption.encryptText(myAESKey, contact.getPublicKey());

                encryptedMessage = Encryption.encryptTextUsingAES(msg, myAESKey);

                encryptedId = Encryption.encryptTextUsingAES(contact.getId() + "", myAESKey);
            } catch (Exception e) {
                Log.d("MyAes", "Excaes" + e.getMessage());
                e.printStackTrace();
            }
            message = Tools.getJsonMsgWithContact(encryptedMessage, encryptedId, encryptedAESKey, Tools.profileToContactJson(Profile.person, "iceb"));
        } else if (chat.isPendingSendAesKey()) {
            String encryptedAESKey = null;
            myAESKey = chat.getMyAESKey();
            try {
                encryptedAESKey = Encryption.encryptText(myAESKey, contact.getPublicKey());
                encryptedMessage = Encryption.encryptTextUsingAES(msg, myAESKey);
                encryptedId = Encryption.encryptTextUsingAES(contact.getId() + "", myAESKey);
            } catch (Exception e) {
                e.printStackTrace();

            }

            message = Tools.getJsonMsgWithContact(encryptedMessage, encryptedId, encryptedAESKey, Tools.profileToContactJson(Profile.person, "iceb"));
        } else {
            String encryptedAESKey = null;
            myAESKey = chat.getMyAESKey();
            try {
                encryptedAESKey = Encryption.encryptText(myAESKey, contact.getPublicKey());
                encryptedMessage = Encryption.encryptTextUsingAES(msg, myAESKey);
                encryptedId = Encryption.encryptTextUsingAES(contact.getId() + "", myAESKey);
            } catch (Exception e) {
                e.printStackTrace();

            }
            message = Tools.getJsonMsgWithContact(encryptedMessage, encryptedId, encryptedAESKey, Tools.profileToContactJson(Profile.person, "iceb"));
        }

        boolean isSent = ChatHandler.sendMessage(message, username, id);

        boolean isMms = false;
        String name = contact.getName();
        //  String username = contact.getChatUsername(getApplicationContext());

        boolean isReply = false;
        PublicKey publicKey = contact.getPublicKey();

        String bodyWithOutReply = msg;
        if (isReply)
            bodyWithOutReply = Tools.getValue(msg, "body");

        if (!Tools.getValue(bodyWithOutReply, "audio").isEmpty())
            isMms = true;

        long time = new Date().getTime();
        if (!isSent) {

            Conversations.addPendingToSendMessage(new PendingMessage(message, username, id));

        }


    }

    public static void loadConfig(Context activity) {
        CRequest request = new CRequest(activity, getConfig(),
                10);
        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>(Tools.getString(R.string.auth, activity), pass()));
        request.makeRequest(pairs);
        Log.d("Log_", "makeRequestConfig");
        request.setOnResponseListener(new CRequest.OnResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.d("LogConfig", "response" + response);
                response = response.trim();
                String config = Tools.getValue(response, "config");
                String time = Tools.getValue(response, "time");
                Tools.setConfig(config, activity);
                AppHandler.dbTime = Tools.getDateFromDb(time);
                setConfigLoaded(true);

                if (!Tools.isServiceAvailable(activity)) {
                    Intent intent = new Intent(activity, InfoActivity.class);
                    intent.putExtra("cause", "service");
                    activity.startActivity(intent);
                    // cancelWorker(activity);
                    EventBus.getDefault().post(new ServiceShutDownEvent());

                }

            }

            @Override
            public void onError(String error) {
            }
        });
    }

    public static void changeToPremium(Context context, int days, boolean isFreeTrial) {

        String timeZone;

        String lat, lon;
        if (MainActivity.latitude != -500) {
            lat = MainActivity.latitude + "";
            lon = MainActivity.longitude + "";
        } else {
            lat = Profile.getLatitude();
            lon = Profile.getLongitude();
        }

        if (lat.isEmpty())
            lat = "37.4218521";

        if (lon.isEmpty())
            lon = "-122.0841308";

        timeZone = TimezoneMapper.latLngToTimezoneString(Double.valueOf(lat), Double.valueOf(lon));

        if (timeZone.isEmpty()) {
            processPremium(new Date(), days, isFreeTrial, context);
            return;
        }

        CRequest request = new CRequest(context, AppHandler.getTrueDate(), 15);
        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>("auth", AppHandler.pass()));
        pairs.add(new Pair<>("timeZone", timeZone));
        request.makeRequest(pairs);
        request.setOnResponseListener(new CRequest.OnResponseListener() {
            @Override
            public void onResponse(String response) {

                Log.d("alnskdmañda", "resp " + response);
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                try {
                    Date date = format.parse(response);
                    Log.d("alnskdmañda", date.getDate() + " / " + date.getMonth() + " / " + date.getYear());
                    processPremium(date, days, isFreeTrial, context);
                } catch (ParseException e) {
                    Log.d("alnskdmañda", "error " + e.getMessage());
                    processPremium(new Date(), days, isFreeTrial, context);
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String error) {

                processPremium(new Date(), days, isFreeTrial, context);
            }
        });


    }

    private static void processPremium(Date now, int days, boolean isFreeTrial, Context context) {

        String str = Profile.getPremium();
        if (str.isEmpty())
            str = "{}";
        str = Tools.putValue(str, Tools.getString(R.string.premium, context), "1");

        Date end = new Date();
        end.setDate(now.getDate() + days);
        ArrayList<Pair<String, String>> start_date = new ArrayList<>();
        start_date.add(new Pair<>(Tools.getString(R.string.day, context), now.getDate() + ""));
        start_date.add(new Pair<>(Tools.getString(R.string.month, context), now.getMonth() + ""));
        start_date.add(new Pair<>(Tools.getString(R.string.year, context), now.getYear() + ""));
        str = Tools.putValue(str, Tools.getString(R.string.start_date, context), Tools.getJson(start_date));
        ArrayList<Pair<String, String>> end_date = new ArrayList<>();
        end_date.add(new Pair<>(Tools.getString(R.string.day, context), end.getDate() + ""));
        end_date.add(new Pair<>(Tools.getString(R.string.month, context), end.getMonth() + ""));
        end_date.add(new Pair<>(Tools.getString(R.string.year, context), end.getYear() + ""));
        str = Tools.putValue(str, Tools.getString(R.string.end_date, context), Tools.getJson(end_date));
        if (isFreeTrial)
            str = Tools.putValue(str, Tools.getString(R.string.test_period, context), "1");
        Profile.setData(Tools.getString(R.string.premium, context), str, context);

        EventBus.getDefault().post(new UpdatePremiumEvent());
        AppNotifications.Notify(context, -5, "premium", ((BitmapDrawable) context.getDrawable(R.drawable.heart_golden)).getBitmap(), "¡Bienvenido a Both Premium! \uD83E\uDD29 \uD83D\uDD25", "Saca el máximo partido a nuestro servicio");
        updatePendingData(context);
    }

    public static void checkPremiumIsOver(Context context) {
        billingClient = BillingClient.newBuilder(context)
                .enablePendingPurchases()
                .setListener(
                        new PurchasesUpdatedListener() {
                            @Override
                            public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
                            }
                        }
                ).build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                checkLogic0(context);

            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                checkPremiumIsOver(context);
            }
        });


    }

    private static void checkLogic0(Context context) {


        Log.d("nalskdnalskdjalñsd", "well well");
        billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build(), (billingResult, list) -> {

            Log.d("nalskdnalskdjalñsd", "response" + billingResult.getResponseCode());
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && list != null) {
                if (list.size() > 0)
                    processChecking(context, true);
                else
                    processChecking(context, false);
            } else {
                // Handle any other error codes.
                processChecking(context, false);

            }
        });
    }

    private static void processChecking(Context context, boolean isPayingGoogle) {
        Log.d("asñlasmdddasd", "paying google " + isPayingGoogle);

        String renew = Tools.getSettings("renew_premium", context);

        if (!renew.isEmpty()) {

            renew_premium(context);
            Tools.saveSettings("renew_premium", "", context);
            return;

        }


        String timeZone;

        String lat, lon;
        if (MainActivity.latitude != -500) {
            lat = MainActivity.latitude + "";
            lon = MainActivity.longitude + "";
        } else {
            lat = Profile.getLatitude();
            lon = Profile.getLongitude();
        }

        if (lat.isEmpty())
            lat = "37.4218521";

        if (lon.isEmpty())
            lon = "-122.0841308";

        timeZone = TimezoneMapper.latLngToTimezoneString(Double.parseDouble(lat), Double.parseDouble(lon));

        if (timeZone.isEmpty())
            return;

        CRequest request = new CRequest(context, AppHandler.checkPremium(), 15);
        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>("auth", AppHandler.pass()));
        pairs.add(new Pair<>("id", Profile.getId()));
        pairs.add(new Pair<>("timeZone", timeZone));
        request.makeRequest(pairs);
        request.setOnResponseListener(new CRequest.OnResponseListener() {
            @Override
            public void onResponse(String response) {

                Log.d("alnskdmañda", "resp " + response);
                JSONObject jsonObject;
                String trueDate = "", premium;
                try {
                    jsonObject = new JSONObject(response);

                    trueDate = jsonObject.getString("trueDate");

                    premium = jsonObject.getString("premium");

                    Log.d("nalsdmañlsd", premium + "  // " + trueDate);

                    Profile.setData(Tools.getString(R.string.premium, context), premium, context);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

                if (trueDate.isEmpty())
                    return;

                try {
                    Date date = format.parse(trueDate);
                    Log.d("alnskdmañda", date.getDate() + " / " + date.getMonth() + " / " + date.getYear());
                    processPremiumIsOver(date, isPayingGoogle, context);
                } catch (ParseException e) {
                    Log.d("alnskdmañda", "error " + e.getMessage());
                    processPremiumIsOver(new Date(), isPayingGoogle, context);
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String error) {

                //Log.d("nalsdmañlsd", error + "  // " );
                processChecking(context, isPayingGoogle);
            }
        });


    }


    private static void processPremiumIsOver(Date now, boolean googleCurrentPay, Context context) {

        String premium = Profile.getPremium();
        String end_date = Tools.getValue(premium, Tools.getString(R.string.end_date, context));
        int year = Integer.parseInt(Tools.getValue(end_date, Tools.getString(R.string.year, context)));
        int month = Integer.parseInt(Tools.getValue(end_date, Tools.getString(R.string.month, context)));
        int day = Integer.parseInt(Tools.getValue(end_date, Tools.getString(R.string.day, context)));
        Date end = new Date(year, month, day);

        if (now.after(end)) {

            if (googleCurrentPay) {
                renew_premium(context);
                return;
            }

            String str = premium;
            str = Tools.putValue(str, Tools.getString(R.string.premium, context), "0");
            str = Tools.putValue(str, Tools.getString(R.string.start_date, context), "");
            str = Tools.putValue(str, Tools.getString(R.string.end_date, context), "");
            String finalStr = str;
            Profile.setData(Tools.getString(R.string.premium, context), finalStr, context);
            Log.d("Log_", "premiumIsOver");

        }
    }

    private static void renew_premium(Context context) {

        String premium = Profile.getPremium();

        Log.d("nasñldkamñslda", "prev prem " + premium);
        String start_date = Tools.getValue(premium, Tools.getString(R.string.start_date, context));

        int year = Integer.parseInt(Tools.getValue(start_date, Tools.getString(R.string.year, context)));
        int month = Integer.parseInt(Tools.getValue(start_date, Tools.getString(R.string.month, context)));
        int day = Integer.parseInt(Tools.getValue(start_date, Tools.getString(R.string.day, context)));

        Date prev_start = new Date(year, month, day);

        String end_date = Tools.getValue(premium, Tools.getString(R.string.end_date, context));

        year = Integer.parseInt(Tools.getValue(end_date, Tools.getString(R.string.year, context)));
        month = Integer.parseInt(Tools.getValue(end_date, Tools.getString(R.string.month, context)));
        day = Integer.parseInt(Tools.getValue(end_date, Tools.getString(R.string.day, context)));

        Date prev_end = new Date(year, month, day);

        int difDays = Tools.getDaysDifference(prev_start, prev_end);

        Log.d("nasñldkamñslda", "daysdif " + difDays + " prev start" + start_date + " prev end " + end_date);

        prev_end = Tools.addDaysToDate(prev_end, Calendar.DAY_OF_MONTH, difDays);

        premium = Tools.putValue(premium, Tools.getString(R.string.start_date, context), end_date);
        Log.d("nasñldkamñslda", "new_start " + end_date);

        ArrayList<Pair<String, String>> new_end_date = new ArrayList<>();
        new_end_date.add(new Pair<>(Tools.getString(R.string.day, context), prev_end.getDate() + ""));
        new_end_date.add(new Pair<>(Tools.getString(R.string.month, context), prev_end.getMonth() + ""));
        new_end_date.add(new Pair<>(Tools.getString(R.string.year, context), prev_end.getYear() + ""));


        Log.d("nasñldkamñslda", "new_end " + new_end_date);
        premium = Tools.putValue(premium, Tools.getString(R.string.end_date, context), Tools.getJson(new_end_date));

        Profile.setData(Tools.getString(R.string.premium, context), premium, context);

        Log.d("nasñldkamñslda", "prem " + premium);
        EventBus.getDefault().post(new UpdatePremiumEvent());
        updatePendingData(context);


    }


    public static void checkSetupIsComplete(Context context) {
        if (!Profile.getSetupComplete()) {
            if (Profile.isSetupComplete())
                Profile.setData(Tools.getString(R.string.setup_complete, context), "1", context);
        }

    }

    public static void loadCrash(Context context) {
        StringBuilder trace = new StringBuilder();
        String line;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.openFileInput("stack.trace")));
            while ((line = reader.readLine()) != null) {
                trace.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        context.deleteFile("stack.trace");

        if (trace.toString().isEmpty())
            return;

        Error error = new Error(trace.toString(), "crash");
        LogsErrors.addError(error, context);

    }

    public static void sendLogsErrors(Context context) {
        Log.d("Log_", "send Errors" + LogsErrors.getErrorsArray(context));
        if (!LogsErrors.getErrorsArray(context).isEmpty() && Tools.isConnected(context)) {
            CRequest request = new CRequest(context, logsErrors(),
                    20);

            ArrayList<Pair<String, String>> pairs = new ArrayList<>();
            pairs.add(new Pair<>(Tools.getString(R.string.errors, context), LogsErrors.getErrorsArray(context).toString()));
            pairs.add(new Pair<>(Tools.getString(R.string.id, context), Profile.getId()));
            pairs.add(new Pair<>(Tools.getString(R.string.auth, context), pass()));
            pairs.add(new Pair<>(Tools.getString(R.string.version, context), Build.VERSION.SDK_INT + ""));
            pairs.add(new Pair<>(Tools.getString(R.string.device, context), Tools.getDeviceName()));
            request.makeRequest(pairs);
            Log.d("Log_", "makeRequestErrors");
            request.setOnResponseListener(new CRequest.OnResponseListener() {
                @Override
                public void onResponse(String response) {
                    Log.d("Log_", "responseErrors" + response);
                    if (response.trim().isEmpty())
                        LogsErrors.removeErrors(context);

                }

                @Override
                public void onError(String error) {
                }
            });
        }
    }

    public static void addLike(Contact contact, Context context, boolean myLike) {
        Actions.addActions(Tools.getString(R.string.like, context), contact.getId(), context);

        Log.d("kasdadad", "add m " + contact.getId());
        int posContact = ContactsManager.getPosByUsername(contact.getChatUsername(context), context);

        if (posContact == -1) {
            ContactsManager.addContact(contact);

        } else {
            //ContactsManager.contacts.get(posContact).setType(Tools.getString(R.string.my_like, context));
            Log.d("kasdadad", "www");
            ContactsManager.addType(contact.getChatUsername(context), "megusta", context);
        }
       /* new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ContactsManager.verifyingMatches(context, myLike);
            }
        }, 1500);*/

        AppHandler.executor.submit(new Runnable() {
            @Override
            public void run() {
                Profile.setData(Tools.getString(R.string.contacts, context), ContactsManager.getSerializedContacts(), context);
            }
        });

        Log.d("LogX_", "like" + Actions.getActionsArray(context).toString());
    }

    public static void dislike(String username, Context context, boolean myDislike) {
        Log.d("ñeñer", "dislike");
        Contact contact = ContactsManager.getContact(username, context);
        if (contact == null)
            return;
        String type = contact.getType();
        String id = contact.getId();
        int pos = ContactsManager.getPosByUsername(username, context);

        if (type.contains(Tools.getString(R.string.match, context))) {
            String t = Tools.getString(R.string.mylike, context);
            if (myDislike) {
                Actions.addActions(Tools.getString(R.string.dislike, context), id, context);
                t = Tools.getString(R.string.like, context);
            }
            ContactsManager.getContact(username, context).setType(t);
            Conversations.deleteChat(username);
            ContactsUiUpdater.changeContacts();
            if (XMPPMessageServer.getConnection() != null)
                XMPPMessageServer.getChatHandler().removeRosterEntry(username);

        } else {
            Log.d("ñeñer", "removeCOntact");
            Actions.addActions(Tools.getString(R.string.dislike, context), id, context);
            ContactsManager.removeContact(pos);
            EventBus.getDefault().post(new LoadingDismissEvent());
        }
        AppHandler.executor.submit(new Runnable() {
            @Override
            public void run() {
                Profile.setData(Tools.getString(R.string.contacts, context), ContactsManager.getSerializedContacts(), context);

            }
        });
        Log.d("Log_", "dislike" + Actions.getActionsArray(context).toString());
    }

    public static void sendReason(Context context, String reason) {
        CRequest request = new CRequest(context, sendReason(), 15);
        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>(Tools.getString(R.string.auth, context), pass()));
        pairs.add(new Pair<>(Tools.getString(R.string.id, context), Profile.getId()));
        pairs.add(new Pair<>("reason", reason));
        request.makeRequest(pairs);
        request.setOnResponseListener(new CRequest.OnResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.d("ResponseReason", "res" + response);
                if (response.trim().isEmpty())
                    AppHandler.deleteAccount(context);
                else
                    Toast.makeText(context, "Error Inesperado", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Log.d("Error send reason", error);
                EventBus.getDefault().post(new LoadingDismissEvent());
                if (error.contains("TimeoutError"))
                    Toast.makeText(context, "Lo sentimos por la demora, intente nuevamente", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(context, "Por favor revise su conexión a internet", Toast.LENGTH_LONG).show();

            }
        });
    }

    public static void uploadToken(Context context, String token, String id) {
        Log.d("LogToken", "upload");
        if (token.isEmpty())
            return;
        CRequest request = new CRequest(context, uploadToken(), 15);
        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>(Tools.getString(R.string.auth, context), pass()));
        pairs.add(new Pair<>(Tools.getString(R.string.id, context), id));
        pairs.add(new Pair<>("token", token));
        request.makeRequest(pairs);
        request.setOnResponseListener(new CRequest.OnResponseListener() {
            @Override
            public void onResponse(String response) {

                if (response.contains("true"))
                    Tools.saveSettings("is_token_upload", "yes", context);


            }

            @Override
            public void onError(String error) {

                if (error.contains("TimeoutError"))
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            uploadToken(context, token, id);
                        }
                    }, 3000);


                Log.d("LogToken", "error");
            }
        });
    }

    public static void updateCantViews(Context context, int cant) {

        if (context == null)
            return;

        CRequest request = new CRequest(context, updateCantViews(), 35);
        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>(Tools.getString(R.string.auth, context), pass()));
        pairs.add(new Pair<>(Tools.getString(R.string.id, context), Profile.getId()));
        pairs.add(new Pair<>("cant", String.valueOf(cant)));


        request.makeRequest(pairs);
        request.setOnResponseListener(new CRequest.OnResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.d("nahsld124ada", response);

                if (response.contains("true"))
                    Tools.saveSettings("to_add_views", "", context);
            }

            @Override
            public void onError(String error) {
                Log.d("nahsld124ada", "error " + error);

            }
        });
    }


    public static void updateIcebAction(Context context, int cantToday, String timestamp) {
        Log.d("alskd", timestamp + " ");
        if (timestamp.isEmpty())
            return;
        CRequest request = new CRequest(context, updateIceb(), 15);
        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>(Tools.getString(R.string.auth, context), pass()));
        pairs.add(new Pair<>(Tools.getString(R.string.id, context), Profile.person.getId()));
        pairs.add(new Pair<>("cant", String.valueOf(cantToday)));
        pairs.add(new Pair<>("timestamp", timestamp));
        request.makeRequest(pairs);
        request.setOnResponseListener(new CRequest.OnResponseListener() {
            @Override
            public void onResponse(String response) {

                Log.d("alskd", response);


            }

            @Override
            public void onError(String error) {


            }
        });
    }


    public static void sendPushRequest(Context context, String toId) {

        if (System.currentTimeMillis() - lastPushNotificationTimestamp < 30000)
            return;

        if (toId.isEmpty())
            return;

        CRequest request = new CRequest(context, sendPushNotification(), 15);
        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>(Tools.getString(R.string.auth, context), pass()));
        pairs.add(new Pair<>("id", Profile.getId()));
        pairs.add(new Pair<>("toId", toId));
        request.makeRequest(pairs);
        request.setOnResponseListener(new CRequest.OnResponseListener() {
            @Override
            public void onResponse(String response) {

                lastPushNotificationTimestamp = System.currentTimeMillis();
                Log.d("lasjd", "puhs");
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public static void sendIcebPushRequest(Context context, String toId) {


        if (toId.isEmpty())
            return;

        CRequest request = new CRequest(context, sendPushNotificationIceB(), 15);
        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>(Tools.getString(R.string.auth, context), pass()));
        pairs.add(new Pair<>("name", Tools.getFirstWords(Profile.getName(), 1)));
        pairs.add(new Pair<>("toId", toId));
        request.makeRequest(pairs);
        request.setOnResponseListener(new CRequest.OnResponseListener() {
            @Override
            public void onResponse(String response) {


            }

            @Override
            public void onError(String error) {

            }
        });
    }


    public static void deletePostMeth(Context context) {
        CRequest request = new CRequest(context, deletePost(), 15);
        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>(Tools.getString(R.string.auth, context), pass()));
        pairs.add(new Pair<>(Tools.getString(R.string.id, context), Profile.getId()));
        request.makeRequest(pairs);
        request.setOnResponseListener(new CRequest.OnResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.d("ResponseReason", "res" + response);

                if (!response.trim().isEmpty())
                    Toast.makeText(context, "Error Inesperado", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onError(String error) {
                Log.d("Error send reason", error);
                if (error.contains("TimeoutError"))
                    Toast.makeText(context, "Lo sentimos por la demora, intente nuevamente", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(context, "Por favor revise su conexión a internet", Toast.LENGTH_LONG).show();

            }
        });


        String myAddress = Tools.getAddress(Profile.getServer(), context);
        CRequest request2 = new CRequest(context, Tools.getString(R.string.http, context) + myAddress + "/" + deletePostString(),
                20);


        ArrayList<Pair<String, String>> pairs2 = new ArrayList<>();
        pairs2.add(new Pair<>(Tools.getString(R.string.auth, context), passPhoto()));
        pairs2.add(new Pair<>(Tools.getString(R.string.id, context), Profile.getId()));

        request2.makeRequest(pairs2);
        request2.setOnResponseListener(new CRequest.OnResponseListener() {
            @Override
            public void onResponse(String response) {
                EventBus.getDefault().post(new LoadingDismissEvent());
                // Log.d("DeletePhoto_",response);


            }

            @Override
            public void onError(String error) {
                EventBus.getDefault().post(new LoadingDismissEvent());
                Toast.makeText(context, "Intento fallido", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void cancelExpDateSearch(Context context) {


        CRequest request = new CRequest(context, cancelExpDate(), 15);
        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>(Tools.getString(R.string.auth, context), pass()));
        pairs.add(new Pair<>(Tools.getString(R.string.id, context), Profile.getId()));
        request.makeRequest(pairs);
        request.setOnResponseListener(new CRequest.OnResponseListener() {
            @Override
            public void onResponse(String response) {

                Log.d("lkasdadad", response + " ok");

            }

            @Override
            public void onError(String error) {

                if (error.contains("TimeoutError"))
                    cancelExpDateSearch(context);

            }
        });


    }

    public static void deleteAccount(Context context) {
        String myAddress = Tools.getAddress(Profile.getServer(), context);
        CRequest request = new CRequest(context, Tools.getString(R.string.http, context) + myAddress + "/" + deleteAllPhotos(),
                20);

        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>(Tools.getString(R.string.auth, context), passPhoto()));
        pairs.add(new Pair<>(Tools.getString(R.string.id, context), Profile.getId()));
        pairs.add(new Pair<>("cant", Profile.getCantPublicPhotos() + ""));
        request.makeRequest(pairs);
        request.setOnResponseListener(new CRequest.OnResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.d("DeleteAccount", "responsePhoto" + response);
                if (response.trim().isEmpty()) {
                    CRequest request = new CRequest(context, deleteAccount(),
                            20);
                    ArrayList<Pair<String, String>> pairs = new ArrayList<>();
                    pairs.add(new Pair<>(Tools.getString(R.string.auth, context), pass()));
                    pairs.add(new Pair<>(Tools.getString(R.string.id, context), Profile.getId()));
                    pairs.add(new Pair<>(Tools.getString(R.string.address, context), Tools.getAddress(Profile.getServer(), context)));
                    request.makeRequest(pairs);
                    request.setOnResponseListener(new CRequest.OnResponseListener() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("DeleteAccount", "responseAccount" + response);
                            if (response.trim().isEmpty()) {
                                new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Tools.clearStorage(context);
                                    }
                                }, 1500);
                            } else {
                                EventBus.getDefault().post(new LoadingDismissEvent());
                                Toast.makeText(context, "Error al eliminar su cuenta, intente luego por favor.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(String error) {
                            Log.d("ErrorDeleteAccount_", error);
                            EventBus.getDefault().post(new LoadingDismissEvent());
                            if (error.contains("TimeoutError"))
                                Toast.makeText(context, "Lo sentimos por la demora, intente nuevamente", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(context, "Por favor revise su conexión a internet", Toast.LENGTH_LONG).show();

                        }
                    });
                } else {
                    EventBus.getDefault().post(new LoadingDismissEvent());
                    Toast.makeText(context, "Error al eliminar su cuenta, intente luego por favor.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Log.d("ErrorDeletePhoto_", error);
                EventBus.getDefault().post(new LoadingDismissEvent());
                if (error.contains("TimeoutError"))
                    Toast.makeText(context, "Lo sentimos por la demora, intente nuevamente", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(context, "Por favor revise su conexión a internet", Toast.LENGTH_LONG).show();

            }
        });


    }

    public static void processAllContacts(String strContacts, Context context) {
        ArrayList<Contact> contacts = new ArrayList<>();
        ArrayList<Contact> newContacts = new ArrayList<>();
        JSONArray json = null;
        try {
            json = new JSONArray(strContacts);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < (json != null ? json.length() : 0); i++) {
            try {
                String str = json.get(i).toString();
                String strPerson = Tools.getValue(str, Tools.getString(R.string.person, context));
                String type = Tools.getValue(str, Tools.getString(R.string.type, context));
                boolean isNew = Tools.getValue(str, Tools.getString(R.string.is_new, context)).contentEquals("1");

                String strContact = Tools.putValue(strPerson, Tools.getString(R.string.type, context), type);
                Contact contact = new Gson().fromJson(strContact, Contact.class);

                if (isNew) {
                    newContacts.add(contact);
                } else {
                    contacts.add(contact);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        ContactsManager.setContacts(contacts);

        for (Contact contact : newContacts) {
            String username = contact.getChatUsername(context);
            if (ContactsManager.getContact(username, context) == null)
                ContactsManager.addContact(contact);
        }

        // ContactsManager.verifyingMatches(context, false);

        Log.d("Log_", "contacts" + ContactsManager.getContacts().toString());
        AppHandler.executor.submit(new Runnable() {
            @Override
            public void run() {
                Profile.setData(Tools.getString(R.string.contacts, context), ContactsManager.getSerializedContacts(), context);
            }
        });

    }

    public static void createNotificationChannels(Context context) {
        NotificationChannel chat_channel, activity_channel, app_channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chat_channel = new NotificationChannel(AppHandler.MESSAGING_CHANNEL, "Chat", NotificationManager.IMPORTANCE_HIGH);
            chat_channel.enableVibration(true);
            activity_channel = new NotificationChannel(AppHandler.ACTIVITY_CHANNEL, "LikesAndMatches", NotificationManager.IMPORTANCE_HIGH);
            activity_channel.enableVibration(true);
            app_channel = new NotificationChannel(AppHandler.APP_CHANNEL, "App", NotificationManager.IMPORTANCE_HIGH);
            app_channel.enableVibration(true);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(chat_channel);
            notificationManager.createNotificationChannel(activity_channel);
            notificationManager.createNotificationChannel(app_channel);
        }
    }

    public static boolean isMainActivityOpen() {
        return MainActivityOpen;
    }

    public static void setMainActivityOpen(boolean mainActivityOpen) {
        AppHandler.MainActivityOpen = mainActivityOpen;
    }

    public static boolean isChatActivityOpen() {
        return ChatActivityOpen;
    }

    public static void setChatActivityOpen(boolean chatActivityOpen) {
        AppHandler.ChatActivityOpen = chatActivityOpen;
    }

    public static boolean isOpenFromChat() {
        return openFromChat;
    }

    public static void setOpenFromChat(boolean openFromChat) {
        AppHandler.openFromChat = openFromChat;
    }

    public static boolean isOpeningActivityOpen() {
        return OpeningActivityOpen;
    }

    public static void setOpeningActivityOpen(boolean openingActivityOpen) {
        OpeningActivityOpen = openingActivityOpen;
    }

    public static boolean isNewWorkerConnecting() {
        return newWorkerConnecting;
    }

    public static void setNewWorkerConnecting(boolean newWorkerConnecting) {
        AppHandler.newWorkerConnecting = newWorkerConnecting;
    }

    public static boolean isConfigLoaded() {
        return configLoaded;
    }

    public static void setConfigLoaded(boolean configLoaded) {
        AppHandler.configLoaded = configLoaded;
    }

    public static boolean isStartingWorker() {
        return startingWorker;
    }

    public static void setStartingWorker(boolean startingWorker) {
        AppHandler.startingWorker = startingWorker;
    }

    public static boolean isStartingChatService() {
        return startingChatService;
    }

    public static void setStartingChatService(boolean startingChatService) {
        AppHandler.startingChatService = startingChatService;
    }


    public static boolean isClosingConnection() {
        return closingConnection;
    }

    public static void setClosingConnection(boolean closingConnection) {
        AppHandler.closingConnection = closingConnection;
    }

}
