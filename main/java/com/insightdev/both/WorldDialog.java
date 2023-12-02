package com.insightdev.both;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class WorldDialog {
    Activity activity;

    public interface ActionInterface {
        void goPrem();
    }

    ActionInterface actionInterface;

    public void showDialog(Activity activity, ActionInterface actionInterface) {
        this.activity = activity;
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.world_lock_dialog);
        ImageView close = dialog.findViewById(R.id.close);
        TextView goPremium = dialog.findViewById(R.id.goPrem);

        this.actionInterface = actionInterface;

        goPremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionInterface.goPrem();
                dialog.dismiss();
                MainActivity.openPremiumFragment();
            }
        });


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
