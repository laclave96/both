package com.insightdev.both;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class AppNotificationWorker extends Worker {
    public AppNotificationWorker(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @NotNull
    @Override
    public Result doWork() {
        Log.d("WorkerNotification", "StartingNotificationWork");
        if (new Date().getHours() > 9 && Tools.isAvailableReload(getApplicationContext()) && !Tools.isNotifiedReload(getApplicationContext()) && !AppHandler.isOpeningActivityOpen()) {
            AppNotifications.Notify(getApplicationContext(), -1, "reload", ((BitmapDrawable) getApplicationContext().getDrawable(R.drawable.mini_heart)).getBitmap(), "Vamos...¿que esperas?", "Cientos de personas quieren conocerte \uD83D\uDD25");
            Tools.saveNotifiedReload(getApplicationContext());
            String pushNotification = Tools.getPushNotification(getApplicationContext());
            Log.d("WorkerNotification", "push"+pushNotification);
            if (!pushNotification.isEmpty()){
                String title = Tools.getValue(pushNotification,"title");
                String body = Tools.getValue(pushNotification,"body");
                String logo = Tools.getValue(pushNotification, "logo");
                Log.d("WorkerNotification", "body"+body);
                Glide.with(getApplicationContext()).asBitmap().load(logo).apply(new RequestOptions().override(100, 100)).encodeFormat(Bitmap.CompressFormat.JPEG).encodeQuality(100).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).transform(new CircleCrop()).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                        AppNotifications.Notify(getApplicationContext(), -3, "push", resource, title, body);

                    }
                });

            }

            if (Profile.person == null) {
                AppNotifications.Notify(getApplicationContext(), -2, "complete_profile", ((BitmapDrawable) getApplicationContext().getDrawable(R.drawable.mini_heart)).getBitmap(), "Casi, casi...", "Completa tu perfil y unétenos \uD83D\uDE09");
                return Result.success();
            }

            if (!Profile.isSetupComplete()) {
                AppNotifications.Notify(getApplicationContext(), -2, "complete_profile", ((BitmapDrawable) getApplicationContext().getDrawable(R.drawable.mini_heart)).getBitmap(), "Casi, casi...", "Completa tu perfil y unétenos \uD83D\uDE09");
            }


        }
        return Result.success();
    }
}
