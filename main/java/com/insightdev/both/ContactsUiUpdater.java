package com.insightdev.both;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public class ContactsUiUpdater {


    public static void changeContacts() {
        EventBus.getDefault().post(new ChangeContactsEvent());
    }

    public static void notifyLike(Contact contact, Context context) {
        String text = "Tienes un nuevo like de " + contact.getName();
      /*  if (!Profile.isPremium(context)) {
            AppNotifications.Notify(context, Integer.parseInt(contact.getId()),
                    Tools.getString(R.string.like, context), ((BitmapDrawable) context.getDrawable(R.drawable.mini_heart)).getBitmap(), text, "Échale un vistazo a su perfil");
            return;
        }*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                try {
                    bitmap = Picasso.get().
                            load(contact.getProfilePhotoLow(context)).transform(new RoundedCornersTransformation(60, 0))
                            .centerCrop().resize(170, 170).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Bitmap finalBitmap = bitmap;
                AppHandler.executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Activity_", "notifyLike" + contact.getName());
                        AppNotifications.Notify(context, Integer.parseInt(contact.getId()),
                                Tools.getString(R.string.like, context), finalBitmap, text, "¿Qué esperas para hacer match?");

                    }
                });
            }
        }).start();


    }

    public static void notifyMatch(Contact contact, Context context) {

        String text = "Tienes un nuevo match con " + contact.getName();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                try {
                    bitmap = Picasso.get().
                            load(contact.getProfilePhotoLow(context)).transform(new RoundedCornersTransformation(60, 0))
                            .centerCrop().resize(170, 170).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Bitmap finalBitmap = bitmap;
                AppHandler.executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Activity_", "notifyMatch" + contact.getName());
                        AppNotifications.Notify(context, Integer.parseInt(contact.getId()),
                                Tools.getString(R.string.match, context), finalBitmap, text, "¿Qué esperas para decirle hola?");
                    }
                });

            }
        }).start();
    }

    public static void openMatchScreen(String username) {
        EventBus.getDefault().post(new OpenMatchActivityEvent(username));
    }


}
