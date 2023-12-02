package com.insightdev.both;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.animsh.animatedcheckbox.AnimatedCheckBox;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;


import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class ReportDialog {

    public static native String report();
    public static native String pass();

    static {
        System.loadLibrary("native-lib");
    }
    public void showDialog(Activity activity, int id) {

        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.report_layout);

        AnimatedCheckBox checkBox1, checkBox2, checkBox3, checkBox4;
        final String[] reason = new String[1];
        reason[0] = "";
        TextView reason1 = dialog.findViewById(R.id.reason1);
        TextView reason2 = dialog.findViewById(R.id.reason2);
        TextView reason3 = dialog.findViewById(R.id.reason3);
        TextView reason4 = dialog.findViewById(R.id.reason4);

        EditText editNewAnswer = dialog.findViewById(R.id.editReason);
        TextView save = dialog.findViewById(R.id.save);

        checkBox1 = dialog.findViewById(R.id.check1);
        checkBox2 = dialog.findViewById(R.id.check2);
        checkBox3 = dialog.findViewById(R.id.check3);
        checkBox4 = dialog.findViewById(R.id.check4);



        reason1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = checkBox1.isChecked();
                checkBox1.setChecked(!isCheck, true);

                if (!isCheck) {
                    checkBox2.setChecked(false, false);
                    checkBox3.setChecked(false, false);
                    checkBox4.setChecked(false, false);
                    editNewAnswer.setText("");
                    reason[0] = reason1.getText().toString();
                }

            }
        });

        reason2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = checkBox2.isChecked();
                checkBox2.setChecked(!isCheck, true);

                if (!isCheck){
                    checkBox1.setChecked(false, false);
                    checkBox3.setChecked(false, false);
                    checkBox4.setChecked(false, false);
                    editNewAnswer.setText("");
                    reason[0] = reason2.getText().toString();
                }
            }
        });

        reason3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = checkBox3.isChecked();
                checkBox3.setChecked(!isCheck, true);

                if (!isCheck){
                    checkBox4.setChecked(false, false);
                    checkBox2.setChecked(false, false);
                    checkBox1.setChecked(false, false);
                    editNewAnswer.setText("");
                    reason[0] = reason3.getText().toString();
                }
            }
        });

        reason4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = checkBox4.isChecked();
                checkBox4.setChecked(!isCheck, true);

                if (!isCheck){
                    checkBox3.setChecked(false, false);
                    checkBox2.setChecked(false, false);
                    checkBox1.setChecked(false, false);
                    editNewAnswer.setText("");
                    reason[0] = reason4.getText().toString();
                }
            }
        });

        checkBox1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        checkBox2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        checkBox3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        checkBox4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        editNewAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().length() > 0){
                    checkBox1.setChecked(false, false);
                    checkBox2.setChecked(false, false);
                    checkBox3.setChecked(false, false);
                    checkBox4.setChecked(false, false);
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
                if (!Tools.isConnected(activity)){
                    Toast.makeText(activity, "Sin Conexion", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (reason[0].isEmpty()){
                    Toast.makeText(activity, "Por favor especifique el motivo de la denuncia", Toast.LENGTH_SHORT).show();
                    return;
                }
                EventBus.getDefault().post(new LoadingShowEvent());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        CRequest request = new CRequest(activity, report(),
                                15);
                        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
                        pairs.add(new Pair<>(Tools.getString(R.string.auth,activity), pass()));
                        pairs.add(new Pair<>(Tools.getString(R.string.from_id,activity), Profile.getId()));
                        pairs.add(new Pair<>(Tools.getString(R.string.to_id,activity), id+""));
                        pairs.add(new Pair<>(Tools.getString(R.string.reason,activity), reason[0]));
                        request.makeRequest(pairs);
                        request.setOnResponseListener(new CRequest.OnResponseListener() {
                            @Override
                            public void onResponse(String response) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        EventBus.getDefault().post(new LoadingDismissEvent());
                                        dialog.dismiss();
                                    }
                                });
                            }

                            @Override
                            public void onError(String error) {
                                EventBus.getDefault().post(new LoadingDismissEvent());
                            }
                        });
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
