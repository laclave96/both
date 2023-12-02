package com.insightdev.both;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.PagerAdapter;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.genius.multiprogressbar.MultiProgressBar;
import com.google.ads.mediation.facebook.FacebookAdapter;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.nativead.AdChoicesView;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.insightdev.both.viewmodels.HomeViewModel;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ProfilePagerAdapter extends PagerAdapter {

    private Activity activity;
    private ImageRequest request;
    private ArrayList<Object> profileItems;
    private ResizeOptions resizeOptions = new ResizeOptions(800, 1200);
    private ArrayList<Integer> currentImages;


    public static native String adsAddress();

    static {
        System.loadLibrary("native-lib");
    }

    private OnItemCLickListener mListener;


    public interface OnItemCLickListener {
        void OnIceBreakerClick(int position);
    }


    public void setOnItemClickListener(OnItemCLickListener listener) {

        mListener = listener;
    }


    public ProfilePagerAdapter(Activity activity, ArrayList<Integer> currentImages, ArrayList<Object> profileItems) {
        this.activity = activity;
        this.profileItems = profileItems;
        this.currentImages = currentImages;
        if (currentImages.isEmpty()) {
            for (int i = 0; i < profileItems.size(); i++) {
                this.currentImages.add(0);
                MainActivity.currentImages.add(0);
            }
        }
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutScreen;

        Log.d("balskdnadkadasd", profileItems.get(position).getClass() + " pos " + position);
        if (profileItems.get(position) instanceof Person) {
            layoutScreen = inflater.inflate(R.layout.profile_layout, null);
            setupProfileLayout(container, position, layoutScreen);
        } else if (profileItems.get(position) instanceof ShowPost) {
            layoutScreen = inflater.inflate(R.layout.post_layout, null);
            setupPostLayout(container, position, layoutScreen);

        } else {
            layoutScreen = inflater.inflate(R.layout.google_ad_layout, null);
            Log.d("ajskdhaljkhakwfa", " arr siz " + MainActivity.showProfileItems.size());
            Log.d("ajskdhaljkhakwfa", " arr siz " + MainActivity.showProfileItems.get(position).getClass());

            displayUnifiedNativeAd(container, (NativeAd) profileItems.get(position), (NativeAdView) layoutScreen);

        }
        return layoutScreen;


    }


    @Override
    public int getCount() {
        return profileItems.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((View) object);

    }


    @SuppressLint("ClickableViewAccessibility")
    private void setupProfileLayout(ViewGroup container, int position, View layoutScreen) {


        View infoButton = layoutScreen.findViewById(R.id.view);
        // MotionLayout motionLayout = layoutScreen.findViewById(R.id.parent);
        TextView textName = layoutScreen.findViewById(R.id.cardName);
        TextView textCity = layoutScreen.findViewById(R.id.cardCity);
        ImageView iconPremium = layoutScreen.findViewById(R.id.iconPremium);


        ProfileDialogFragment profileDialogFragment = new ProfileDialogFragment();

        SimpleDraweeView draweeView = layoutScreen.findViewById(R.id.mainImage);
        MultiProgressBar stepProgressBar = layoutScreen.findViewById(R.id.stepBar);
        View leftButton = layoutScreen.findViewById(R.id.leftButt);
        View rightButton = layoutScreen.findViewById(R.id.rightButt);

        Person profile = ((Person) profileItems.get(position));
        stepProgressBar.setProgressStepsCount(profile.getCantPublicPhotos());
        stepProgressBar.next();

        boolean[] isClicked = {false};
        ArrayList<String>[] publicPhotos = new ArrayList[]{MainActivity.publicPhotos.get(position)};


        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = currentImages.get(position);
                if (pos > 0) {
                    pos--;
                    loadPublicPhoto(publicPhotos[0], pos, draweeView);
                    currentImages.set(position, pos);
                    MainActivity.currentImages.set(position, pos);
                    stepProgressBar.previous();
                } else {
                    int p = position - 1;
                    if (p < 0)
                        p = 0;
                    EventBus.getDefault().post(new ChangePersonEvent(p));
                }
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int size = profile.getCantPublicPhotos();

                int pos = currentImages.get(position);

                if (pos < size - 1) {
                    pos++;
                    loadPublicPhoto(publicPhotos[0], pos, draweeView);
                    currentImages.set(position, pos);
                    MainActivity.currentImages.set(position, pos);
                    stepProgressBar.next();
                } else {
                    EventBus.getDefault().post(new ChangePersonEvent(position + 1));
                }

            }
        });



        /*motionLayout.addTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int i, int i1) {

            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int i, int i1, float v) {


            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int i) {
                if (i == R.id.end) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if ((!Profile.isPremium(activity) || Tools.isAdsPremiumAvailable(activity))
                                    && Tools.isBannerAvailable(activity) && AppHandler.adsArray != null
                                    && AppHandler.limitAds >= AppHandler.cantAds && Tools.isConnected(activity)) {

                                String[] adSize = {"small_banner.html", "medium_banner.html"};
                                int posUrlRand = Tools.generateRandom(0, AppHandler.adsArray.length - 1);
                                int posSizeRand = Tools.generateRandom(0, 1);

                                Log.d("Ad_", "url" + AppHandler.adsArray[posUrlRand]);
                                WebView adContainer = layoutScreen.findViewById(R.id.banner_container);
                                AppHandler.cantAds += 1;
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adContainer.getSettings().setJavaScriptEnabled(true);
                                        adContainer.loadUrl(adsAddress() + "/" + AppHandler.adsArray[posUrlRand] + "/" + adSize[posSizeRand]);
                                    }
                                });

                            }
                        }
                    }).start();
                }
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int i, boolean b, float v) {

            }
        });*/

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new ProfileLayoutDialog().showDialog(activity, profile, 0);

                if (profileDialogFragment.isAdded())
                    return;
                Bundle bundle = new Bundle();
                bundle.putString("person", new Gson().toJson(profile));
                bundle.putInt("from", 1);
                profileDialogFragment.setArguments(bundle);

                profileDialogFragment.show(((AppCompatActivity) activity).getSupportFragmentManager(), "ProfileDialog");

            }
        });

        container.addView(layoutScreen);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (stepProgressBar.getCurrentStep() < currentImages.get(position) + 1)
                    stepProgressBar.next();
                String name = profile.getName();

                while (name.length() > 18)
                    name = Tools.getStringWithDotsAt(name, 18);

                String city = profile.getCity();

                while (city.length() > 18)
                    city = Tools.getStringWithDotsAt(city, 18);

                String country = profile.getCountry();

                boolean premium = profile.isPremium(activity);
                int age = profile.getAge();

                String finalName = name;
                String finalCity = city;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        textName.setText(finalName.lastIndexOf(".") == finalName.length() - 1 ? finalName + " " + age : finalName + ", " + age);
                        textCity.setText(finalCity.lastIndexOf(".") == finalCity.length() - 1 ? finalCity + " " + country : finalCity + ", " + country);

                        if (premium) {
                            iconPremium.setVisibility(View.VISIBLE);
                        }
                    }
                });


                Contact contact = ContactsManager.getContact(profile.getChatUsername(activity), activity);
                if (contact == null)
                    return;

                String type = contact.getType();
                if (type.contains(Tools.getString(R.string.mylike, activity)) || type.contains(Tools.getString(R.string.match, activity)))
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //likeButton.playAnimation();
                        }
                    });

            }
        }).start();

        if (currentImages.size() > position)
            loadPublicPhoto(publicPhotos[0], currentImages.get(position), draweeView);

    }

    private void setupPostLayout(ViewGroup container, int position, View layoutScreen) {

        ConstraintLayout layout = layoutScreen.findViewById(R.id.layout);
        TextView textName = layoutScreen.findViewById(R.id.name);
        TextView textComment = layoutScreen.findViewById(R.id.comment);
        SimpleDraweeView profile = layoutScreen.findViewById(R.id.profileImg);
        SimpleDraweeView postImage = layoutScreen.findViewById(R.id.mainImage);

        ProfileDialogFragment profileDialogFragment = new ProfileDialogFragment();
        ShowPost post = ((ShowPost) profileItems.get(position));
        Object obj = MainActivity.profileItems.get(position);
        textName.setText(post.getTitle());
        textComment.setText(post.getDescription());


        if (!post.getDescription().isEmpty())
            textComment.setVisibility(View.VISIBLE);
        else
            textComment.setVisibility(View.GONE);


        loadPhoto(postImage, post.getPrimaryUrl());
        loadPhoto(profile, post.getSecondaryUrl());

        container.addView(layoutScreen);


        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (profileDialogFragment.isAdded())
                    return;
                Bundle bundle = new Bundle();
                bundle.putString("person", new Gson().toJson(profile));
                profileDialogFragment.setArguments(bundle);

                profileDialogFragment.show(((AppCompatActivity) activity).getSupportFragmentManager(), "ProfileDialog");
            }
        });

    }

    private void displayUnifiedNativeAd(ViewGroup parent, NativeAd ad, NativeAdView adView) {


        // Locate the view that will hold the headline, set its text, and call the
        // UnifiedNativeAdView's setHeadlineView method to register it.
        TextView headlineView = adView.findViewById(R.id.native_ad_title);
        headlineView.setText(ad.getHeadline());
        adView.setHeadlineView(headlineView);

        TextView action = adView.findViewById(R.id.native_ad_call_to_action);
        action.setText(ad.getCallToAction());
        adView.setCallToActionView(action);

        AdChoicesView adChoicesView=adView.findViewById(R.id.ad_choices_container);
        adView.setAdChoicesView(adChoicesView);

        TextView body = adView.findViewById(R.id.native_ad_body);
        body.setText(ad.getBody());
        adView.setBodyView(headlineView);

        ImageView appIcon= adView.findViewById(R.id.native_ad_icon);
        appIcon.setImageDrawable(ad.getIcon().getDrawable());


        Log.d("balskdnadkadasd", "headline " + headlineView.getText().toString());
        Bundle extras = ad.getExtras();
        Log.d("balskdnadkadasd", "headlineextra " + extras.toString());
        if (extras.containsKey(FacebookAdapter.KEY_SOCIAL_CONTEXT_ASSET)) {

            String socialContext = extras.get(FacebookAdapter.KEY_SOCIAL_CONTEXT_ASSET).toString();
            TextView sContext = adView.findViewById(R.id.native_ad_social_context);
            sContext.setText(socialContext);
        }





        MediaView mediaView = adView.findViewById(R.id.native_ad_media);
        mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
        adView.setMediaView(mediaView);




        // Call the UnifiedNativeAdView's setNativeAd method to register the
        // NativeAdObject.
        adView.setNativeAd(ad);

        // Ensure that the parent view doesn't already contain an ad view.

        // Place the AdView into the parent.
        parent.addView(adView);
    }

    private void setupAdNativeLayout(ViewGroup container, int position, View layoutScreen) {

    }

    private void loadPublicPhoto(ArrayList<String> photos, int photoPos, SimpleDraweeView draweeView) {
        if (photoPos < photos.size()) {
            String url = photos.get(photoPos);
            loadPhoto(draweeView, url);
        }
    }

    private void loadPhoto(SimpleDraweeView draweeView, String url) {
        request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .disableMemoryCache()
                .setResizeOptions(resizeOptions)
                .setProgressiveRenderingEnabled(true)
                .build();

        draweeView.setImageRequest(request);
/*

        if (imageView != null) {

            ImagePipeline imagePipeline = Fresco.getImagePipeline();
            DataSource<CloseableReference<CloseableImage>> dataSource =
                    imagePipeline.fetchDecodedImage(request, activity);

            dataSource.subscribe(new BaseBitmapDataSubscriber() {

                @Override
                public void onNewResultImpl(@Nullable Bitmap bitmap) {
                    // You can use the bitmap in only limited ways
                    // No need to do any cleanup.


                    Log.d("aslsdk", "===");
                    imageView.setImageBitmap(PaletteUtils.getDominantGradient(bitmap, bitmap.getHeight(), bitmap.getWidth()));
                }

                @Override
                public void onFailureImpl(DataSource dataSource) {
                    // No cleanup required here.
                }

            }, CallerThreadExecutor.getInstance());

        }*/
    }

    @Override
    public int getItemPosition(@NonNull @NotNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public void notifyDataSetChanged() {
        while (currentImages.size() < profileItems.size()) {
            currentImages.add(0);
            MainActivity.currentImages.add(0);
        }
        super.notifyDataSetChanged();

    }


}
