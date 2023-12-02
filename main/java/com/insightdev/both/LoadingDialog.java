package com.insightdev.both;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Window;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class LoadingDialog {
    private Dialog dialog;

    public void showDialog(Activity activity, String title) {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        dialog.setContentView(R.layout.loading_dialog);

        TextView textView = dialog.findViewById(R.id.text);

        textView.setText(title + "...");
        dialog.show();

    }

    public void dismiss() {
        if (dialog != null)
            dialog.dismiss();
    }
}
