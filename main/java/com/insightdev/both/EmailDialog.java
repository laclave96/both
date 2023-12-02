package com.insightdev.both;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.greenrobot.eventbus.EventBus;

public class EmailDialog {
    public void showDialog(Activity activity) {

        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.email_dialog);
        EditText edit = dialog.findViewById(R.id.edit);
        TextView save = dialog.findViewById(R.id.saveButton);
        ImageView delete = dialog.findViewById(R.id.delete);

        if (!Profile.getEmail().isEmpty()) {
            edit.setText(Profile.getEmail());
            delete.setVisibility(View.VISIBLE);
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Tools.validateEmail(edit, false)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Profile.setData("email", edit.getText().toString().trim(), activity);
                            EventBus.getDefault().post(new BasicInfoEvent());
                        }
                    }).start();
                    dialog.dismiss();
                }

            }


        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.setText("");
            }
        });

        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0 & delete.getVisibility() == View.INVISIBLE) {

                    delete.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.fade_in));
                    delete.setVisibility(View.VISIBLE);

                } else if (s.toString().length() == 0 & delete.getVisibility() == View.VISIBLE) {

                    delete.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.fade_off));
                    delete.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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
