package com.insightdev.both;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.greenrobot.eventbus.EventBus;

public class AboutMeDialog {
    public void showDialog(Activity activity, TextView textAboutMe) {
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.about_dialog);
        EditText editAbout = dialog.findViewById(R.id.editAbout);
        TextView save = dialog.findViewById(R.id.nextButton);

        String str = Profile.getAboutMe();
        if (str != null)
            if (!str.isEmpty())
                editAbout.setText(str);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = editAbout.getText().toString().trim();
                if (!str.isEmpty()){
                    textAboutMe.setTextColor(activity.getResources().getColor(R.color.black));
                    textAboutMe.setText(str);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Profile.setData(Tools.getString(R.string.about_me,activity),str,activity);
                        }
                    }).start();
                    dialog.dismiss();
                    return;
                }
                Toast.makeText(activity, "Descripción vacía", Toast.LENGTH_SHORT).show();
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
