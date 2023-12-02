package com.insightdev.both;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.w3c.dom.Text;

public class NewFeatureDialog {
    Activity activity;

    public void showDialog(Activity activity, Contact contact, int from, String date) {
        this.activity = activity;
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.new_feature_dialog);


        TextView go = dialog.findViewById(R.id.goPrem);



        Tools.saveSettings("is_icebreaker", "yes", activity);

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new IceBreakerDialog().showDialog(activity, contact, from, date);
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
