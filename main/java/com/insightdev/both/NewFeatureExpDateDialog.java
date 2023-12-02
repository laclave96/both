package com.insightdev.both;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class NewFeatureExpDateDialog {
    Activity activity;

    public interface ActionInterface {
        void go();
    }

    ActionInterface actionInterface;

    public void showDialog(Activity activity, ActionInterface actionInterface) {
        this.activity = activity;
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.new_feature_exp_date);
        this.actionInterface = actionInterface;


        TextView go = dialog.findViewById(R.id.goPrem);

        Tools.saveSettings("is_new_feature_exp_date", "yes", activity);

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                actionInterface.go();
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
