package com.insightdev.both;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.core.widget.TextViewCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.genius.multiprogressbar.MultiProgressBar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec;
import com.google.gson.Gson;
import com.insightdev.both.viewmodels.HomeViewModel;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class ProfileDialogFragment extends BottomSheetDialogFragment {

    private ImageRequest request;

    private ResizeOptions resizeOptions = new ResizeOptions(850, 1300);

    NestedScrollView scrollView;

    SimpleDraweeView draweeView;

    IceBreakerDialog iceBreakerDialog = new IceBreakerDialog();

    Person profile;
    int from = 10;

    CircularProgressIndicator iceBreakerIndicator;
    NewFeatureDialog newFeatureDialog = new NewFeatureDialog();

    TextView expDateLabel;

    public static native String adsAddress();

    static {
        System.loadLibrary("native-lib");
    }


    public ProfileDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View dialog = inflater.inflate(R.layout.profile_dialog, container, false);

        profile = new Gson().fromJson(getArguments().getString("person"), Person.class);

        from = getArguments().getInt("from");

        final int[] currentImage = {0};
        MultiProgressBar stepProgressBar = dialog.findViewById(R.id.stepBar);
        ImageView back = dialog.findViewById(R.id.back);
        LottieAnimationView likeButton = dialog.findViewById(R.id.likeButton);
        LottieAnimationView iceBreaker = dialog.findViewById(R.id.icebreaker);
        LottieAnimationView likeAnim = dialog.findViewById(R.id.likeAnim);
        View leftButton = dialog.findViewById(R.id.leftButt);
        View rightButton = dialog.findViewById(R.id.rightButt);


        scrollView = dialog.findViewById(R.id.parent);

        draweeView = dialog.findViewById(R.id.mainImage);
        //draweeView.getLayoutParams().height = (int) (MainActivity.screenHeight * 0.9);

        TextView textName = dialog.findViewById(R.id.cardName);
        TextView textCity = dialog.findViewById(R.id.cardCity);
        expDateLabel = dialog.findViewById(R.id.expDateLabel);
        ImageView iconPremium = dialog.findViewById(R.id.iconPremium);
        TextView sex = dialog.findViewById(R.id.sex);
        TextView occupationTop = dialog.findViewById(R.id.occupationTop);
        TextView textOccupation = dialog.findViewById(R.id.occupation);
        TextView aboutMeTop = dialog.findViewById(R.id.aboutMeTop);
        iceBreakerIndicator = dialog.findViewById(R.id.iceBIndicator);
        iceBreakerIndicator.hide();
        TextView textAboutMe = dialog.findViewById(R.id.aboutMe);
        TextView lookingForTop = dialog.findViewById(R.id.lookingForTop);
        TextView textLookingFor = dialog.findViewById(R.id.lookingFor);
        TextView highlightTop = dialog.findViewById(R.id.highlightTop);
        TextView textHighlight = dialog.findViewById(R.id.highlight);
        TextView dislikeTop = dialog.findViewById(R.id.dislikeTop);
        TextView textDislike = dialog.findViewById(R.id.dislike);
        TextView travelTop = dialog.findViewById(R.id.travelTop);
        TextView textTravel = dialog.findViewById(R.id.travel);
        TextView textInstagram = dialog.findViewById(R.id.instagram);
        ImageView instaLogo = dialog.findViewById(R.id.instaLogo);
        ChipGroup chipGroup = dialog.findViewById(R.id.tags);
        Chip chipGuide = dialog.findViewById(R.id.chipGuide);
        WebView adContainer = dialog.findViewById(R.id.banner_container);
        TextView report = dialog.findViewById(R.id.report);

        final HomeViewModel homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);

        if (from == 1)
            /*scrollView.post(new Runnable() {
                @Override
                public void run() {*/
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.scrollTo(0, draweeView.getBottom());
                }
            });


        homeViewModel.getTrueDateLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                iceBreakerIndicator.hide();

                if (!s.equals("error")) {

                    Contact contact = Tools.profileToContact(profile, "iceb");

                    if (!Objects.equals(Tools.getSettings("is_icebreaker", getActivity()), "yes"))
                        newFeatureDialog.showDialog(getActivity(), contact, 0, s);
                    else
                        iceBreakerDialog.showDialog(getActivity(), contact, 0, s);
                } else
                    ProToast.makeText(getActivity(), R.drawable.toast_offline, getString(R.string.revise_conexion), Toast.LENGTH_SHORT);


            }
        });


        final boolean[] isClicked = {false};
        final ArrayList<String>[] publicPhotos = new ArrayList[]{new ArrayList<>()};

        new Thread(new Runnable() {
            @Override
            public void run() {

                stepProgressBar.setProgressStepsCount(profile.getCantPublicPhotos());
                stepProgressBar.next();


                if (profile.getId().contentEquals(Profile.getId())) {
                    likeButton.setVisibility(View.GONE);
                    report.setVisibility(View.GONE);
                    iceBreaker.setVisibility(View.GONE);
                }


                String name = profile.getName();

                while (name.length() > 18)
                    name = Tools.getStringWithDotsAt(name, 18);

                String city = profile.getCity();

                while (city.length() > 18)
                    city = Tools.getStringWithDotsAt(city, 18);

                //String city = Tools.getStringWithDotsAt("Santiago de Compostela", 18);

                String country = profile.getCountry();

                //String country = Tools.getStringWithDotsAt("Estados Unidos de América", 12);
                boolean premium = profile.isPremium(getActivity());
                int month = profile.getMonth();
                int day = profile.getDay();
                int age = profile.getAge();
                int gender = -1;
                try {
                    gender = Integer.parseInt(profile.getGender());
                } catch (Exception ignored) {
                }
                boolean show_orientation = profile.isShowOrientation();
                String orientation = profile.getOrientation();
                String occupation = profile.getOccupation();
                String preferences = profile.getPreferences();
                String aboutMe = profile.getAboutMe();
                final String[] lookingFor = {profile.getLookingFor()};
                final String[] highlight = {profile.getHighlight()};
                final String[] dislike = {profile.getDislike()};
                final String[] travel = {profile.getTravel()};
                String instagram = profile.getInstagram();


                String finalName = name;
                int finalGender = gender;
                String finalCity = city;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        textName.setText(finalName.lastIndexOf(".") == finalName.length() - 1 ? finalName + " " + age : finalName + ", " + age);
                        textCity.setText(finalCity.lastIndexOf(".") == finalCity.length() - 1 ? finalCity + " " + country : finalCity + ", " + country);


                        if (finalGender != -1)
                            sex.setText(getResources().getStringArray(R.array.genders_list)[finalGender]);

                        if (premium) {
                            iconPremium.setVisibility(View.VISIBLE);
                        }


                        if (show_orientation) {
                            if (!orientation.isEmpty()) {
                                sex.setText(sex.getText().toString() + ". " + getResources().getStringArray(R.array.sexs_list)[Integer.parseInt(orientation)]);

                            }
                        }
                        sex.setText(sex.getText().toString());

                        if (!aboutMe.isEmpty()) {
                            aboutMeTop.setVisibility(View.VISIBLE);
                            textAboutMe.setVisibility(View.VISIBLE);
                            textAboutMe.setText(aboutMe);
                        }

                        if (!lookingFor[0].isEmpty()) {

                            lookingForTop.setVisibility(View.VISIBLE);
                            textLookingFor.setVisibility(View.VISIBLE);

                            String[] answers = getResources().getStringArray(R.array.answers_question_1);

                            try {


                                int pos = Integer.valueOf(lookingFor[0]);

                                lookingFor[0] = answers[pos - 1];

                                if (pos == 1)
                                    lookingFor[0] += " " + "\uD83D\uDCAC";
                                else if (pos == 2)
                                    lookingFor[0] += " " + "\uD83D\uDC95";
                                else if (pos == 3) {
                                    lookingFor[0] += " " + "\uD83D\uDE43";
                                }

                            } catch (Exception ignored) {

                            }

                            textLookingFor.setText(lookingFor[0]);
                        }

                        if (!highlight[0].isEmpty()) {

                            String[] answers = getResources().getStringArray(R.array.answers_question_2);
                            try {
                                int pos = Integer.valueOf(highlight[0]);

                                highlight[0] = answers[pos - 1];


                                if (pos == 1) {
                                    highlight[0] += " " + "\uD83D\uDCAA";
                                } else if (pos == 2) {
                                    highlight[0] += " " + "\uD83E\uDD23";
                                } else if (pos == 3) {
                                    highlight[0] += " " + "\uD83E\uDDD8";
                                }


                            } catch (Exception ignored) {
                                Log.d("mañsldañsd", ignored.getMessage());
                            }
                            highlightTop.setVisibility(View.VISIBLE);
                            textHighlight.setVisibility(View.VISIBLE);
                            textHighlight.setText(highlight[0]);
                        }

                        if (!dislike[0].isEmpty()) {

                            String[] answers = getResources().getStringArray(R.array.answers_question_3);

                            try {
                                int pos = Integer.valueOf(dislike[0]);

                                dislike[0] = answers[pos - 1];

                                if (pos == 1) {
                                    dislike[0] += " " + "\uD83C\uDF73";
                                } else if (pos == 2) {
                                    dislike[0] += " " + "⏰";
                                } else if (pos == 3) {
                                    dislike[0] += " " + "❤️";
                                }

                            } catch (Exception ignored) {

                            }
                            dislikeTop.setVisibility(View.VISIBLE);
                            textDislike.setVisibility(View.VISIBLE);
                            textDislike.setText(dislike[0]);
                        }

                        if (!travel[0].isEmpty()) {

                            String[] answers = getResources().getStringArray(R.array.answers_question_4);
                            try {
                                int pos = Integer.valueOf(travel[0]);

                                travel[0] = answers[pos - 1];

                                if (pos == 1) {
                                    travel[0] += " " + "\uD83E\uDD50";
                                } else if (pos == 2) {
                                    travel[0] += " " + "\uD83C\uDFD4";
                                } else if (pos == 3) {
                                    travel[0] += " " + "\uD83C\uDFC4";
                                }

                            } catch (Exception ignored) {

                            }
                            travelTop.setVisibility(View.VISIBLE);
                            textTravel.setVisibility(View.VISIBLE);
                            textTravel.setText(travel[0]);
                        }

                        if (!instagram.isEmpty()) {
                            instaLogo.setVisibility(View.VISIBLE);
                            textInstagram.setVisibility(View.VISIBLE);
                            textInstagram.setText("@" + instagram);
                        }

                        if (day != -1) {
                            chipGuide.setText(Tools.getZodiac(day, month, getContext()));
                        } else {
                            chipGroup.removeView(chipGuide);
                        }
                        if (!preferences.isEmpty()) {
                            String[] tags = preferences.split("///");
                            for (String tag : tags) {
                                Chip chip = new Chip(getActivity());
                                chip.setText(tag);
                                chip.setChipBackgroundColorResource(R.color.white);
                                chip.setCloseIconVisible(false);
                                chip.setChipBackgroundColorResource(R.color.dBackground);
                                chip.setTextColor(Color.BLACK);
                                TextViewCompat.setTextAppearance(chip, R.style.chipFont);
                                chipGroup.addView(chip);
                            }


                        }
                    }
                });

                if (!occupation.isEmpty()) {
                    String str = Tools.getValue(occupation, Tools.getString(R.string.occupation, getActivity()));
                    if (str.contentEquals(Tools.getString(R.string.work, getActivity()))) {
                        Log.d("Log_", "work");
                        String work_center = Tools.getValue(occupation, Tools.getString(R.string.work_center, getActivity()));
                        String job = Tools.getValue(occupation, Tools.getString(R.string.job, getActivity()));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!work_center.isEmpty())
                                    textOccupation.setText(getString(R.string.trabajo_en_) + work_center);
                                else
                                    textOccupation.setText(getString(R.string.trabajo));

                                if (!job.isEmpty())
                                    textOccupation.setText(textOccupation.getText().toString() + getString(R.string._como_) + job);

                                textOccupation.setText(textOccupation.getText().toString() + " " + "\uD83D\uDCBC");
                                occupationTop.setVisibility(View.VISIBLE);
                                textOccupation.setVisibility(View.VISIBLE);
                            }
                        });

                    } else {
                        String study_center = Tools.getValue(occupation, Tools.getString(R.string.study_center, getActivity()));
                        String course = Tools.getValue(occupation, Tools.getString(R.string.course, getActivity()));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (!course.isEmpty())
                                    textOccupation.setText(getString(R.string.estudio_) + course);
                                else
                                    textOccupation.setText(getString(R.string.estudio));

                                if (!study_center.isEmpty())
                                    textOccupation.setText(textOccupation.getText().toString() + getString(R.string._en_) + study_center);

                                textOccupation.setText(textOccupation.getText().toString() + " " + "📚");//"\uD83C\uDF93";
                                occupationTop.setVisibility(View.VISIBLE);
                                textOccupation.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }

                try {
                    publicPhotos[0] = profile.getPublicPhotos(getActivity());
                    loadPublicPhoto(publicPhotos[0], currentImage[0], draweeView);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            leftButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int pos = currentImage[0];
                                    if (pos > 0) {
                                        pos--;
                                        loadPublicPhoto(publicPhotos[0], pos, draweeView);
                                        currentImage[0] = pos;
                                        stepProgressBar.previous();
                                    }
                                }
                            });

                            rightButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    int size = profile.getCantPublicPhotos();
                                    int pos = currentImage[0];

                                    if (pos < size - 1) {
                                        pos++;
                                        loadPublicPhoto(publicPhotos[0], pos, draweeView);
                                        currentImage[0] = pos;
                                        stepProgressBar.next();
                                    }

                                }
                            });
                        }
                    });

                } catch (
                        Exception ignored) {
                }

                if ((!Profile.isPremium(

                        getActivity()) || Tools.isAdsPremiumAvailable(

                        getActivity()))
                        && Tools.isBannerAvailable(

                        getActivity()) && AppHandler.adsArray != null
                        && AppHandler.limitAds >= AppHandler.cantAds && Tools.isConnected(

                        getActivity())) {
                    String[] adSize = {"small_banner.html", "medium_banner.html"};
                    int posUrlRand = Tools.generateRandom(0, AppHandler.adsArray.length - 1);
                    int posSizeRand = Tools.generateRandom(0, 1);
                    AppHandler.cantAds += 1;
                    Log.d("Ad_", "url" + AppHandler.adsArray[posUrlRand]);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adContainer.getSettings().setJavaScriptEnabled(true);
                            adContainer.loadUrl(adsAddress() + "/" + AppHandler.adsArray[posUrlRand] + "/" + adSize[posSizeRand]);
                        }
                    });

                }


                Contact contact = ContactsManager.getContact(profile.getChatUsername(getActivity()), getActivity());
                if (contact == null || contact.getType() == null)
                    return;

                String type = contact.getType();


                if (type.contains("expDate"))
                    expDateLabel.setVisibility(View.VISIBLE);


            }
        }).

                start();


        iceBreaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iceBreakerIndicator.show();
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

        textInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String instagram = profile.getInstagram();
                if (!instagram.isEmpty()) {
                    Uri uri;
                    if (Tools.isAppInstalled(getActivity(), "com.instagram.android"))
                        uri = Uri.parse("http://instagram.com/_u/" + instagram);
                    else
                        uri = Uri.parse("http://instagram.com/" + instagram);
                    getActivity().startActivityForResult(new Intent(Intent.ACTION_VIEW, uri), MainActivity.REQUEST_INSTAGRAM);
                }
            }
        });

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ReportDialog().showDialog(getActivity(), Integer.parseInt(profile.getId()));
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Back_", "x");
                dismiss();
            }
        });


        return dialog;
    }


    @Override
    public void onPause() {
        dismiss();
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
/*
        if (from == 1)
            scrollView.post(new Runnable() {
                @Override
                public void run() {
            scrollView.scrollTo(0, draweeView.getBottom());
                }
            });*/

    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle
                                         savedInstanceState) {

        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;

                FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        // Do something with your dialog like setContentView() or whatever
        return dialog;
    }

    @Override
    public void onCancel(@NonNull @NotNull DialogInterface dialog) {


        dismiss();
        super.onCancel(dialog);
    }

    private void loadPublicPhoto(ArrayList<String> photos, int photoPos, SimpleDraweeView
            draweeView) {
        String url = photos.get(photoPos);
        loadPhoto(draweeView, url);
    }

    private void loadPhoto(SimpleDraweeView draweeView, String url) {

        request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setResizeOptions(resizeOptions)
                .disableMemoryCache()
                .setProgressiveRenderingEnabled(true)
                .build();

        draweeView.setImageRequest(request);

    }

    private void like() {
        AppHandler.executor.submit(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                Contact contact = gson.fromJson(gson.toJson(profile), Contact.class);
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
