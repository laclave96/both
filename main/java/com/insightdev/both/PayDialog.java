package com.insightdev.both;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class  PayDialog {

    public void showDialog(Activity activity, int cantTotal) {
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.pay_dialog);

        TextView cancel = dialog.findViewById(R.id.cancel);
        TextView total = dialog.findViewById(R.id.total);
        TextView cardNumber = dialog.findViewById(R.id.cardNumber);
        ImageView copy = dialog.findViewById(R.id.copy);
        TextView openTransfer = dialog.findViewById(R.id.openTransfer);
        View bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);

        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(MainActivity.screenHeight);

        total.setText(cantTotal+" CUP");
        String number = Tools.getCardNumber(activity);
        cardNumber.setText(number.substring(0,4)+" "+number.substring(4,8)+" "+number.substring(8,12)
                +" "+number.substring(12,16));

        cardNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("card number",number);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(activity, "Copiado", Toast.LENGTH_SHORT).show();
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("card number",number);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(activity, "Número de cuenta copiado", Toast.LENGTH_SHORT).show();
            }
        });

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        dialog.show();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        openTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = activity.getPackageManager().getLaunchIntentForPackage("cu.etecsa.cubacel.tr.tm");
                    activity.startActivity(i);
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("card number",number);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(activity, "Número de cuenta copiado", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(activity, "Instale Transfermóvil antes", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
