package com.insightdev.both;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Network;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.location.LocationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;
import com.insightdev.both.viewmodels.HomeViewModel;
import com.insightdev.both.viewmodels.ReverseGeoViewModel;
import com.insightdev.both.viewmodels.UpdateDatabaseViewModel;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {


    LottieAnimationView iceBreaker, likeButton, likeAnim, expressDate;

    HomeViewModel homeViewModel;

    private CircularProgressIndicator expDateIndicator, iceBreakerIndicator;

    CancelExpDateDialog.ActionInterface actionInterface;

    ExpWaitingDialog.ActionInterface waitingInterface;

    NewFeatureExpDateDialog.ActionInterface expDateInterface;

    Collection<String> ids = new ArrayList<>();
    private ScrollView scrollView;
    private CircularImageView drawable;
    private TextView info, details;
    private Button action;
    private ViewPager viewPager;
    private ProfilePagerAdapter profilePagerAdapter;
    boolean isPendindExpDate = false;
    private NetworkReceiver receiver;
    private int pagePos, oldState, lastAdPos = 0, maxPage = -1, toAdd = 0;
    private boolean pendingAdLoad = true;
    private boolean isLost = false;
    private static final float ROT_MOD = -15f;

    ConstraintLayout buttonsContainer;

    private UpdateDatabaseViewModel databaseViewModel;

    IceBreakerDialog iceBreakerDialog = new IceBreakerDialog();

    NewFeatureDialog newFeatureDialog = new NewFeatureDialog();


    int iceBreakerPos;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InicioFragment.
     */
    // TODO: Rename and change types and number of parameters
    public HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        viewPager = view.findViewById(R.id.mainImage);

        scrollView = view.findViewById(R.id.scrollView);

        buttonsContainer = view.findViewById(R.id.buttonsContainer);

        drawable = view.findViewById(R.id.drawable);

        expDateIndicator = view.findViewById(R.id.expDateIndicator);

        iceBreakerIndicator = view.findViewById(R.id.iceBIndicator);

        expDateIndicator.hide();

        iceBreakerIndicator.hide();

        info = view.findViewById(R.id.comment);

        details = view.findViewById(R.id.details);

        action = view.findViewById(R.id.actionButton);

        likeButton = view.findViewById(R.id.likeButton);

        iceBreaker = view.findViewById(R.id.icebreaker);

        likeAnim = view.findViewById(R.id.likeAnim);

        expressDate = view.findViewById(R.id.express_date);

        expDateInterface = new NewFeatureExpDateDialog.ActionInterface() {
            @Override
            public void go() {
                expressDate.performClick();
            }
        };

        actionInterface = new CancelExpDateDialog.ActionInterface() {
            @Override
            public void methodOk() {

                cancelSearch();
            }
        };


        waitingInterface = new ExpWaitingDialog.ActionInterface() {
            @Override
            public void cancelWaiting() {

                cancelSearch();

            }
        };


        String pending_expDate = Tools.getSettings("pending_expDate", getContext());

        if (pending_expDate.isEmpty()) {

            expressDate.setMinAndMaxFrame(0, 12);

            expressDate.setFrame(0);

            isPendindExpDate = false;

        } else {

            expressDate.setMinAndMaxFrame(14, 26);

            expressDate.setFrame(14);

            isPendindExpDate = true;

        }

       /* if (!Tools.isMyServiceRunning(UpdateNetworkService.class, getContext()))
            getContext().startService(new Intent(getContext(), UpdateNetworkService.class));*/


        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(getActivity()));
        profilePagerAdapter = new ProfilePagerAdapter(getActivity(), MainActivity.currentImages, MainActivity.showProfileItems);
        viewPager.setAdapter(profilePagerAdapter);

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);

        databaseViewModel = ViewModelProviders.of(this).get(UpdateDatabaseViewModel.class);

        databaseViewModel.getResponseLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {

            }
        });


        homeViewModel.getTrueDateLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                iceBreakerIndicator.hide();

                if (!s.equals("error")) {

                    Contact contact = Tools.profileToContact((Person) MainActivity.profileItems.get(iceBreakerPos), "iceb");

                    if (!Objects.equals(Tools.getSettings("is_icebreaker", getContext()), "yes"))
                        newFeatureDialog.showDialog(getActivity(), contact, 0, s);
                    else
                        iceBreakerDialog.showDialog(getActivity(), contact, 0, s);
                } else
                    ProToast.makeText(getContext(), R.drawable.toast_offline, getString(R.string.revise_conexion), Toast.LENGTH_SHORT);


            }
        });

        homeViewModel.getfindExpRespLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                expDateIndicator.hide();


                if (!s.equals("error")) {


                    if (s.length() == 5) {

                        expressDate.setMinAndMaxFrame(14, 26);

                        expressDate.setFrame(14);

                        isPendindExpDate = true;

                        Tools.saveSettings("pending_expDate", "0,1", getContext());

                        new ExpWaitingDialog().showDialog(getActivity(), waitingInterface);


                    } else if (s.length() == 3) {

                        if (Profile.isPremium(getContext()))
                            ProToast.makeText(getContext(), R.drawable.ic_info, getString(R.string.limite_citas), Toast.LENGTH_SHORT);
                        else
                            new LimitExpDateDialog().showDialog(getActivity());

                    } else {

                        s = Tools.putValue(s, "status", "");
                        s = Tools.putValue(s, "type", "expDate");
                        Contact contact = null;
                        try {
                            contact = new Gson().fromJson(s, Contact.class);
                        } catch (Exception e) {

                        }
                        if (contact == null)
                            return;


                        if (!ContactsManager.checkAddContact(contact, getContext()))

                            ContactsManager.addType(contact.getChatUsername(getContext()), "expDate", getContext());


                        Intent intent = new Intent(getActivity(), ExpDateActivity.class);

                        intent.putExtra("expDate", contact.getId());

                        getActivity().startActivity(intent);

                    }


                } else
                    ProToast.makeText(getContext(), R.drawable.toast_offline, getString(R.string.revise_conexion), Toast.LENGTH_SHORT);
            }
        });


        expressDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                expressDate.playAnimation();


                if (!Objects.equals(Tools.getSettings("is_new_feature_exp_date", getContext()), "yes")) {
                    new NewFeatureExpDateDialog().showDialog(getActivity(), expDateInterface);
                    return;
                }

                if (!isPendindExpDate) {

                    expDateIndicator.show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int result = homeViewModel.findExpDateAction(getContext());
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (result == 0) {
                                        ProToast.makeText(getContext(), R.drawable.ic_location, getString(R.string.intentando_localizar), Toast.LENGTH_LONG);
                                        expDateIndicator.hide();
                                    } else if (result == 1) {
                                        ProToast.makeText(getContext(), R.drawable.ic_filter_grad, getString(R.string.configura_parametros_busqueda), Toast.LENGTH_SHORT);
                                        new FilterDialog().showDialog(getActivity(), 1);
                                        expDateIndicator.hide();
                                    }
                                }
                            });

                        }
                    }).start();
                } else
                    new CancelExpDateDialog().showDialog(getActivity(), actionInterface);
                //ProToast.makeText(getContext(), R.drawable.ic_info, "La búsqueda ha sido cancelada", Toast.LENGTH_LONG);
            }
        });

        iceBreaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iceBreakerIndicator.show();
                iceBreakerPos = viewPager.getCurrentItem();
                iceBreaker.playAnimation();
                homeViewModel.getTrueDate(getContext());
            }
        });

        likeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Person profile = ((Person) MainActivity.profileItems.get(viewPager.getCurrentItem()));
                            String username = profile.getChatUsername(getActivity());
                            Contact contact = ContactsManager.getContact(username, getActivity());
                            if (contact == null)
                                like();
                            else if (contact.getType().contains("match")) {
                                dislike(contact);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        likeButton.cancelAnimation();
                                        likeAnim.cancelAnimation();
                                        likeButton.setFrame(0);
                                        likeAnim.setFrame(0);
                                    }
                                });

                            } else if (!contact.getType().contains(Tools.getString(R.string.mylike, getActivity())))
                                like();

                        }
                    }).start();


                    if (likeButton.getFrame() != 0) {
                        likeButton.cancelAnimation();
                        likeAnim.cancelAnimation();
                        likeButton.setFrame(0);
                        likeAnim.setFrame(0);
                    }
                    likeButton.playAnimation();
                    likeAnim.playAnimation();
                }
                return true;
            }
        });


        viewPager.setPageTransformer(true, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(@NotNull View page, float position) {

                final float width = page.getWidth();
                final float height = page.getHeight();
                final float rotation = ROT_MOD * position * -1.25f;

                page.setPivotX(width * 0.5f);
                page.setPivotY(height);
                page.setRotation(rotation);

                page.setTranslationX(position < 0 ? 0f : -page.getWidth() * position);


            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.d("PosPerson_", "pageselected" + position);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("nahsld124ada", "pos " + position + " maxPage " + maxPage);
                        if (!Profile.isPremium(getContext()) && position >= maxPage) {
                            PersonsToday.saveCurrentPos(getContext(), position);
                        }


                    }
                }).start();
                pagePos = position;

                maxPage = Math.max(maxPage, position);

                if (MainActivity.showProfileItems.get(position) instanceof Person)
                    buttonsContainer.setVisibility(View.VISIBLE);
                else
                    buttonsContainer.setVisibility(View.INVISIBLE);


            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (pagePos == MainActivity.showProfileItems.size() - 1 && oldState - state == 1) {
                    //  if (Profile.isPremium(getContext())) {
                    EventBus.getDefault().post(new LoadingShowEvent());
                    if (!Tools.isConnected(getContext())) {
                        ProToast.makeText(getContext(), R.drawable.offline, getString(R.string.sin_conexion), Toast.LENGTH_SHORT);
                        EventBus.getDefault().post(new LoadingDismissEvent());
                        return;
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            updateToAdd(true);
                            AppHandler.loadPersons(getContext(), true, toAdd);
                        }
                    }).start();
                   /* } else {
                        new LimitDialog().showDialog(getActivity());
                    }*/
                }
                oldState = state;

            }
        });


        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = action.getText().toString();
                if (text.contentEquals("Completar perfil"))
                    MainActivity.openInfoProfileFragment();
                else if (text.contentEquals("Activar ubicación")) {
                    checkLocation();
                } else if (text.contentEquals("Conectar")) {
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                } else if (text.contentEquals("Ir a Premium"))
                    MainActivity.openPremiumFragment();
                else if (text.contentEquals("Reintentar"))
                    EventBus.getDefault().post(new ActionHomeEvent(""));
                else {
                    new FilterDialog().showDialog(getActivity(), 0);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (Tools.getSettings("first_time", getContext()).isEmpty())
                                Tools.saveSettings("first_time", "1", getContext());
                        }
                    }).start();
                }
            }
        });

        EventBus.getDefault().register(this);

        receiver = new NetworkReceiver(getContext());
        receiver.setOnConnectivityListener(new NetworkReceiver.OnConnectivityListener() {
            @Override
            public void Available(Network network) {
                if (isLost) {
                    EventBus.getDefault().post(new ActionHomeEvent(""));
                }
            }

            @Override
            public void Lost(Network network) {
                isLost = true;
            }
        });

        receiver.register();

        if (!Tools.isConnected(getContext()))
            isLost = true;
        if (MainActivity.viewPager.getCurrentItem() == 0)
            EventBus.getDefault().post(new ActionHomeEvent(""));


     /*   MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

                Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
                for (String adapterClass : statusMap.keySet()) {
                    AdapterStatus status = statusMap.get(adapterClass);
                    Log.d("balskdnadkadasd", String.format(
                            "Adapter name: %s, Description: %s, Latency: %d",
                            adapterClass, status.getDescription(), status.getLatency()));
                }

                AdLoader adLoader = new AdLoader.Builder(getContext(), "ca-app-pub-3940256099942544/2247696110")
                        .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                            @Override
                            public void onNativeAdLoaded(@NonNull @NotNull NativeAd nativeAd) {

                                Log.d("balskdnadkadasd", "okloaded " + nativeAd.getHeadline() + " current pos " + pagePos);
                                Log.d("ajskdhaljkhakwfa", " arr siz " + MainActivity.showProfileItems.size());
                                MainActivity.showProfileItems.add(pagePos + 2, nativeAd);
                                MainActivity.currentImages.add(pagePos + 2, 0);
                                MainActivity.publicPhotos.add(pagePos + 2, null);
                                Log.d("ajskdhaljkhakwfa", " arr siz " + MainActivity.showProfileItems.size());
                                profilePagerAdapter.notifyDataSetChanged();
                            }
                        })
                        .withAdListener(new AdListener() {
                            @Override
                            public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                                super.onAdFailedToLoad(loadAdError);
                                Log.d("balskdnadkadasd", "failed");
                            }
                        })
                        .withNativeAdOptions(new NativeAdOptions.Builder()
                                // Methods in the NativeAdOptions.Builder class can be
                                // used here to specify individual options settings.
                                .build())
                        .build();

                adLoader.loadAd(new AdRequest.Builder().build());
            }
        });*/

        return view;

    }


    @Override
    public void onStop() {
        super.onStop();


    }

    @Override
    public void onStart() {
        super.onStart();

        new Thread(new Runnable() {
            @Override
            public void run() {

                updateToAdd(false);

                String check = Tools.getSettings("pending_to_cancel", getContext());

                if (!check.isEmpty()) {
                    cancelSearch();
                    Tools.saveSettings("pending_to_cancel", "", getContext());
                }

            }
        }).start();
    }


    @Override
    public void onPause() {
        super.onPause();


        new Thread(new Runnable() {
            @Override
            public void run() {
                updateToAdd(false);
            }
        }).start();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        receiver.unregister();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void actionHome(ActionHomeEvent event) throws JSONException {
        Log.d("LogXX", "actionHome" + event.getActionResponse());
        Log.d("anskldnañgla", "actionHome " + event.getActionResponse());


        if (event.getActionResponse().contentEquals("limit")) {

            if (viewPager.getVisibility() == View.VISIBLE)
                new LimitDialog().showDialog(getActivity());

        }
        if (!MainActivity.profileItems.isEmpty()) {
            showViewPager();
            return;
        }
        Log.d("LogXX", "isEmptyArray" + event.getActionResponse());
        if (Profile.person == null) {
            Profile.Load(getContext());
            Profile.loadOrCreateRSAKeys(getContext());
            Conversations.loadDatabaseDrivers(getContext());
            ContactsManager.loadContactDBDriver(getContext());
            AppHandler.loadInternalContacts(getContext());
            AppHandler.loadMessagesFromInternalDatabase();
            if (Profile.isPremium(getContext()))
                AppHandler.checkPremiumIsOver(getContext());

        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("LogXX", "loadingShowHome");
                if (getActivity() == null || getContext() == null)
                    return;
                if (event.getActionResponse().contentEquals("timeOut")) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showInfo();
                            drawable.setImageResource(R.drawable.clock);
                            info.setText("Demoramos demasiado en responder...");
                            details.setText("Intente nuevamente conectar con nosotros");
                            action.setText("Reintentar");
                        }
                    });
                    return;
                }

                if (event.getActionResponse().contentEquals("newConfig")) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showInfo();
                            drawable.setImageResource(R.drawable.search);
                            info.setText("Lo sentimos");
                            details.setText("No pudimos encontrar personas con esas características, intente con otras");
                            action.setText("Configurar");
                        }
                    });
                    return;
                }


                if (!Profile.isPhotoUpLoad()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showInfo();
                            drawable.setImageResource(R.drawable.camera);
                            info.setText("¿Quieres empezar a conocer personas?");
                            details.setText("Antes, necesitas añadir al menos una foto");
                            action.setText("Completar perfil");
                        }
                    });
                    return;
                }

                if (!Profile.isSetupComplete()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showInfo();
                            drawable.setImageResource(R.drawable.account);
                            info.setText("¿Quieres empezar a conocer personas?");
                            details.setText("Antes, necesitamos saber algunos detalles sobre tí");
                            action.setText("Completar perfil");
                        }
                    });
                    return;
                }

                if (!isLocationEnabled() || !isLocationGranted()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showInfo();
                            drawable.setImageResource(R.drawable.location);
                            info.setText("¿Quieres empezar a conocer personas?");
                            details.setText("Antes, necesitas activar tu ubicación para continuar");
                            action.setText("Activar ubicación");
                        }
                    });
                    return;
                }

                if (MainActivity.latitude == -500) {
                    Log.d("Location_", "activatingList");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            EventBus.getDefault().post(new ActivateLocationListenerEvent());
                        }
                    });

                }

                if (!Tools.isConnected(getContext())) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showInfo();
                            drawable.setImageResource(R.drawable.offline);
                            info.setText("No hay conexión a internet...");
                            details.setText("Necesitas conectarte a una red para continuar");
                            action.setText("Conectar");
                        }
                    });
                    return;
                }

                if (Tools.getSettings("first_time", getContext()).isEmpty()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showInfo();
                            drawable.setImageResource(R.drawable.search);
                            info.setText("Encuentra a la persona que buscas...");
                            details.setText("Configura los parámetros de búsqueda, sexo, edad, etc...");
                            action.setText("Configurar");
                        }
                    });
                    return;
                }

                if (MainActivity.latitude == -500 && Profile.getLatitude().isEmpty()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showInfo();
                            drawable.setImageResource(R.drawable.location);
                            info.setText("No sabemos donde estás...");
                            details.setText("Puede demorar un poco localizarte");
                            action.setText("Reintentar");
                        }
                    });
                    return;
                }

                if (Profile.isPremium(getContext())) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showViewPager();
                        }
                    });
                    if (MainActivity.showProfileItems.isEmpty()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                EventBus.getDefault().post(new LoadingShowEvent());
                                updateToAdd(true);
                                AppHandler.loadPersons(getContext(), false, toAdd);
                            }
                        }).start();
                    }
                    return;
                }

                if (!Tools.isAvailableReload(getContext()) && !PersonsToday.loadPersonsToday(getContext())) {
                    if (!Profile.isPremium(getContext())) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showInfo();
                                drawable.setImageResource(R.drawable.limit_red);
                                info.setText("Has excedido el límite diario de visualizaciones");
                                details.setText("Necesitas pasarte a premium para desbloquear todas las funciones");
                                action.setText("Ir a Premium");
                            }
                        });
                    }
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showViewPager();
                        }
                    });
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (Tools.isAvailableReload(getContext())) {
                                EventBus.getDefault().post(new LoadingShowEvent());
                                updateToAdd(true);
                                AppHandler.loadPersons(getContext(), false, toAdd);

                            }
                        }
                    }).start();
                }
            }
        }).start();


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void applyFilters(ApplyFiltersEvent event) {


      /*  new Thread(new Runnable() {
            @Override
            public void run() {

                String min_age = Tools.getSettings("min_age", getContext());
                String max_age = Tools.getSettings("max_age", getContext());
                String gender = Tools.getSettings("gender", getContext());
                String regions = Tools.getSettings("regions", getContext());
                String distance = Tools.getSettings("distance", getContext());


                if (gender.isEmpty())
                    Tools.saveSettings("gender", "2", getContext());

                if (min_age.isEmpty())
                    Tools.saveSettings("min_age", "18", getContext());
                
                if (max_age.isEmpty())
                    Tools.saveSettings("max_age", "40", getContext());

                if (distance.isEmpty())
                    Tools.saveSettings("distance", "1000", getContext());

                if (regions.isEmpty())
                    Tools.saveSettings("regions", "0", getContext());

            }
        }).start();*/


        if (event.getFrom() == 0) {

            MainActivity.openHomeFragment();

            new Thread(new Runnable() {
                @Override
                public void run() {


                    if (MainActivity.showProfileItems.size() == 0) {
                        //updateToAdd(true);
                        //AppHandler.loadPersons(getContext(), false, toAdd);
                        return;
                    }
                    Log.d("nahsld124ada", "MaxPage " + maxPage);
                    int seen = maxPage + 1, size = MainActivity.showProfileItems.size() - 1;

                    while (size >= seen) {
                        Log.d("nahsld124ada", "remove " + size);
                        MainActivity.showProfileItems.remove(size);
                        MainActivity.profileItems.remove(size);
                        MainActivity.publicPhotos.remove(size);
                        size--;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            profilePagerAdapter.notifyDataSetChanged();
                            viewPager.setCurrentItem(maxPage);
                        }
                    });


                    updateToAdd(true);

                    AppHandler.loadPersons(getContext(), true, toAdd);
                }
            }).start();
        } else {


            expDateIndicator.show();

            Log.d("asldmañda", "ok0");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int result = homeViewModel.findExpDateAction(getContext());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Log.d("asldmañda", "ok result " + result);
                            if (result == 0) {
                                ProToast.makeText(getContext(), R.drawable.ic_location, "Estamos intentando localizarte, puede tardar unos segundos", Toast.LENGTH_LONG);
                                expDateIndicator.hide();
                            } else if (result == 1) {
                                ProToast.makeText(getContext(), R.drawable.ic_filter_grad, "Configura los parámetros de búsqueda", Toast.LENGTH_SHORT);
                                new FilterDialog().showDialog(getActivity(), 1);
                                expDateIndicator.hide();
                            }
                        }
                    });

                }
            }).start();
        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadPersonsFromStorage(PersonsStoredEvent event) {
        MainActivity.profileItems = event.getProfileItems();
        MainActivity.showProfileItems = event.getShowProfileItems();
        profilePagerAdapter = new ProfilePagerAdapter(getActivity(), MainActivity.currentImages, MainActivity.showProfileItems);
        viewPager.setAdapter(profilePagerAdapter);
        viewPager.setCurrentItem(event.getPos(), false);
        EventBus.getDefault().post(new LoadingDismissEvent());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void notify(PersonsAddedEvent event) {
        Log.d("PersonsAdded", MainActivity.showProfileItems.size() + "");
        toAdd = 0;
        profilePagerAdapter.notifyDataSetChanged();
        if (event.isNextPage()) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String strViews = Tools.getSettings("views_ok", getContext());

                    if (!strViews.isEmpty()) {
                        int intViews = Integer.parseInt(strViews) + 1;
                        Tools.saveSettings("views_ok", String.valueOf(intViews), getContext());
                        //  maxPage = intViews;
                    }

                }
            }).start();
        }
        EventBus.getDefault().post(new LoadingDismissEvent());
        pendingAdLoad = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changePage(ChangePersonEvent event) {
        viewPager.setCurrentItem(event.getPosition(), true);
    }

    public void checkLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isLocationGranted()) {
                EventBus.getDefault().post(new ActivateLocationListenerEvent());
            } else {
                getActivity().requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        10);
            }
        } else
            EventBus.getDefault().post(new ActivateLocationListenerEvent());

    }

    private void enableLocationSettings() {
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(500)
                .setFastestInterval(500)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        LocationServices
                .getSettingsClient(getActivity())
                .checkLocationSettings(builder.build())
                .addOnSuccessListener(getActivity(), (LocationSettingsResponse response) -> {
                })
                .addOnFailureListener(getActivity(), ex -> {
                    if (ex instanceof ResolvableApiException) {
                        // Location settings are NOT satisfied,  but this can be fixed  by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),  and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) ex;
                            resolvable.startResolutionForResult(getActivity(), MainActivity.REQUEST_CODE_LOCATION_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                    }
                });
    }

   /* public void activateLocationListener() {
        if ((ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            return;
        }
        Log.d("Location_", "op listener");

        enableLocationSettings();

        if (MainActivity.locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            MainActivity.locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 3000, 0, locationListenerNetwork);
        if (MainActivity.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            MainActivity.locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 3000, 0, locationListenerGps);

    }*/


    private boolean isLocationGranted() {
        return ((ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED));
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return LocationManagerCompat.isLocationEnabled(locationManager);
    }

    private void showViewPager() {
        scrollView.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);

    }

    private void showInfo() {
        viewPager.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
    }


    private void cancelSearch() {

        expressDate.setMinAndMaxFrame(0, 12);

        expressDate.setFrame(0);

        isPendindExpDate = false;

        Tools.saveSettings("pending_expDate", "", getContext());
    }

    private void updateToAdd(boolean beforeLoad) {

        if (getContext() == null)
            return;

        String strViews = Tools.getSettings("views_ok", getContext());

        String prevToAdd = Tools.getSettings("to_add_views", getContext());

        int intPrevs = prevToAdd.isEmpty() ? 0 : Integer.parseInt(prevToAdd);

        int intViews = strViews.isEmpty() ? 0 : Integer.parseInt(strViews);

        Log.d("nahsld124ada", "intViews " + intViews + " maxPage " + maxPage);
        toAdd = maxPage - intViews;

        toAdd += intPrevs;

        Log.d("nahsld124ada", "update " + toAdd);
        if (toAdd > 0 && !beforeLoad) {
            Tools.saveSettings("to_add_views", String.valueOf(toAdd), getContext());
            AppHandler.updateCantViews(getContext(), toAdd);
            toAdd = 0;
        }

        Tools.saveSettings("views_ok", String.valueOf(Math.max(intViews, maxPage)), getContext());

        Log.d("nahsld124ada", "pause " + Math.max(intViews, maxPage));
    }

    private void like() {
        AppHandler.executor.submit(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                Contact contact = gson.fromJson(gson.toJson(MainActivity.profileItems.get(viewPager.getCurrentItem())), Contact.class);
                contact.setType(Tools.getString(R.string.mylike, getActivity()));
                AppHandler.addLike(contact, getActivity(), true);
            }
        });
    }

    private void dislike(Contact contact) {

        AppHandler.executor.submit(new Runnable() {
            @Override
            public void run() {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new DislikeDialog().showDialog(getActivity(), contact.getChatUsername(getContext()));
                    }
                });

            }
        });
    }
}