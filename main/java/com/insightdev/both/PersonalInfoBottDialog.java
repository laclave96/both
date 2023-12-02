package com.insightdev.both;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class PersonalInfoBottDialog {

    DatePickerDialog datePickerDialog;
    TextView editCity;
    ImageView icon;
    CircularProgressIndicator indicator;
    LocationModel locationModel = null;

    public void showDialog(Activity activity) {
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.info_persona_dialog);
        datePickerDialog = new DatePickerDialog();

        EditText editName = dialog.findViewById(R.id.editName);
        editCity = dialog.findViewById(R.id.editCity);
        TextView date = dialog.findViewById(R.id.agePicker);
        TextView save = dialog.findViewById(R.id.nextButton);
        icon = dialog.findViewById(R.id.ic_location);
        indicator = dialog.findViewById(R.id.location_indicator);
        indicator.hide();

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        editCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                        (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

                    new LocationDialog().showDialog(activity);
                } else {
                    EventBus.getDefault().post(new ActivateLocationListenerEvent());
                    Tools.saveSettings("location_object", "", activity);
                    icon.setVisibility(View.INVISIBLE);
                    indicator.show();
                    editCity.setText("Localizando...");

                }

            }
        });

        final int[] dayI = {0};
        final int[] monthI = {0};
        final int[] yearI = {0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                String name = Profile.getName();
                String city = Profile.getCity();
                String country = Profile.getCountry();
                String birth = Profile.getBirth();

                editName.setText(name);

                if (!birth.isEmpty()) {
                    yearI[0] = Integer.parseInt(birth.substring(0, 4));
                    monthI[0] = Integer.parseInt(birth.substring(4, 6));
                    dayI[0] = Integer.parseInt(birth.substring(6, 8));
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            date.setText(dayI[0] + " " + Tools.getMonth(monthI[0]) + " " + yearI[0]);
                        }
                    });

                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!city.isEmpty())
                            editCity.setText(city + ", " + country);
                    }
                });


            }
        }).start();

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.showDialog(activity, date);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(!Tools.validateName(editName, false))) {
                    if (Profile.getBirth() == null) {
                        Toast.makeText(activity, "Eliga su fecha de nacimiento", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (Profile.getBirth().isEmpty()) {
                        Toast.makeText(activity, "Eliga su fecha de nacimiento", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String name = editName.getText().toString().trim();

                    String city = editCity.getText().toString().trim();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Profile.setData(Tools.getString(R.string.name, activity), name, activity);
                            if (locationModel != null) {
                                Profile.setData(Tools.getString(R.string.city, activity), locationModel.getCity(), activity);
                                Profile.setData("country", locationModel.getCountry(), activity);
                                Profile.setData("region", RegionsUtils.getRegion(locationModel.getCountryCode()), activity);
                            }
                            AppHandler.checkSetupIsComplete(activity);
                            EventBus.getDefault().post(new BasicInfoEvent());
                        }
                    }).start();
                    dialog.dismiss();


                }
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.d("amsdad", "holaaa");
                EventBus.getDefault().unregister(this);
                dialog.dismiss();
            }
        });
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        dialog.show();

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LocationEvent event) {

        locationModel = event.getModel();
        editCity.setText(locationModel.getCity() + ", " + locationModel.getCountry());
        indicator.hide();
        icon.setVisibility(View.VISIBLE);
    }

}
