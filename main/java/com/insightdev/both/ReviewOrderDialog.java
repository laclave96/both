package com.insightdev.both;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class ReviewOrderDialog {
    Activity activity;
    Dialog dialog;

    public interface ActionInterface {

        void pay();

    }


    public void showDialog(Activity activity, String total, String currencySymb, String card, ActionInterface actionInterface) {
        this.activity = activity;
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        this.dialog = dialog;
        dialog.setContentView(R.layout.review_order_dialog);
        ImageView close = dialog.findViewById(R.id.close);
        TextView totalTv = dialog.findViewById(R.id.total);
        TextView cardTv = dialog.findViewById(R.id.cardBrand);

        TextView pay = dialog.findViewById(R.id.pay);


        totalTv.setText(total + currencySymb);
        cardTv.setText(card);

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


        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionInterface.pay();
                dialog.dismiss();

            }
        });

        FrameLayout bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        dialog.show();

    }


}
