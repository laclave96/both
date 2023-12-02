package com.insightdev.both;

import android.app.Activity;
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

public class AdsDialog {
    AnimatedCheckBox animatedCheckBox;
    TextView textCheck;
    ImageView close;

    public void showDialog(Activity activity) {
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.ads_dialog);

        animatedCheckBox = dialog.findViewById(R.id.check1);
        textCheck = dialog.findViewById(R.id.activeAds);
        close = dialog.findViewById(R.id.close);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Tools.getSettings("erotic_ads_available",activity).contentEquals("1"))
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            animatedCheckBox.setChecked(true);
                        }
                    });
            }
        }).start();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        textCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animatedCheckBox.setChecked(!animatedCheckBox.isChecked(), true);
                int flag = animatedCheckBox.isChecked() ? 1 : 0;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Tools.saveSettings("erotic_ads_available",flag+"",activity);
                        Log.d("Erotic_","ads"+Tools.getSettings("erotic_ads_available",activity));

                    }
                }).start();


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
