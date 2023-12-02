package com.insightdev.both;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.animsh.animatedcheckbox.AnimatedCheckBox;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class NotificationsDialog {

    AnimatedCheckBox animatedCheckBox, animatedCheckBox2, animatedCheckBox3;
    TextView chatTv, likesTv, appTv;
    ImageView close;

    public void showDialog(Activity activity) {
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.notification_dialog);

        animatedCheckBox = dialog.findViewById(R.id.check1);
        animatedCheckBox2 = dialog.findViewById(R.id.check2);
        animatedCheckBox3 = dialog.findViewById(R.id.check3);
        chatTv = dialog.findViewById(R.id.chatNotify);
        likesTv = dialog.findViewById(R.id.likesNotify);
        appTv = dialog.findViewById(R.id.appNotify);
        close = dialog.findViewById(R.id.close);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String chatChannel = Tools.getSettings("chat_channel",activity);
                String likesChannel = Tools.getSettings("likes_channel",activity);
                String appChannel = Tools.getSettings("app_channel",activity);
                if (chatChannel.contentEquals("1") || chatChannel.isEmpty())
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            animatedCheckBox.setChecked(true);
                        }
                    });
                if (likesChannel.contentEquals("1") || likesChannel.isEmpty())
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            animatedCheckBox2.setChecked(true);
                        }
                    });

                if (appChannel.contentEquals("1") || appChannel.isEmpty())
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            animatedCheckBox3.setChecked(true);
                        }
                    });
            }
        }).start();

        chatTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = animatedCheckBox.isChecked();
                animatedCheckBox.setChecked(!isChecked, true);
                int flag = animatedCheckBox.isChecked() ? 1 : 0;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("maslñdaksñd", flag + " Flag");
                        Tools.saveSettings("chat_channel",flag+"",activity);
                    }
                }).start();

            }
        });


        likesTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = animatedCheckBox2.isChecked();
                animatedCheckBox2.setChecked(!isChecked, true);
                int flag = animatedCheckBox2.isChecked() ? 1 : 0;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Tools.saveSettings("likes_channel",flag+"",activity);
                    }
                }).start();


            }
        });

        appTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = animatedCheckBox3.isChecked();
                animatedCheckBox3.setChecked(!isChecked, true);
                int flag = animatedCheckBox3.isChecked() ? 1 : 0;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Tools.saveSettings("app_channel",flag+"",activity);
                    }
                }).start();


            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        FrameLayout bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        dialog.show();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });

    }
}
