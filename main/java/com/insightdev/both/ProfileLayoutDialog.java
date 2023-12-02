package com.insightdev.both;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.lifecycle.ViewModelProviders;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.genius.multiprogressbar.MultiProgressBar;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.insightdev.both.viewmodels.HomeViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;


public class ProfileLayoutDialog {

    Collection<String> ids = new ArrayList<>();
    private ImageRequest request;
    private BottomSheetDialog dialog;
    private ResizeOptions resizeOptions = new ResizeOptions(800, 1200);

    IceBreakerDialog iceBreakerDialog = new IceBreakerDialog();


    NewFeatureDialog newFeatureDialog = new NewFeatureDialog();


    public static native String adsAddress();

    static {
        System.loadLibrary("native-lib");
    }

    @SuppressLint("ClickableViewAccessibility")
    public void showDialog(Activity activity, Person profile, int from) {

        dialog = new BottomSheetDialog(activity);
        final int[] currentImage = {0};
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.profile_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        FrameLayout bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);

        MultiProgressBar stepProgressBar = dialog.findViewById(R.id.stepBar);
        ImageView back = dialog.findViewById(R.id.back);
        LottieAnimationView likeButton = dialog.findViewById(R.id.likeButton);
        LottieAnimationView iceBreaker = dialog.findViewById(R.id.icebreaker);
        LottieAnimationView likeAnim = dialog.findViewById(R.id.likeAnim);
        View leftButton = dialog.findViewById(R.id.leftButt);
        View rightButton = dialog.findViewById(R.id.rightButt);

        NestedScrollView scrollView = dialog.findViewById(R.id.parent);


        SimpleDraweeView draweeView = dialog.findViewById(R.id.mainImage);
        //draweeView.getLayoutParams().height = (int) (MainActivity.screenHeight * 0.9);

        TextView textName = dialog.findViewById(R.id.cardName);
        TextView textCity = dialog.findViewById(R.id.cardCity);
        ImageView iconPremium = dialog.findViewById(R.id.iconPremium);
        TextView sex = dialog.findViewById(R.id.sex);
        TextView occupationTop = dialog.findViewById(R.id.occupationTop);
        TextView textOccupation = dialog.findViewById(R.id.occupation);
        TextView aboutMeTop = dialog.findViewById(R.id.aboutMeTop);
        TextView textAboutMe = dialog.findViewById(R.id.aboutMe);
        TextView lookingForTop = dialog.findViewById(R.id.lookingForTop);
        TextView textLookingFor = dialog.findViewById(R.id.lookingFor);
        TextView highlightTop = dialog.findViewById(R.id.highlightTop);
        TextView textHighlight = dialog.findViewById(R.id.highlight);
        TextView dislikeTop = dialog.findViewById(R.id.dislikeTop);
        TextView textDislike = dialog.findViewById(R.id.dislike);
        TextView textInstagram = dialog.findViewById(R.id.instagram);
        ImageView instaLogo = dialog.findViewById(R.id.instaLogo);
        ChipGroup chipGroup = dialog.findViewById(R.id.tags);
        Chip chipGuide = dialog.findViewById(R.id.chipGuide);
        WebView adContainer = dialog.findViewById(R.id.banner_container);
        TextView report = dialog.findViewById(R.id.report);

        stepProgressBar.setProgressStepsCount(profile.getCantPublicPhotos());
        stepProgressBar.next();


        if (profile.getId().contentEquals(Profile.getId())) {
            likeButton.setVisibility(View.GONE);
            report.setVisibility(View.GONE);
            iceBreaker.setVisibility(View.GONE);
        }
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();

        //layoutParams.height = MainActivity.screenHeight;

        bottomSheet.setLayoutParams(layoutParams);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        final HomeViewModel homeViewModel = ViewModelProviders.of((FragmentActivity) activity).get(HomeViewModel.class);


        homeViewModel.getTrueDateLiveData().removeObservers((AppCompatActivity) activity);

        homeViewModel.getTrueDateLiveData().observe(ProcessLifecycleOwner.get(), new Observer<String>() {
            @Override
            public void onChanged(String s) {

                Log.d("ahoisdaod", "okok");

                if (!s.equals("error")) {

                    Contact contact = Tools.profileToContact(profile, "iceb");

                    if (!Objects.equals(Tools.getSettings("is_icebreaker", activity), "yes"))
                        newFeatureDialog.showDialog(activity, contact, 0, s);
                    else
                        iceBreakerDialog.showDialog(activity, contact, 0, s);
                } else
                    ProToast.makeText(activity, R.drawable.toast_offline, "Compruebe su conexión a internet", Toast.LENGTH_SHORT);

                homeViewModel.getTrueDateLiveData().removeObservers((AppCompatActivity) activity);

            }
        });


        final boolean[] isClicked = {false};
        final ArrayList<String>[] publicPhotos = new ArrayList[]{new ArrayList<>()};

        new Thread(new Runnable() {
            @Override
            public void run() {

                String name = profile.getName();
                if (name.length() > 13)
                    name = Tools.getFirstWords(name, 2);
                String city = profile.getCity();
                boolean premium = profile.isPremium(activity);
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
                String instagram = profile.getInstagram();


                String finalName = name;
                int finalGender = gender;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textName.setText(finalName);
                        if (day != -1)
                            textName.setText(finalName + ", " + age);
                        textCity.setText(city);
                        if (finalGender != -1)
                            sex.setText(activity.getResources().getStringArray(R.array.genders_list)[finalGender]);

                        if (premium) {
                            iconPremium.setVisibility(View.VISIBLE);
                        }


                        if (show_orientation) {
                            if (!orientation.isEmpty()) {
                                sex.setText(sex.getText().toString() + ". " + activity.getResources().getStringArray(R.array.sexs_list)[Integer.parseInt(orientation)]);

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
                            if (lookingFor[0].contains("Seria"))
                                lookingFor[0] = "Una relación seria " + "\uD83D\uDC8D";
                            else if (lookingFor[0].contains("Una aventura"))
                                lookingFor[0] = "Una aventura " + "\uD83C\uDFDE";
                            else if (lookingFor[0].contains("Lo que surja")) {
                                lookingFor[0] = "Lo que surja " + "\uD83C\uDF0C";
                            }
                            textLookingFor.setText(lookingFor[0]);
                        }

                        if (!highlight[0].isEmpty()) {
                            if (highlight[0].contains("Mi inteligencia")) {
                                highlight[0] = "Mi inteligencia " + "\uD83E\uDDE0";
                            } else if (highlight[0].contains("Mi físico")) {
                                highlight[0] = "Mi físico " + "\uD83D\uDCAA";
                            } else if (highlight[0].contains("Mi sentido del humor")) {
                                highlight[0] = "Mi sentido del humor " + "\uD83E\uDD23";
                            }
                            highlightTop.setVisibility(View.VISIBLE);
                            textHighlight.setVisibility(View.VISIBLE);
                            textHighlight.setText(highlight[0]);
                        }

                        if (!dislike[0].isEmpty()) {
                            if (dislike[0].contains("Cocinar")) {
                                dislike[0] = "Cocinar " + "\uD83C\uDF73";
                            } else if (dislike[0].contains("Organizar mi tiempo")) {
                                dislike[0] = "Organizar mi tiempo " + "⏰";
                            } else if (dislike[0].contains("Expresar mis sentimientos")) {
                                dislike[0] = "Expresar mis sentimientos " + "❤️";
                            }
                            dislikeTop.setVisibility(View.VISIBLE);
                            textDislike.setVisibility(View.VISIBLE);
                            textDislike.setText(dislike[0]);
                        }

                        if (!instagram.isEmpty()) {
                            instaLogo.setVisibility(View.VISIBLE);
                            textInstagram.setVisibility(View.VISIBLE);
                            textInstagram.setText("@" + instagram);
                        }

                        if (day != -1) {
                            chipGuide.setText(Tools.getZodiac(day, month, null));
                        } else {
                            chipGroup.removeView(chipGuide);
                        }
                        if (!preferences.isEmpty()) {
                            String[] tags = preferences.split("///");
                            for (String tag : tags) {
                                Chip chip = new Chip(activity);
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
                    String str = Tools.getValue(occupation, Tools.getString(R.string.occupation, activity));
                    if (str.contentEquals(Tools.getString(R.string.work, activity))) {
                        Log.d("Log_", "work");
                        String work_center = Tools.getValue(occupation, Tools.getString(R.string.work_center, activity));
                        String job = Tools.getValue(occupation, Tools.getString(R.string.job, activity));
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!work_center.isEmpty())
                                    textOccupation.setText("Trabajo en " + work_center);
                                else
                                    textOccupation.setText("Trabajo");

                                if (!job.isEmpty())
                                    textOccupation.setText(textOccupation.getText().toString() + " como " + job);

                                textOccupation.setText(textOccupation.getText().toString() + " " + "\uD83D\uDCBC");
                                occupationTop.setVisibility(View.VISIBLE);
                                textOccupation.setVisibility(View.VISIBLE);
                            }
                        });

                    } else {
                        String study_center = Tools.getValue(occupation, Tools.getString(R.string.study_center, activity));
                        String course = Tools.getValue(occupation, Tools.getString(R.string.course, activity));
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (!course.isEmpty())
                                    textOccupation.setText("Estudio" + " " + course);
                                else
                                    textOccupation.setText("Estudio");

                                if (!study_center.isEmpty())
                                    textOccupation.setText(textOccupation.getText().toString() + " en " + study_center);

                                textOccupation.setText(textOccupation.getText().toString() + " " + "📚");//"\uD83C\uDF93";
                                occupationTop.setVisibility(View.VISIBLE);
                                textOccupation.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }

                try {
                    publicPhotos[0] = profile.getPublicPhotos(activity);
                    loadPublicPhoto(publicPhotos[0], currentImage[0], draweeView);

                    activity.runOnUiThread(new Runnable() {
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

                } catch (Exception ignored) {
                }

                if ((!Profile.isPremium(activity) || Tools.isAdsPremiumAvailable(activity))
                        && Tools.isBannerAvailable(activity) && AppHandler.adsArray != null
                        && AppHandler.limitAds >= AppHandler.cantAds && Tools.isConnected(activity)) {
                    String[] adSize = {"small_banner.html", "medium_banner.html"};
                    int posUrlRand = Tools.generateRandom(0, AppHandler.adsArray.length - 1);
                    int posSizeRand = Tools.generateRandom(0, 1);
                    AppHandler.cantAds += 1;
                    Log.d("Ad_", "url" + AppHandler.adsArray[posUrlRand]);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adContainer.getSettings().setJavaScriptEnabled(true);
                            adContainer.loadUrl(adsAddress() + "/" + AppHandler.adsArray[posUrlRand] + "/" + adSize[posSizeRand]);
                        }
                    });

                }


                Contact contact = ContactsManager.getContact(profile.getChatUsername(activity), activity);
                if (contact == null || contact.getType() == null)
                    return;

                String type = contact.getType();
                if (type.contentEquals(Tools.getString(R.string.mylike, activity)) || type.contentEquals(Tools.getString(R.string.match, activity)))
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            likeButton.playAnimation();
                        }
                    });


            }
        }).start();


        likeButton.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                Log.d("kasdadad", "sssaa");
                if (isClicked[0])
                    AppHandler.executor.submit(new Runnable() {
                        @Override
                        public void run() {

                            Log.d("kasdadad", "sss");
                            Gson gson = new Gson();
                            Contact contact = gson.fromJson(gson.toJson(profile), Contact.class);
                            contact.setType(Tools.getString(R.string.mylike, activity));
                            AppHandler.addLike(contact, activity, true);
                        }
                    });
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (isClicked[0])
                    AppHandler.executor.submit(new Runnable() {
                        @Override
                        public void run() {
                            String username = profile.getChatUsername(activity);
                            if (ContactsManager.getContact(username, activity).getType().contentEquals("match"))
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new DislikeDialog().showDialog(activity, username);
                                    }
                                });
                            else
                                AppHandler.dislike(username, activity, true);
                        }
                    });
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        iceBreaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iceBreaker.playAnimation();

                homeViewModel.getTrueDate(activity);

            }
        });

        likeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isClicked[0] = true;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (likeButton.getFrame() == 0) {
                        likeButton.playAnimation();
                        likeAnim.playAnimation();
                    } else {
                        likeButton.cancelAnimation();
                        likeButton.setFrame(0);
                    }

                }
                return true;
            }
        });

        textInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String instagram = profile.getInstagram();
                if (!instagram.isEmpty()) {
                    EventBus.getDefault().post(new LoadingShowEvent());
                    Uri uri;
                    if (Tools.isAppInstalled(activity, "com.instagram.android"))
                        uri = Uri.parse("http://instagram.com/_u/" + instagram);
                    else
                        uri = Uri.parse("http://instagram.com/" + instagram);
                    activity.startActivityForResult(new Intent(Intent.ACTION_VIEW, uri), MainActivity.REQUEST_INSTAGRAM);
                }
            }
        });

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ReportDialog().showDialog(activity, Integer.parseInt(profile.getId()));
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Back_", "x");
                dismiss();
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dismiss();
            }
        });


        if (from == 0)

            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.scrollTo(0, draweeView.getBottom());
                }
            });

        dialog.show();


    }

    private void loadPublicPhoto(ArrayList<String> photos, int photoPos, SimpleDraweeView draweeView) {
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

    public void dismiss() {
        if (dialog != null)
            dialog.dismiss();
    }
}
