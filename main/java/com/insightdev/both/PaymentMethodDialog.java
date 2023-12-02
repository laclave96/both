package com.insightdev.both;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class PaymentMethodDialog {

    Activity activity;
    Dialog dialog;

    public interface ActionInterface {

        void payWithCard();

        void payWithGooglePay();

    }

    ActionInterface actionInterface;

    public void showDialog(Activity activity, ActionInterface actionInterface, boolean googleAvailable) {
        this.activity = activity;
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        this.dialog = dialog;
        dialog.setContentView(R.layout.payment_method);
        ImageView close = dialog.findViewById(R.id.close);

        TextView addCard = dialog.findViewById(R.id.card);

        TextView googlePay = dialog.findViewById(R.id.google);

        addCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionInterface.payWithCard();
                dialog.dismiss();
            }
        });

        googlePay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (googleAvailable)
                    actionInterface.payWithGooglePay();
                else
                    ProToast.makeText(activity, R.drawable.ic_info, "El pago con google no esta disponible en este momento", Toast.LENGTH_LONG);
                dialog.dismiss();
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

    public void dismissDialog() {
        dialog.dismiss();
    }

}
