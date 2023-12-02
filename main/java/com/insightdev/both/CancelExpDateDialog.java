package com.insightdev.both;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class CancelExpDateDialog {


    Activity activity;


    public interface ActionInterface {
        void methodOk();
    }

    ActionInterface actionInterface;

    public void showDialog(Activity activity, ActionInterface actionInterface) {
        this.activity = activity;
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.exp_date_cancel_confirmation);

        // ImageView drawable = dialog.findViewById(R.id.drawable);

        TextView go = dialog.findViewById(R.id.ok);

        TextView cancel = dialog.findViewById(R.id.cancel);

        ImageView close = dialog.findViewById(R.id.close);

        this.actionInterface = actionInterface;

        // Glide.with(activity).asGif().load(R.raw.expd).into(drawable);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });


        Tools.saveSettings("is_icebreaker", "yes", activity);

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

                actionInterface.methodOk();

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
