package com.insightdev.both;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.greenrobot.eventbus.EventBus;

public class UpdateDialog {
    Activity activity;

    public void showDialog(Activity activity, boolean isRequired) {
        this.activity = activity;
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(!isRequired);
        dialog.setContentView(R.layout.update_dialog);
        TextView update = dialog.findViewById(R.id.update);
        TextView cancel = dialog.findViewById(R.id.cancel);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new LoadingShowEvent());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String url = Tools.getAppUrl(activity);
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        activity.startActivityForResult(i, MainActivity.REQUEST_APP_URL);
                    }
                }).start();
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRequired) {
                   /* //AppHandler.cancelWorker(activity);
                    if (!Tools.isMyServiceRunning(MainService.class, activity.getApplicationContext()))
                        activity.stopService(new Intent(activity, MainService.class));
                    EventBus.getDefault().post(new ServiceShutDownEvent());
                } else {*/
                    dialog.dismiss();
                }
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });

        FrameLayout bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        dialog.show();

    }
}
