package com.insightdev.both;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.greenrobot.eventbus.EventBus;

public class FreeTrialDialog {

    public void showDialog(Activity activity) {
        ShareDialog shareDialog;
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.free_trial_dialog);
        TextView facebook = dialog.findViewById(R.id.facebook);

        shareDialog = new ShareDialog(activity);

        MainActivity.callbackManager = CallbackManager.Factory.create();

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Tools.isConnected(activity)) {
                    Toast.makeText(activity, "Sin Conexión", Toast.LENGTH_SHORT).show();
                    return;
                }
                EventBus.getDefault().post(new LoadingShowEvent());

                shareDialog.registerCallback(MainActivity.callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                EventBus.getDefault().post(new LoadingDismissEvent());
                                AppHandler.changeToPremium(activity,Tools.getFreePremiumDays(activity),true);
                                EventBus.getDefault().post(new UpdatePremiumEvent());
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                    }
                                });

                            }
                        }).start();
                    }

                    @Override
                    public void onCancel() {
                        EventBus.getDefault().post(new LoadingDismissEvent());
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        EventBus.getDefault().post(new LoadingDismissEvent());
                        dialog.dismiss();
                    }


                });

                String url = Tools.getSharedPhotoUrl(activity);

                Glide.with(activity)
                        .asBitmap()
                        .load(url)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                SharePhoto sharePhoto = new SharePhoto.Builder()
                                        .setBitmap(resource)
                                        .build();

                                String sharedText = "Conoce y conecta con gente nueva, ten citas y llevas tus relaciones a otro nivel ❤️"+"\n\n";

                                sharedText = sharedText +"Que esperas, decarga nuestra app ya \uD83E\uDD29"+"\n";

                                String web = Tools.getWebsite(activity);
                                sharedText = !web.isEmpty() ? sharedText + "\n"+ "\uD83C\uDF10"+" Web: "+ web:sharedText;
                                String directLink = Tools.getDirectLink(activity);
                                sharedText = !directLink.isEmpty() ? sharedText + "\n"+ "⬇️"+" Descarga Directa: "+ directLink:sharedText;
                                sharedText = !Tools.getApkLisUrl(activity).isEmpty() ? sharedText +"\n"+ "⬇️"+" Apklis:"+ "\n"+Tools.getApkLisUrl(activity):sharedText;
                                sharedText = !Tools.getGooglePlayUrl(activity).isEmpty() ? sharedText + "\n"+ "⬇️"+" Google Play: "+Tools.getGooglePlayUrl(activity) : sharedText;
                                String social = Tools.getSocial(activity);
                                String facebook = Tools.getValue(social,"facebook");
                                String instagram = Tools.getValue(social,"instagram");
                                sharedText = !facebook.isEmpty() && !instagram.isEmpty() ? sharedText + "\n"+ "👉"+" Siguenos en:":sharedText;
                                sharedText = !facebook.isEmpty() ? sharedText + "\n"+ "#️"+" Facebook: "+ facebook:sharedText;
                                sharedText = !instagram.isEmpty() ? sharedText + "\n"+ "#️"+" Instagram: "+ instagram:sharedText;



                                if (ShareDialog.canShow(SharePhotoContent.class)) {
                                    SharePhotoContent photoContent = new SharePhotoContent.Builder()
                                            .addPhoto(sharePhoto)
                                            .setShareHashtag(new ShareHashtag.Builder()
                                                    .setHashtag(sharedText)
                                                    .build())
                                            .build();

                                    try {
                                        shareDialog.show(photoContent);
                                    }catch (Exception e){
                                        Toast.makeText(activity, "Facebook Error. Intente en el próximo inicio", Toast.LENGTH_LONG).show();
                                        EventBus.getDefault().post(new LoadingDismissEvent());
                                        dialog.dismiss();
                                    }

                                }else{
                                    Toast.makeText(activity, "Facebook Error. Intente en el próximo inicio", Toast.LENGTH_LONG).show();
                                    EventBus.getDefault().post(new LoadingDismissEvent());
                                    dialog.dismiss();
                                }

                            }

                            @Override
                            public void onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable Drawable errorDrawable) {
                                super.onLoadFailed(errorDrawable);
                                EventBus.getDefault().post(new LoadingDismissEvent());
                                Toast.makeText(activity, "Carga Fallida", Toast.LENGTH_SHORT).show();
                            }
                        });

               /* ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(url))
                        .setShareHashtag(new ShareHashtag.Builder()
                                .setHashtag("#Both")
                                .build())
                        .setQuote("Por qué estás solo si somos miles "+"\uD83D\uDE09"+"?")
                        .build();


                if (ShareDialog.canShow(ShareLinkContent.class)){
                    shareDialog.show(content);
                }*/

            }
        });

        FrameLayout bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        dialog.show();

    }
}
