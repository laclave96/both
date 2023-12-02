package com.insightdev.both;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Network;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.RenderMode;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.insightdev.both.viewmodels.GetPricesViewModel;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PremiumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PremiumFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Button meInteresa, freeTrial;
    ScrollView scrollView;
    ConstraintLayout premiumLayout, infoLayout;
    CircularImageView drawable;
    TextView info, details, likesTitle;
    ImageView timeButt;
    ArrayList<PremiumLikeItem> premiumLikeItems = new ArrayList<>();
    Button action;
    RelativeLayout addButton;
    DotsIndicator dotsIndicator;
    PremiumLikesAdapter premiumLikesAdapter;

    TakeImageDialog takeImageDialog;
    int lastPos = 0;
    RecyclerView recyclerViewLikes;
    ViewPager viewPager;
    ArrayList<DescriptionModel> listDescription = new ArrayList<>();
    DescriptionAdapter descriptionAdapter;
    FreeTrialDialog freeTrialDialog;
    PricesDialog pricesDialog;
    NetworkReceiver receiver;

    int actualGender = 2;

    int actualTime = 1;
    // private ProfileDialogFragment profileDialogFragment;

    HottestFilterDialog.ActionInterface actionInterface;

    boolean isLost = false;


    static {
        System.loadLibrary("native-lib");
    }

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PremiumFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PremiunFragment.
     */
    // TODO: Rename and change types and number of parameters
    public PremiumFragment newInstance(String param1, String param2) {
        PremiumFragment fragment = new PremiumFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_premiun, container, false);

        recyclerViewLikes = view.findViewById(R.id.likesRecyclerView);
        likesTitle = view.findViewById(R.id.likesTitle);

        //likesTitle.setText(" Top " + Tools.getCantTopLikes(getContext()));
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerViewLikes.setLayoutManager(staggeredGridLayoutManager);

        freeTrialDialog = new FreeTrialDialog();
        takeImageDialog = new TakeImageDialog();
        pricesDialog = new PricesDialog();
        dotsIndicator = view.findViewById(R.id.dots_indicator);
        addButton = view.findViewById(R.id.addPost);
        descriptionAdapter = new DescriptionAdapter(getContext(), listDescription);

        scrollView = view.findViewById(R.id.scrollView);
        freeTrial = view.findViewById(R.id.freeTrial);
        timeButt = view.findViewById(R.id.since);
        premiumLayout = view.findViewById(R.id.premiumLayout);
        infoLayout = view.findViewById(R.id.infoLayout);
        drawable = view.findViewById(R.id.drawable);
        info = view.findViewById(R.id.comment);
        details = view.findViewById(R.id.details);
        action = view.findViewById(R.id.actionButton);
        meInteresa = view.findViewById(R.id.meInteresa);

        actionInterface = new HottestFilterDialog.ActionInterface() {
            @Override
            public void selection(int gender, int time) {

                Log.d("jñalsdkaf", "gender " + actualGender + " time " + actualTime);
                Log.d("jñalsdkaf", "hhgender " + gender + " time " + time);

                if ((actualTime == time) && (actualGender == gender))
                    return;
                actualGender = gender;


                switch (gender) {
                    case 2:
                        fillPremiumItems(MainActivity.topFull);
                        break;
                    case 1:
                        fillPremiumItems(MainActivity.topMan);
                        break;
                    case 0:
                        fillPremiumItems(MainActivity.topWoman);
                        break;

                }
                scrollView.setVisibility(View.GONE);
                infoLayout.setVisibility(View.GONE);
                if (time != actualTime) {
                    actualTime = time;
                    EventBus.getDefault().post(new LoadingShowEvent());
                    AppHandler.loadTopLikes(getContext(), actualTime);
                    return;
                }
                Log.d("jñalsdkaf", "lolgender " + actualGender + " time " + actualTime);
                premiumLikesAdapter.notifyDataSetChanged();
            }
        };


        listDescription.add(new DescriptionModel(R.drawable.prem_3, R.drawable.circle_back_white, R.drawable.prem_3_part, "Busca en tus regiones favoritas"));
        listDescription.add(new DescriptionModel(R.drawable.prem_5, R.drawable.circle_back_white, R.drawable.prem_5_part, "Explora los más codiciados"));
        listDescription.add(new DescriptionModel(R.drawable.prem_0, R.drawable.circle_back_white, R.drawable.prem_0_part, "Desliza sin límites"));
        listDescription.add(new DescriptionModel(R.drawable.prem_2, R.drawable.circle_back_white, R.drawable.prem_2_part, "10 Citas Express más al día"));
        listDescription.add(new DescriptionModel(R.drawable.prem_4, R.drawable.circle_back_white, R.drawable.prem_4_part, "Sin anuncios"));
        listDescription.add(new DescriptionModel(R.drawable.prem_1, R.drawable.circle_back_white, R.drawable.prem_1_part, "5 Icebreakers más al día"));


        viewPager = view.findViewById(R.id.descrip);
        viewPager.setAdapter(descriptionAdapter);
        //profileDialogFragment = new ProfileDialogFragment();
        viewPager.setPageTransformer(true, new SlowTransformation());

        dotsIndicator.setViewPager(viewPager);
        View decorView = getActivity().getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();

        premiumLikesAdapter = new PremiumLikesAdapter(getContext(), premiumLikeItems);
        recyclerViewLikes.setAdapter(premiumLikesAdapter);

        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(getActivity()));

        premiumLikesAdapter.setOnItemClickListener(new PremiumLikesAdapter.OnItemCLickListener() {
            @Override
            public void OnItemClick(int position) {

                switch (actualGender) {
                    case 2:
                        openPersonDialog(MainActivity.topFull.get(position));
                        break;
                    case 1:
                        openPersonDialog(MainActivity.topMan.get(position));
                        break;
                    case 0:
                        openPersonDialog(MainActivity.topWoman.get(position));
                        break;
                }
            }
        });


        meInteresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                

                if (Profile.getCountryCode() == null || Profile.getCountryCode().isEmpty()) {
                    if ((ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                            (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

                        new LocationDialog().showDialog(getActivity());
                    } else
                        ProToast.makeText(getContext(), R.drawable.ic_location, "Localizando...", Toast.LENGTH_SHORT);
                    return;
                }

                PricesDialogFragment profileDialogFragment = new PricesDialogFragment();

                profileDialogFragment.show((getActivity()).getSupportFragmentManager(), "PricesDialog");
            }
        });

        premiumLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastPos != 4)
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MainActivity.hasPermissions(getContext(), MainActivity.PERMISSIONS)) {
                    ActivityCompat.requestPermissions(getActivity(), MainActivity.PERMISSIONS, MainActivity.PERMISSION_ALL);
                    return;
                }
                if (!Profile.isSetupComplete()) {
                    Toast.makeText(getContext(), "Complete su perfil", Toast.LENGTH_SHORT).show();
                    return;
                }
                takeImageDialog.showDialog(getActivity(), 0);
            }
        });

        timeButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HottestFilterDialog().showDialog(getActivity(), actionInterface, actualGender, actualTime);

            }
        });


       /* editPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (!MainActivity.hasPermissions(getContext(), MainActivity.PERMISSIONS)) {
                    ActivityCompat.requestPermissions(getActivity(), MainActivity.PERMISSIONS, MainActivity.PERMISSION_ALL);
                    return;
                }
                if (!Profile.isSetupComplete()) {
                    Toast.makeText(getContext(), "Complete su perfil", Toast.LENGTH_SHORT).show();
                    return;
                }
                EventBus.getDefault().post(new LoadingShowEvent());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String myAddress = Tools.getAddress(Profile.getServer(), getContext());
                        String url = Tools.getString(R.string.http, getContext()) + myAddress + "/" + pathPhoto() + "/" +
                                Profile.getId() + "/" + Tools.getString(R.string.post, getContext()) + Tools.getString(R.string.jpeg, getContext());
                        CRequest request = new CRequest(getContext(), getComment(),
                                20);
                        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
                        pairs.add(new Pair<>(Tools.getString(R.string.user_id, getContext()), Profile.getId()));
                        pairs.add(new Pair<>(Tools.getString(R.string.auth, getContext()), pass()));
                        request.makeRequest(pairs);
                        request.setOnResponseListener(new CRequest.OnResponseListener() {

                            @Override
                            public void onResponse(String response) {

                                String comment = response.trim();

                                Log.d("kajjsdka", comment);

                                Glide.with(getContext())
                                        .asBitmap()
                                        .load(url)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
                                        .into(new SimpleTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                                                String file = Tools.getString(R.string.post, getContext()) + Tools.getString(R.string.jpeg, getContext());

                                                String path = getActivity().getFilesDir().getAbsolutePath() + "/" + file;

                                                Tools.write(file, resource, getContext());

                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        new EditPostDialog().showDialog(getActivity(), path, comment);
                                                        EventBus.getDefault().post(new LoadingDismissEvent());
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable Drawable errorDrawable) {
                                                super.onLoadFailed(errorDrawable);
                                                EventBus.getDefault().post(new LoadingDismissEvent());
                                                Toast.makeText(getContext(), "No tiene ninguna publicación", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }


                            @Override
                            public void onError(String error) {
                                EventBus.getDefault().post(new LoadingDismissEvent());
                                Toast.makeText(getContext(), "No se pudo cargar su post", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();


            }
        });*/


        if (!Tools.isFreePremiumAvailable(getContext()) || Profile.isFreePremiumConsumed(getContext()))
            freeTrial.setVisibility(View.INVISIBLE);

        freeTrial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                freeTrialDialog.showDialog(getActivity());
            }
        });

        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = action.getText().toString();
                if (text.contentEquals("Conectar")) {
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                } else {
                    EventBus.getDefault().post(new LoadingShowEvent());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            AppHandler.loadTopLikes(getContext(), actualTime);
                        }
                    }).start();
                }

            }
        });

        receiver = new NetworkReceiver(getContext());
        receiver.setOnConnectivityListener(new NetworkReceiver.OnConnectivityListener() {
            @Override
            public void Available(Network network) {
                if (premiumLikeItems.isEmpty() && isLost && Profile.isPremium(getContext())) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            AppHandler.loadTopLikes(getContext(), actualTime);
                        }
                    }).start();
                }
            }

            @Override
            public void Lost(Network network) {
                if (premiumLikeItems.isEmpty() && Profile.isPremium(getContext())) {
                    isLost = true;
                    EventBus.getDefault().post(new TopLikesEvent("disconnect"));
                }
            }
        });

        if (!Tools.isConnected(getContext()))
            isLost = true;

        receiver.register();

        EventBus.getDefault().register(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        receiver.unregister();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void checkPremium(UpdatePremiumEvent event) {
        Log.d("alnskdmañda", "chec");
        if (Profile.isPremium(getContext())) {
            Log.d("alnskdmañda", "go prem");
            EventBus.getDefault().post(new ChangeContactsEvent());
            premiumLayout.setVisibility(View.GONE);
            if (premiumLikeItems.isEmpty()) {
                EventBus.getDefault().post(new LoadingShowEvent());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AppHandler.loadTopLikes(getContext(), actualTime);
                    }
                }).start();
            }
        } else {
            scrollView.setVisibility(View.VISIBLE);
            premiumLayout.setVisibility(View.VISIBLE);
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void topLikes(TopLikesEvent event) {
        if (event.getAction().contentEquals("disconnect")) {
            scrollView.setVisibility(View.VISIBLE);
            premiumLayout.setVisibility(View.GONE);
            infoLayout.setVisibility(View.VISIBLE);
            drawable.setImageResource(R.drawable.offline);
            info.setText("Ups, no hay conexión a internet...");
            details.setText("Necesitas conectarte a una red para continuar");
            action.setText("Conectar");
            EventBus.getDefault().post(new LoadingDismissEvent());
        } else if (event.getAction().contentEquals("timeOut")) {
            scrollView.setVisibility(View.VISIBLE);
            premiumLayout.setVisibility(View.GONE);
            infoLayout.setVisibility(View.VISIBLE);
            drawable.setImageResource(R.drawable.clock);
            info.setText("Ups, demoramos demasiado en responder...");
            details.setText("Intente nuevamente conectar con nosotros");
            action.setText("Reintentar");
            EventBus.getDefault().post(new LoadingDismissEvent());
        } else {
            AppHandler.executor.submit(new Runnable() {
                @Override
                public void run() {
                    switch (actualGender) {
                        case 2:
                            fillPremiumItems(MainActivity.topFull);
                            break;
                        case 1:
                            fillPremiumItems(MainActivity.topMan);
                            break;
                        case 0:
                            fillPremiumItems(MainActivity.topWoman);
                            break;
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.setVisibility(View.GONE);
                            infoLayout.setVisibility(View.GONE);
                            premiumLikesAdapter.notifyDataSetChanged();
                            EventBus.getDefault().post(new LoadingDismissEvent());
                        }
                    });

                }
            });

        }


    }

    private void openPersonDialog(Person contact) {

        ProfileDialogFragment profileDialogFragment = new ProfileDialogFragment();
        if (profileDialogFragment.isAdded())
            return;
        Bundle bundle = new Bundle();
        bundle.putString("person", new Gson().toJson(contact));
        profileDialogFragment.setArguments(bundle);

        profileDialogFragment.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "ProfileDialog");
    }

    private void fillPremiumItems(ArrayList<HottestPerson> people) {
        premiumLikeItems.clear();
        for (HottestPerson person : people)
            premiumLikeItems.add(new PremiumLikeItem(Uri.parse(person.getProfilePhotoMedium(getContext())), Tools.getFirstWords(person.getName(), 1), Tools.getStringFromInt(person.getLikes_today())));


    }

}