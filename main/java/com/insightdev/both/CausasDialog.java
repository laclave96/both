package com.insightdev.both;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.animsh.animatedcheckbox.AnimatedCheckBox;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.greenrobot.eventbus.EventBus;

public class CausasDialog {
    public void showDialog(Activity activity) {

        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.causas_dialog);

        AnimatedCheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6;
        final String[] reason = new String[1];
        reason[0] = "";
        TextView reason1 = dialog.findViewById(R.id.reason1);
        TextView reason2 = dialog.findViewById(R.id.reason2);
        TextView reason3 = dialog.findViewById(R.id.reason3);
        TextView reason4 = dialog.findViewById(R.id.reason4);
        TextView reason5 = dialog.findViewById(R.id.reason5);
        TextView reason6 = dialog.findViewById(R.id.reason6);

        EditText editNewAnswer = dialog.findViewById(R.id.editReason);
        TextView save = dialog.findViewById(R.id.save);

        checkBox1 = dialog.findViewById(R.id.check1);
        checkBox2 = dialog.findViewById(R.id.check2);
        checkBox3 = dialog.findViewById(R.id.check3);
        checkBox4 = dialog.findViewById(R.id.check4);
        checkBox5 = dialog.findViewById(R.id.check5);
        checkBox6 = dialog.findViewById(R.id.check6);

        ImageView close = dialog.findViewById(R.id.close);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        reason1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = checkBox1.isChecked();
                checkBox1.setChecked(!isCheck, true);

                if (!isCheck) {
                    checkBox2.setChecked(false, false);
                    checkBox3.setChecked(false, false);
                    checkBox4.setChecked(false, false);
                    checkBox5.setChecked(false, false);
                    checkBox6.setChecked(false, false);
                    editNewAnswer.setText("");
                    reason[0] = reason1.getText().toString();
                    return;
                }
                reason[0] = "";

            }
        });

        reason2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = checkBox2.isChecked();
                checkBox2.setChecked(!isCheck, true);

                if (!isCheck) {
                    checkBox1.setChecked(false, false);
                    checkBox3.setChecked(false, false);
                    checkBox4.setChecked(false, false);
                    checkBox5.setChecked(false, false);
                    checkBox6.setChecked(false, false);
                    editNewAnswer.setText("");
                    reason[0] = reason2.getText().toString();
                    return;
                }
                reason[0] = "";
            }
        });

        reason3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = checkBox3.isChecked();
                checkBox3.setChecked(!isCheck, true);

                if (!isCheck) {
                    checkBox4.setChecked(false, false);
                    checkBox2.setChecked(false, false);
                    checkBox1.setChecked(false, false);
                    checkBox5.setChecked(false, false);
                    checkBox6.setChecked(false, false);
                    editNewAnswer.setText("");
                    reason[0] = reason3.getText().toString();
                    return;
                }
                reason[0] = "";
            }
        });

        reason4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = checkBox4.isChecked();
                checkBox4.setChecked(!isCheck, true);

                if (!isCheck) {
                    checkBox3.setChecked(false, false);
                    checkBox2.setChecked(false, false);
                    checkBox1.setChecked(false, false);
                    checkBox5.setChecked(false, false);
                    checkBox6.setChecked(false, false);
                    editNewAnswer.setText("");
                    reason[0] = reason4.getText().toString();
                    return;
                }
                reason[0] = "";
            }
        });

        reason5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = checkBox5.isChecked();
                checkBox5.setChecked(!isCheck, true);

                if (!isCheck) {
                    checkBox3.setChecked(false, false);
                    checkBox2.setChecked(false, false);
                    checkBox1.setChecked(false, false);
                    checkBox4.setChecked(false, false);
                    checkBox6.setChecked(false, false);
                    editNewAnswer.setText("");
                    reason[0] = reason4.getText().toString();
                    return;
                }
                reason[0] = "";
            }
        });

        reason6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = checkBox6.isChecked();
                checkBox6.setChecked(!isCheck, true);

                if (!isCheck) {
                    checkBox3.setChecked(false, false);
                    checkBox2.setChecked(false, false);
                    checkBox1.setChecked(false, false);
                    checkBox5.setChecked(false, false);
                    checkBox4.setChecked(false, false);
                    editNewAnswer.setText("");
                    reason[0] = reason4.getText().toString();
                    return;
                }
                reason[0] = "";
            }
        });

        editNewAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().length() > 0) {
                    checkBox1.setChecked(false, false);
                    checkBox2.setChecked(false, false);
                    checkBox3.setChecked(false, false);
                    checkBox4.setChecked(false, false);
                    checkBox5.setChecked(false, false);
                    checkBox6.setChecked(false, false);
                    reason[0] = s.toString();
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Tools.isConnected(activity)) {
                    Toast.makeText(activity, "Sin Conexión", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (reason[0].isEmpty()) {
                    Toast.makeText(activity, "Por favor especifique un motivo", Toast.LENGTH_SHORT).show();
                    return;
                }

                EventBus.getDefault().post(new LoadingShowEvent());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AppHandler.sendReason(activity,reason[0]);
                    }
                }).start();


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
