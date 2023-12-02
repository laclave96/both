package com.insightdev.both.viewmodels;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.insightdev.both.AppHandler;
import com.insightdev.both.CRequest;
import com.insightdev.both.MainActivity;
import com.insightdev.both.Profile;
import com.insightdev.both.R;
import com.insightdev.both.TimezoneMapper;
import com.insightdev.both.Tools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> trueDate = new MutableLiveData<>();

    private MutableLiveData<String> findExpDateResp = new MutableLiveData<>();


    public LiveData<String> getTrueDateLiveData() {
        return trueDate;

    }

    public LiveData<String> getfindExpRespLiveData() {
        return findExpDateResp;

    }


    public void getTrueDate(Context context) {

        String timeZone;

        String lat, lon;
        if (MainActivity.latitude != -500) {
            lat = MainActivity.latitude + "";
            lon = MainActivity.longitude + "";
        } else {
            lat = Profile.getLatitude();
            lon = Profile.getLongitude();
        }

        if (lat.isEmpty())
            lat = "37.4218521";

        if (lon.isEmpty())
            lon = "-122.0841308";

        timeZone = TimezoneMapper.latLngToTimezoneString(Double.valueOf(lat), Double.valueOf(lon));

        if (timeZone.isEmpty())
            return;

        CRequest request = new CRequest(context, AppHandler.getTrueDate(), 15);
        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>("auth", AppHandler.pass()));
        pairs.add(new Pair<>("timeZone", timeZone));
        request.makeRequest(pairs);
        request.setOnResponseListener(new CRequest.OnResponseListener() {
            @Override
            public void onResponse(String response) {


                trueDate.postValue(response);


            }

            @Override
            public void onError(String error) {

                Log.d("LogToken", "error");

                trueDate.postValue("error");
            }
        });


    }

    public int findExpDateAction(Context context) {

        Log.d("asldmañda", "ok find 0 ");
        String latitud, longitud, distance, find_age_min, find_age_max, find_search_by, regions, timeZone, first_time_s;


        latitud = String.valueOf(MainActivity.latitude);

        longitud = String.valueOf(MainActivity.longitude);

        distance = Tools.getSettings("distance", context);

        first_time_s = Tools.getSettings("fist_time_express_date", context);

        boolean first_time = first_time_s.isEmpty();

        find_age_min = Tools.getSettings("min_age", context);

        find_age_max = Tools.getSettings("max_age", context);

        find_search_by = Tools.getSettings("gender", context);

        regions = Tools.getSettings("regions", context);

        if (regions.isEmpty())
            regions = "0";

        if (MainActivity.latitude != -500) {
            latitud = MainActivity.latitude + "";
            longitud = MainActivity.longitude + "";
        } else {
            latitud = Profile.getLatitude();
            longitud = Profile.getLongitude();
        }

        if (latitud.isEmpty() || longitud.isEmpty())
            return 0;


        if (first_time && (find_search_by.isEmpty() || find_age_min.isEmpty() || find_age_max.isEmpty() || distance.isEmpty())) {
            Tools.saveSettings("fist_time_express_date", "ok", context);
            return 1;
        }

        if (find_search_by.isEmpty())
            find_search_by = "2";

        if (find_age_min.isEmpty())
            find_age_min = "18";

        if (find_age_max.isEmpty())
            find_age_max = "40";


        if (distance.isEmpty())
            distance = "1000";


        timeZone = TimezoneMapper.latLngToTimezoneString(Double.parseDouble(latitud), Double.parseDouble(longitud));


        CRequest request = new CRequest(context, AppHandler.findExpDate(), 15);
        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>("auth", AppHandler.pass()));
        pairs.add(new Pair<>("id", Profile.person.getId()));
        pairs.add(new Pair<>("latitud", latitud));
        pairs.add(new Pair<>("longitud", longitud));
        pairs.add(new Pair<>("find_distance", distance));
        pairs.add(new Pair<>("find_age_min", find_age_min));
        pairs.add(new Pair<>("find_age_max", find_age_max));
        pairs.add(new Pair<>("find_search_by", find_search_by));
        pairs.add(new Pair<>("find_regions", regions));
        pairs.add(new Pair<>("timeZone", timeZone));


        request.makeRequest(pairs);
        Log.d("asldmañda", "ok find makeReq");
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppHandler.updatePendingData(context);
            }
        }).start();
        request.setOnResponseListener(new CRequest.OnResponseListener() {
            @Override
            public void onResponse(String response) {

                Log.d("afna2opkfañsfa", "ok find response" + response);
                findExpDateResp.postValue(response);

            }

            @Override
            public void onError(String error) {

                Log.d("asldmañda", "ok find error" + error);
                findExpDateResp.postValue("error");

            }
        });

        return 10;
    }


}
