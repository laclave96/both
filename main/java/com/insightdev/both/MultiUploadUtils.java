package com.insightdev.both;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import net.gotev.uploadservice.data.UploadNotificationConfig;
import net.gotev.uploadservice.data.UploadNotificationStatusConfig;

import java.io.ByteArrayOutputStream;

import kotlin.jvm.functions.Function2;

public class MultiUploadUtils {


    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static String getRealPathFromURI(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public static Function2<? super Context, ? super String, UploadNotificationConfig> createNotificationChannel(Context context) {

        Function2<? super Context, ? super String, UploadNotificationConfig> test = null;
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = "Upload";
            String description = "getString(R.string.channel_description)";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("UploadJSON", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            UploadNotificationStatusConfig uploadNotificationStatusConfig = new UploadNotificationStatusConfig("Subiendo imágenes",
                    "En proceso...");
            UploadNotificationStatusConfig uploadNotificationStatusConfigSucces = new UploadNotificationStatusConfig("Subiendo imágenes",
                    "Terminado");
            UploadNotificationStatusConfig uploadNotificationStatusConfigError = new UploadNotificationStatusConfig("Subiendo imágenes",
                    "Error");
            UploadNotificationStatusConfig uploadNotificationStatusConfigCancele = new UploadNotificationStatusConfig("Subiendo imágenes",
                    "Cancelado");

            UploadNotificationConfig uploadNotificationConfig = new UploadNotificationConfig("UploadJSON",
                    true,
                    uploadNotificationStatusConfig, uploadNotificationStatusConfigSucces,
                    uploadNotificationStatusConfigError,
                    uploadNotificationStatusConfigCancele
            );

            test = new Function2<Context, String, UploadNotificationConfig>() {
                @Override
                public UploadNotificationConfig invoke(Context context, String s) {
                    return uploadNotificationConfig;
                }
            };

        } else {

        }
        return test;
    }
}
