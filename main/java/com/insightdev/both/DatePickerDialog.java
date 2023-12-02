package com.insightdev.both;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.autofill.AutofillValue;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Date;

public class DatePickerDialog {

    public void showDialog(Activity activity, TextView date) {

        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.pick_date);
        DatePicker datePicker = dialog.findViewById(R.id.datePicker);
        TextView save = dialog.findViewById(R.id.nextButton);

        Date max = new Date();
        max.setYear(max.getYear() - 18);
        datePicker.setMaxDate(max.getTime());


        try {
            if (Profile.person.getYear() == -1)
                datePicker.updateDate((max.getYear() + 1900), 1, 1);
            else
                datePicker.updateDate(Profile.person.getYear(), Profile.person.getMonth()-1, Profile.person.getDay());
        } catch (Exception e) {

        }


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String day = String.valueOf(datePicker.getDayOfMonth());
                day = day.length() == 1 ? "0" + day : day;
                String month = String.valueOf(datePicker.getMonth() + 1);
                month = month.length() == 1 ? "0" + month : month;
                String year = String.valueOf(datePicker.getYear());


               /* ArrayList<Pair<String,String>> birth = new ArrayList<>();
                birth.add(new Pair<>(Tools.getString(R.string.day,activity),day));
                birth.add(new Pair<>(Tools.getString(R.string.month,activity),month+""));
                birth.add(new Pair<>(Tools.getString(R.string.year,activity),year));*/
                String birth = year + month + day;
                date.setText(Integer.parseInt(day) + " " + Tools.getMonth(Integer.parseInt(month)) + " " + year);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Profile.setData(Tools.getString(R.string.birth, activity), birth, activity);
                    }
                }).start();
                dialog.dismiss();

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
