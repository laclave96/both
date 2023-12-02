package com.insightdev.both;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mikhaellopez.circularimageview.CircularImageView;

public class ExpWaitingDialog {

    Activity activity;

    public interface ActionInterface {
        void cancelWaiting();
    }

    ActionInterface actionInterface;

    public void showDialog(Activity activity, ActionInterface actionInterface) {
        this.activity = activity;
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.exp_date_waiting_dialog);

        this.actionInterface = actionInterface;

        // ImageView drawable = dialog.findViewById(R.id.drawable);

        TextView go = dialog.findViewById(R.id.ok);
        TextView cancel = dialog.findViewById(R.id.cancel);

        ImageView close = dialog.findViewById(R.id.close);


        // Glide.with(activity).asGif().load(R.raw.expd).into(drawable);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();


            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!Tools.isConnected(activity)) {
                    ProToast.makeText(activity, R.drawable.offline, activity.getString(R.string.revise_conexion), Toast.LENGTH_SHORT);
                    return;
                }


                AppHandler.cancelExpDateSearch(activity);

                actionInterface.cancelWaiting();

                dialog.dismiss();


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
