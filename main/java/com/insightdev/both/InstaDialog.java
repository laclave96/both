package com.insightdev.both;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;


public class InstaDialog {
    public void showDialog(Activity activity, TextView instaText) {

        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.insta_dialog);
        EditText edit = dialog.findViewById(R.id.edit);
        TextView save = dialog.findViewById(R.id.saveButton);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = edit.getText().toString().trim();
                if (!user.isEmpty()){
                    instaText.setTextColor(Color.BLUE);
                    instaText.setText("@"+user);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String str = "{}";
                            str = Tools.putValue(str, Tools.getString(R.string.instagram,activity),user);
                            Profile.setData(Tools.getString(R.string.social_networks,activity),str,activity);
                        }
                    }).start();
                    dialog.dismiss();
                }else
                    Toast.makeText(activity, "Rellene el campo", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });

    }
}
