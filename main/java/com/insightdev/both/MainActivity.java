package com.insightdev.both;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Network;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryPurchasesParams;
import com.facebook.CallbackManager;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.facebook.common.internal.Supplier;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImageTranscoderType;
import com.facebook.imagepipeline.core.MemoryChunkType;
import com.google.android.ads.mediationtestsuite.MediationTestSuite;
import com.google.android.gms.ads.AdInspectorError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnAdInspectorClosedListener;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.insightdev.both.viewmodels.GetPricesViewModel;

import com.insightdev.both.viewmodels.ReverseGeoViewModel;
import com.insightdev.both.viewmodels.StripeCardViewModel;

import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.paymentsheet.PaymentSheet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    public static BottomNavigationView bottomNavigationView;
    public static CustomViewPager viewPager;
    public static ArrayList<Object> profileItems = new ArrayList<>();
    public static ArrayList<Object> showProfileItems = new ArrayList<>();
    public static ArrayList<ArrayList<String>> publicPhotos = new ArrayList<>();
    public static ArrayList<Integer> currentImages = new ArrayList<>();
    public static ArrayList<HottestPerson> topFull = new ArrayList<>();
    public static ArrayList<HottestPerson> topWoman = new ArrayList<>();
    public static ArrayList<HottestPerson> topMan = new ArrayList<>();
    public static DisplayMetrics displayMetrics;
    private Uri imageUri;
    public static int galleryHeight;
    public static int screenHeight;
    public static String editImagetUri = "";
    private static final int REQUEST_IMAGE_CAPTURE = 1001;
    private static final int REQUEST_CHOOSE_IMAGE = 1002;
    public static final int REQUEST_CODE_LOCATION_SETTINGS = 8989;
    public static final int REQUEST_INSTAGRAM = 1000;
    public static final int REQUEST_APP_URL = 2000;
    public static final int REQUEST_EXP_DATE = 2024;
    public static CallbackManager callbackManager;
    public static LocationManager locationManager;
    public static double longitude = -500, latitude = -500;
    private ReverseGeoViewModel reverseGeoViewModel;
    private LoadingDialog loadingDialog;
    private WebView v1, v2;
    public static int PERMISSION_ALL = 0;
    public static String[] PERMISSIONS;
    private static final String SIZE_SCREEN = "w:screen";
    private static final String SIZE_1024_768 = "w:1024";
    private static final String SIZE_640_480 = "w:640";
    private String selectedSize = SIZE_1024_768;
    private NetworkReceiver receiver;
    private BillingClient billingClient;
    private boolean isLost = false;
    private boolean isLoadingShow = false;
    PendingPayment pendingPaymentDialog;
    int actualPulses = 0;
    String clientId, ephKey, secret;
    StripeCardViewModel stripeCardViewModel;
    String clientSecret;


    long lastMillis = 0;

    PaymentModel paymentModel;

    private Stripe stripe;

    public static int viewsOk;
    PaymentSheet paymentSheet;


    NativeAd nativeAd;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT <= 28) {
            PERMISSIONS = new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            };
        } else {
            PERMISSIONS = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            };
        }
        pendingPaymentDialog = new PendingPayment();
        loadingDialog = new LoadingDialog();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Supplier<MemoryCacheParams> memoryCacheParamsSupplier = new Supplier<MemoryCacheParams>() {
            private MemoryCacheParams cacheParams = new MemoryCacheParams(1000000, 1, 0, 0, 1000000);

            @Override
            public MemoryCacheParams get() {
                return cacheParams;
            }
        };

        stripe = new Stripe(
                getApplicationContext(),
                "pk_test_51KujHcHjbd308KiSR7fzDD0ESDGMbbCRpfFWsq5CCEN6rVZjh793S3y84u1RDgKaEcrQCn3tXsRbB8qWphD6ShqC00g2F7M6ME"
        );


        /*AudienceNetworkAds.initialize(this);

        List<String> testDeviceIds = Arrays.asList("0A6E8523381F4C3B8DE1DCC662A61444");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);


        MediationTestSuite.launch(MainActivity.this);*/

        Fresco.initialize(
                this,
                ImagePipelineConfig.newBuilder(this)
                        .setBitmapMemoryCacheParamsSupplier(memoryCacheParamsSupplier)
                        .setMemoryChunkType(MemoryChunkType.BUFFER_MEMORY)
                        .setImageTranscoderType(ImageTranscoderType.JAVA_TRANSCODER)
                        .experiment().setNativeCodeDisabled(true)
                        .build());

        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(
                        new PurchasesUpdatedListener() {
                            @Override
                            public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
                                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                                        && list != null) {
                                    for (Purchase purchase : list) {
                                        handlePurchase(purchase);
                                    }
                                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                                    // Handle an error caused by a user cancelling the purchase flow.
                                } else {
                                    // Handle any other error codes.
                                }
                            }
                        })
                .build();

        PaymentConfiguration.init(this, "pk_test_51KujHcHjbd308KiSR7fzDD0ESDGMbbCRpfFWsq5CCEN6rVZjh793S3y84u1RDgKaEcrQCn3tXsRbB8qWphD6ShqC00g2F7M6ME");
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        viewPager = findViewById(R.id.mainViewPager);
        bottomNavigationView = findViewById(R.id.bottomNavBar);
        bottomNavigationView.setItemIconTintList(null);
        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), 6);

        viewPager.setAdapter(mainPagerAdapter);
        viewPager.setOffscreenPageLimit(5);

        stripeCardViewModel = ViewModelProviders.of(this).get(StripeCardViewModel.class);

        reverseGeoViewModel = ViewModelProviders.of(this).get(ReverseGeoViewModel.class);


        reverseGeoViewModel.getReverseModelLiveData().observe(this, new Observer<LocationModel>() {
            @Override
            public void onChanged(LocationModel locationModel) {

                Log.d("Location_", locationModel.getCity() + " / " + locationModel.getCountry());


                if (locationModel.getCountry().isEmpty() || !locationModel.isSuccessful()) {

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {

                            reverseGeoViewModel.getLocationInfo(getApplicationContext());

                        }
                    }, 2000);

                    return;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Tools.saveSettings("location_object", new Gson().toJson(locationModel), getApplicationContext());
                        // Tools.saveSettings("location_country", locationModel.country, getContext());

                        Log.d("aksldadmad", RegionsUtils.getRegion(locationModel.getCountryCode()));


                        Profile.setData(Tools.getString(R.string.city, MainActivity.this), locationModel.getCity(), MainActivity.this);
                        Profile.setData("country", locationModel.getCountry(), MainActivity.this);
                        Profile.setData("country_code", locationModel.getCountryCode(), MainActivity.this);
                        Constants.COUNTRY_CODE = locationModel.getCountryCode();
                        Profile.setData("region", RegionsUtils.getRegion(locationModel.getCountryCode()), MainActivity.this);
                        //databaseViewModel.updateRegion(getContext(), locationModel);

                        EventBus.getDefault().post(new LocationEvent(locationModel));
                        EventBus.getDefault().post(new BasicInfoEvent());

                    }
                }).start();


            }
        });

        stripeCardViewModel.getResponseLiveData().observe(this, new Observer<JSONObject>() {
            @Override
            public void onChanged(JSONObject s) {

                pendingPaymentDialog.dismissDialog();

                if (s != null) {

                    try {

                        clientSecret = s.getString("client_secret");

                        String status = s.getString("status");

                        if (status.equals("succeeded")) {

                            new OkPaymentDialog().showDialog(MainActivity.this, null);
                            AppHandler.changeToPremium(getApplicationContext(), paymentModel.getPlanDuration() * 31, false);

                        } else if (status.equals("payment_failed")) {

                            new ErrorPaymentDialog().showDialog(MainActivity.this);

                        } else {

                            stripe.handleNextActionForPayment(MainActivity.this, clientSecret);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else
                    new ErrorPaymentDialog().showDialog(MainActivity.this);

            }
        });


        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.home:
                                EventBus.getDefault().post(new ProfileDismissEvent());
                                viewPager.setCurrentItem(0, false);
                                EventBus.getDefault().post(new ActionHomeEvent(""));
                                break;
                            case R.id.premium:
                                EventBus.getDefault().post(new ProfileDismissEvent());
                                viewPager.setCurrentItem(1, false);
                                EventBus.getDefault().post(new UpdatePremiumEvent());
                                checkPendingPayment();
                                break;
                            case R.id.chats:
                                EventBus.getDefault().post(new ProfileDismissEvent());
                                viewPager.setCurrentItem(2, false);
                                break;
                            case R.id.profile:
                                EventBus.getDefault().post(new ProfileDismissEvent());
                                viewPager.setCurrentItem(3, false);
                                break;
                        }
                        return true;
                    }
                });


        // BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.chats);
        //badgeDrawable.setBackgroundColor(Color.parseColor("#ff0046"));
        //badgeDrawable.setVisible(true);
        //  badgeDrawable.setNumber(3);

        screenHeight = displayMetrics.heightPixels - 120;
        galleryHeight = (displayMetrics.widthPixels - (4 * Tools.dpIntoPx(7, this))) / 3;

        /*String pkg = getPackageName();

        PowerManager powerManager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            powerManager = getSystemService(PowerManager.class);
            if (!powerManager.isIgnoringBatteryOptimizations(pkg)) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:" + pkg));
                        startActivity(intent);
                    }
                }, 8000);
            }
        }*/


        new Thread(new Runnable() {
            @Override
            public void run() {


                if (!Tools.getSettings("is_token_upload", getApplicationContext()).equals("yes"))
                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<String> task) {

                            if (!task.isSuccessful())
                                return;
                            AppHandler.uploadToken(getApplicationContext(), task.getResult(), Profile.getId());

                        }
                    });


                checkIf24HExpDate();
                checkIfNewExpDate();
                chechIfBothAd();

            }
        }).start();


        receiver = new NetworkReceiver(MainActivity.this);
        receiver.setOnConnectivityListener(new NetworkReceiver.OnConnectivityListener() {
            @Override
            public void Available(Network network) {
                if (isLost)
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (!AppHandler.isConfigLoaded())
                                AppHandler.loadConfig(MainActivity.this);
                        }
                    }).start();
            }

            @Override
            public void Lost(Network network) {

            }
        });
        receiver.register();

        if (!Tools.isConnected(MainActivity.this))
            isLost = true;

        AppHandler.setMainActivityOpen(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Tools.isConnected(getApplicationContext())) {
                    AppHandler.loadCrash(MainActivity.this);
                    AppHandler.sendLogsErrors(MainActivity.this);
                    if (!AppHandler.isConfigLoaded())
                        AppHandler.loadConfig(MainActivity.this);
                }
            }
        }).start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkAppVersion();
            }
        }, 18000);

        EventBus.getDefault().register(this);
        if (AppHandler.isOpenFromChat()) {
            AppHandler.setOpenFromChat(false);
            openChatFragment();
            return;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initAdsArray(MainActivity.this);
                loadingBackAds();
            }
        }, 10000);


        //  Log.d("lashdjkl", new Gson().toJson(Tools.myProfileToContact(Profile.person)));

        Bundle bundle = getIntent().getExtras();
        if (bundle == null)
            return;
        if (bundle.getBoolean("notification", false)) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    Profile.Load(getApplicationContext());
                    Profile.loadOrCreateRSAKeys(getApplicationContext());
                    if (Tools.isConnected(getApplicationContext())) {
                        new Handler(getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!AppHandler.isStartingWorker()) {
                                    Log.d("Connection", "notStartingWorker");
                                    if (XMPPMessageServer.getConnection() == null) {
                                        EventBus.getDefault().post(new ReconnectWorkerEvent());
                                    } else if (!XMPPMessageServer.getConnection().isConnected()) {
                                        EventBus.getDefault().post(new ReconnectWorkerEvent());
                                    }
                                }
                                //  AppHandler.addWorkerConnection(getApplicationContext());

                            }
                        }, 10000);
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Tools.initConfig(MainActivity.this);
                            }
                        });
                        ContactsManager.loadContactDBDriver(getApplicationContext());
                        Conversations.loadDatabaseDrivers(getApplicationContext());
                        AppHandler.loadInternalContacts(getApplicationContext());
                        AppHandler.loadMessagesFromInternalDatabase();


                        if (Profile.isPremium(getApplicationContext()))
                            AppHandler.checkPremiumIsOver(getApplicationContext());


                    }
                    String type = bundle.getString(Tools.getString(R.string.type, getApplicationContext()), "");
                    if (type.contentEquals(Tools.getString(R.string.reload, getApplicationContext()))) {
                        Log.d("Notify_", "reload");
                        return;
                    }

                    if (type.contentEquals("complete_profile")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("Notify_", "complete");
                                openInfoProfileFragment();
                            }
                        });
                        return;
                    }

                    if (type.contentEquals("premium")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("Notify_", "premium");
                                openPremiumFragment();
                            }
                        });
                        return;
                    }
                    Log.d("Notify_", "chat");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            openChatFragment();
                        }
                    });

                    if (type.contentEquals(Tools.getString(R.string.match, getApplicationContext()))) {
                        String id = bundle.getString(Tools.getString(R.string.id, getApplicationContext()));
                        String username = ContactsManager.getContact(Integer.parseInt(id)).getChatUsername(getApplicationContext());
                        ContactsUiUpdater.openMatchScreen(username);
                    }

                }
            }).start();

        }


    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 4 || viewPager.getCurrentItem() == 5) {
            openProfileFragment();
        } else {
            if (System.currentTimeMillis() >= lastMillis + 2500) {
                lastMillis = System.currentTimeMillis();
                ProToast.makeText(getApplicationContext(), R.drawable.ic_close_app, "Pulse una vez más para salir", Toast.LENGTH_SHORT);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AppHandler.updatePendingData(getApplicationContext());

                        AppHandler.updatePendingActions(getApplicationContext());
                    }
                }).start();

                return;
            }
            cleanArrays();
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //AppHandler.checkPremiumIsOver(getApplicationContext());
        AppHandler.setMainActivityOpen(true);
        Log.d("aklsd", "mainStart ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppHandler.loadPendingInMessages(getApplicationContext());

                if (Tools.isConnected(MainActivity.this)) {
                    AppHandler.updatePendingData(getApplicationContext());
                    AppHandler.updatePendingActions(getApplicationContext());
                    AppHandler.sendPresence("en línea");
                }

                AppHandler.runService(getApplicationContext());

                String check = Tools.getSettings("back_from_exp_activity", getApplicationContext());

                if (!check.isEmpty()) {

                    ProfileDialogFragment profileDialogFragment = new ProfileDialogFragment();

                    if (profileDialogFragment.isAdded())
                        return;
                    Bundle bundle = new Bundle();
                    bundle.putString("person", new Gson().toJson(ContactsManager.getContact(Integer.valueOf(check))));
                    profileDialogFragment.setArguments(bundle);

                    profileDialogFragment.show(getSupportFragmentManager(), "ProfileDialog");

                    Tools.saveSettings("back_from_exp_activity", "", getApplicationContext());
                }


            }
        }).start();

    }


    @Override
    protected void onStop() {
        super.onStop();


        Tools.closeMainService(getApplicationContext());

        new Thread(new Runnable() {
            @Override
            public void run() {

                // if (Tools.isConnected(MainActivity.this)) {

                if (!AppHandler.isChatActivityOpen()) {
                    AppHandler.sendPresence("offline/" + new Date().getTime() + "/");
                }

                AppHandler.updatePendingData(getApplicationContext());

                AppHandler.updatePendingActions(getApplicationContext());


                //  }
                if (latitude != -500 && longitude != -500) {
                    Profile.setData(Tools.getString(R.string.latitude, getApplicationContext()), latitude + "", getApplicationContext());
                    Profile.setData(Tools.getString(R.string.longitude, getApplicationContext()), longitude + "", getApplicationContext());
                }
            }
        }).start();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        // AppHandler.addWorkerConnection(MainActivity.this);
        /*
        if (Tools.isMyServiceRunning(MainService.class, getApplicationContext()))
            stopService(new Intent(MainActivity.this, MainService.class));*/
        AppHandler.setMainActivityOpen(false);
        EventBus.getDefault().unregister(this);
        receiver.unregister();
        super.onDestroy();

    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();

        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearCaches();

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void serviceShutDown(ServiceShutDownEvent event) {
        Log.d("aklsd", "shutdown");
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadingShow(LoadingShowEvent event) {
        if (loadingDialog != null && isLoadingShow == false) {
            isLoadingShow = true;
            loadingDialog.showDialog(MainActivity.this, "Cargando");
            Log.d("LogXX", "loadingShow");
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadingDismiss(LoadingDismissEvent event) {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            isLoadingShow = false;
            Log.d("LogXX", "loadingDismiss");
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void activateListener(ActivateLocationListenerEvent event) {
        Log.d("nlaskjdmad", "activate");
        activateLocationListener();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void profileDismiss(ProfileDismissEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openMatchScreen(OpenMatchActivityEvent event) {
        Intent intent = new Intent(MainActivity.this, MatchActivity.class);
        intent.putExtra(Tools.getString(R.string.match, getApplicationContext()), event.getUsername());
        startActivity(intent);
    }


    static public void openPremiumFragment() {
        bottomNavigationView.setSelectedItemId(R.id.premium);
    }

    static public void openChatFragment() {
        bottomNavigationView.setSelectedItemId(R.id.chats);

    }

    static public void openHomeFragment() {

        bottomNavigationView.setSelectedItemId(R.id.home);

    }

    static public void openProfileFragment() {
        bottomNavigationView.setSelectedItemId(R.id.profile);
    }

    static public void openInfoProfileFragment() {
        /*if (viewPager.getCurrentItem() != 3) {
            openProfileFragment();
        }*/
        viewPager.setCurrentItem(4, false);
    }

    static public void openSettingsFragment() {
        if (viewPager.getCurrentItem() != 3) {
            openProfileFragment();
        }
        viewPager.setCurrentItem(5, false);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("ActivityResult_", requestCode + "//" + resultCode);
        if (REQUEST_CODE_LOCATION_SETTINGS == requestCode) {
            if (Activity.RESULT_OK == resultCode) {
                EventBus.getDefault().post(new ActionHomeEvent(""));
            }
            return;
        }


        if (REQUEST_INSTAGRAM == requestCode || REQUEST_APP_URL == requestCode) {
            if (loadingDialog != null && data != null) {
                loadingDialog.dismiss();
                isLoadingShow = false;
            }
            return;
        }
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {
            imageUri = TakeImageDialog.imageUri;
            tryReloadAndDetectInImage(TakeImageDialog.from);
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CHOOSE_IMAGE && data != null) {
            imageUri = data.getData();
            tryReloadAndDetectInImage(TakeImageDialog.from);
        } else if (resultCode != RESULT_CANCELED && data != null) {
            if (callbackManager != null)
                callbackManager.onActivityResult(requestCode, resultCode, data);
            stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void tryReloadAndDetectInImage(int from) {
        if (imageUri == null) {
            return;
        }

        if (SIZE_SCREEN.equals(selectedSize)) {
            return;
        }

        Intent intent = new Intent(this, EditImage.class);
        intent.putExtra("uri", imageUri.toString());
        intent.putExtra("from", from);
        startActivity(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();

        billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build(), new PurchasesResponseListener() {
            @Override
            public void onQueryPurchasesResponse(@NonNull @NotNull BillingResult billingResult, @NonNull @NotNull List<Purchase> list) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                        && list != null) {
                    for (Purchase purchase : list) {
                        handlePurchase(purchase);
                    }
                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                    // Handle an error caused by a user cancelling the purchase flow.
                } else {
                    // Handle any other error codes.
                }
            }
        });


    }

    void handlePurchase(Purchase purchase) {

        int productDuration = Tools.getDurationByProduct(purchase.getProducts().get(0));

        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {

            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
                    @Override
                    public void onAcknowledgePurchaseResponse(@NonNull @NotNull BillingResult billingResult) {
                        AppHandler.changeToPremium(getApplicationContext(), productDuration * 31, false);
                    }
                });
            } else
                AppHandler.changeToPremium(getApplicationContext(), productDuration * 31, false);

        } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED)
            ProToast.makeText(getApplicationContext(), R.drawable.ic_info, "Estamos procesando su pago", Toast.LENGTH_SHORT);

        else if (purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE)
            ProToast.makeText(getApplicationContext(), R.drawable.ic_alert, "Ha ocurrido un error inesperado", Toast.LENGTH_SHORT);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults.length > 0 && isLocationGranted()) {
                Log.d("Log_", "PermissionGranted");
                activateLocationListener();
                EventBus.getDefault().post(new ActionHomeEvent(""));
            }

        }
    }

    private void enableLocationSettings() {
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(500)
                .setFastestInterval(500)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        LocationServices
                .getSettingsClient(this)
                .checkLocationSettings(builder.build())
                .addOnSuccessListener(this, (LocationSettingsResponse response) -> {

                })
                .addOnFailureListener(this, ex -> {
                    if (ex instanceof ResolvableApiException) {
                        // Location settings are NOT satisfied,  but this can be fixed  by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),  and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) ex;
                            resolvable.startResolutionForResult(MainActivity.this, REQUEST_CODE_LOCATION_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                    }
                });
    }

    public void activateLocationListener() {
        if ((ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            return;
        }

        enableLocationSettings();

        if (MainActivity.locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            MainActivity.locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 3000, 0, locationListenerNetwork);
        if (MainActivity.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            MainActivity.locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 3000, 0, locationListenerGps);

    }

    private final LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(final Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            Log.d("Location_", MainActivity.latitude + "netWork");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (Tools.getSettings("location_object", getApplicationContext()).isEmpty()) {
                        Log.d("Location_", "get infoo");
                        reverseGeoViewModel.getLocationInfo(getApplicationContext());
                    }
                }
            }).start();

            actualPulses++;

            if (actualPulses > 3) {
                locationManager.removeUpdates(this);
                actualPulses = 0;
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private final LocationListener locationListenerGps = new LocationListener() {

        public void onLocationChanged(final Location location) {

            MainActivity.longitude = location.getLongitude();
            MainActivity.latitude = location.getLatitude();

            Log.d("Location_", MainActivity.latitude + "gps");


            new Thread(new Runnable() {
                @Override
                public void run() {

                    if (Tools.getSettings("location_object", getApplicationContext()).isEmpty()) {
                        Log.d("Location_", "get infoo");
                        reverseGeoViewModel.getLocationInfo(getApplicationContext());
                    }
                }
            }).start();
            actualPulses++;

            if (actualPulses > 3) {
                locationManager.removeUpdates(this);
                actualPulses = 0;
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    public static boolean hasPermissions(Context context, String[] permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isLocationGranted() {
        return ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED));
    }


    private void checkAppVersion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Pair<Integer, Boolean> version = Tools.getAppVersionAvailable(MainActivity.this);
                if (Tools.isAppVersionHigher(version.first)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                new UpdateDialog().showDialog(MainActivity.this, version.second);
                            } catch (Exception ignored) {
                            }

                        }
                    });
                }
            }
        }).start();

    }

    private void loadingBackAds() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Tools.isBackAdsAvailable(MainActivity.this) && Tools.isConnected(MainActivity.this)) {
                    Tools.saveBackAds(MainActivity.this);
                    String[] urlArray = Tools.getBackAds(MainActivity.this).split(",");
                    Log.d("BackAds", "urlArray" + urlArray.length);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            v1 = findViewById(R.id.v1);
                            v1.getSettings().setJavaScriptEnabled(true);
                            v1.setWebViewClient(new WebViewClient() {
                                @Override
                                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                    return false;
                                }
                            });
                            v2 = findViewById(R.id.v2);
                            v2.getSettings().setJavaScriptEnabled(true);
                            v2.setWebViewClient(new WebViewClient() {
                                @Override
                                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                    return false;
                                }
                            });

                            if (urlArray.length > 0) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        v1.loadUrl(urlArray[0]);
                                        Log.d("BackAds", "load" + urlArray[0]);
                                    }
                                }, 3000);
                                if (urlArray.length > 1)
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            v2.loadUrl(urlArray[1]);
                                            Log.d("BackAds", "load" + urlArray[1]);
                                        }
                                    }, 8000);
                            }
                        }
                    });


                }
            }
        }).start();

    }

    private void initAdsArray(Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Profile.person == null || Tools.getConfig(context).isEmpty())
                    return;
                String defaultAds = Tools.getDefaultAds(context);
                String eroticAds = Tools.getEroticAds(context);
                String customAdsObject = Tools.getCustomAds(context);
                String customAds = "";

                String preferences = Profile.getPreferences();
                if (!preferences.isEmpty()) {
                    String[] tags = preferences.split("///");
                    String tagWithoutEmoji;
                    for (String tag : tags) {
                        tagWithoutEmoji = tag.split(" ")[0];
                        if (customAdsObject.contains(tagWithoutEmoji))
                            customAds += "," + Tools.getValue(customAdsObject, tagWithoutEmoji);
                    }
                }
                String complete = defaultAds + customAds;

                if (Tools.getSettings("erotic_ads_available", context).contentEquals("1"))
                    complete += "," + eroticAds;
                Log.d("Ad_", "adsUrls" + complete);
                AppHandler.limitAds = Tools.getCantAds(MainActivity.this);
                AppHandler.adsArray = complete.split(",");
            }
        }).start();


    }

    private void checkIfNewExpDate() {

        String dateExpId = Tools.getSettings("is_new_exp_date", getApplicationContext());

        Log.d("laksndakld", dateExpId);

        if (!dateExpId.isEmpty()) {

            Intent intent = new Intent(getApplicationContext(), ExpDateActivity.class);

            intent.putExtra("expDate", dateExpId);

            startActivity(intent);
        }

    }


    private void checkIf24HExpDate() {


        try {
            Log.d("ahlskdjalksdnñalkd", " have " + ContactsManager.getContact(21).getName() + " type " + ContactsManager.getContact(21).getType());

        } catch (Exception e) {
            Log.d("ahlskdjalksdnñalkd", e.getMessage() + " error");

        }

        ArrayList<ExpDateContact> expDateContacts = ContactsManager.getContactFromDB(getApplicationContext());

        boolean update = false;

        for (int i = 0; i < expDateContacts.size(); i++)

            if (System.currentTimeMillis() >= expDateContacts.get(i).getTimestamp() + 24 * 60 * 60 * 1000) {

                ContactsManager.removeContactFromDB(String.valueOf(expDateContacts.get(i).getId()), getApplicationContext());

                Contact contact = ContactsManager.getContact(expDateContacts.get(i).getId());

                if (contact != null) {
                    ContactsManager.removeType(contact.getChatUsername(getApplicationContext()), "expDate", getApplicationContext());

                    if (!contact.getType().contains("match"))
                        Conversations.deleteChat(contact.getChatUsername(getApplicationContext()));

                    if (contact.getType().trim().isEmpty())
                        ContactsManager.removeContact(ContactsManager.getPosByUsername(contact.getChatUsername(getApplicationContext()), getApplicationContext()));

                    update = true;

                }


            }

        if (update)
            AppHandler.executor.submit(new Runnable() {
                @Override
                public void run() {
                    Profile.setData(Tools.getString(R.string.contacts, getApplicationContext()), ContactsManager.getSerializedContacts(), getApplicationContext());
                }
            });


    }

    private void checkPendingPayment() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String jsonPaymentModel = Tools.getSettings("payment_model_pending", getApplicationContext());


                if (!jsonPaymentModel.isEmpty())
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            paymentModel = new Gson().fromJson(jsonPaymentModel, PaymentModel.class);

                            stripeCardViewModel.sendPaymentMethod(getApplicationContext(), paymentModel, 0);
                            pendingPaymentDialog.showDialog(MainActivity.this);
                        }
                    });

            }
        }).start();


    }


    private void cleanArrays() {
        profileItems.clear();
        showProfileItems.clear();
        publicPhotos.clear();
        currentImages.clear();
        topMan.clear();
        topFull.clear();
        topWoman.clear();
    }

    private final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        @NonNull
        private final WeakReference<MainActivity> activityRef;

        PaymentResultCallback(@NonNull MainActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        //If Payment is successful
        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {

            final MainActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                new OkPaymentDialog().showDialog(MainActivity.this, null);
                AppHandler.changeToPremium(getApplicationContext(), paymentModel.getPlanDuration() * 31, false);


            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method
                new ErrorPaymentDialog().showDialog(MainActivity.this);
            }
        }

        //If Payment is not successful
        @Override
        public void onError(@NonNull Exception e) {
            new ErrorPaymentDialog().showDialog(MainActivity.this);

        }
    }

    private void chechIfBothAd() {

        String bothAdJson = Tools.getSettings("new_both_ad", getApplicationContext());


        if (bothAdJson.isEmpty())
            return;

        BothAdDialogFragment profileDialogFragment = new BothAdDialogFragment();

        if (profileDialogFragment.isAdded())
            return;

        Bundle bundle = new Bundle();
        bundle.putString("adModel", bothAdJson);
        profileDialogFragment.setArguments(bundle);

        profileDialogFragment.show(((AppCompatActivity) MainActivity.this).getSupportFragmentManager(), "BothAdDialogFragment");
    }
}