package com.insightdev.both.viewmodels;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.insightdev.both.AppHandler;
import com.insightdev.both.CRequest;
import com.insightdev.both.LocationModel;
import com.insightdev.both.MainActivity;
import com.insightdev.both.Profile;
import com.insightdev.both.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class ReverseGeoViewModel extends ViewModel {

    //String url="https://api.bigdatacloud.net/data/reverse-geocode-client?latitude=20.02233&longitude=-75.83324&localityLanguage=en";

    private MutableLiveData<LocationModel> modelMutableLiveData = new MutableLiveData<>();


    public LiveData<LocationModel> getReverseModelLiveData() {

        return modelMutableLiveData;

    }


    public void getLocationInfo(Context context) {

        if (!Tools.isConnected(context))
            return;

        String lat, lon;
        if (MainActivity.latitude != -500) {
            lat = String.valueOf(MainActivity.latitude);
            lon = MainActivity.longitude + "";
        } else {
            lat = Profile.getLatitude();
            lon = Profile.getLongitude();
        }

        if (lat.isEmpty() || lon.isEmpty())
            return;
        Log.d("ablsdkamlda", lat + " /l " + lon);

        CRequest request = new CRequest(context, "https://api.bigdatacloud.net/data/reverse-geocode-client?latitude=" + lat + "&longitude=" + lon + "&localityLanguage=" + Locale.getDefault().getLanguage(), 15);

        request.makeGETRequest();
        request.setOnResponseListener(new CRequest.OnResponseListener() {
            @Override
            public void onResponse(String response) {

                JSONObject json = null;
                try {
                    json = new JSONObject(response);

                    String city = json.getString("city");

                    if (city.isEmpty()) {
                        
                        city = json.getString("principalSubdivision");

                        city = city.replace(" Province", "");

                        city = city.replace("Provincia de ", "");

                    }

                    modelMutableLiveData.postValue(new LocationModel(city, json.getString("countryName"), json.getString("countryCode"), true));

                } catch (JSONException e) {
                    modelMutableLiveData.postValue(new LocationModel("", "", "", false));
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String error) {


                if (error.contains("TimeoutError"))
                    getLocationInfo(context);

                modelMutableLiveData.postValue(new LocationModel("", "", "", false));

            }
        });
    }


}
