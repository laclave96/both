package com.insightdev.both.viewmodels;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.insightdev.both.AppHandler;
import com.insightdev.both.CRequest;
import com.insightdev.both.LocationModel;
import com.insightdev.both.Profile;
import com.insightdev.both.R;
import com.insightdev.both.RegionsUtils;
import com.insightdev.both.Tools;

import java.util.ArrayList;
import java.util.Objects;

public class UpdateDatabaseViewModel extends ViewModel {

    private MutableLiveData<String> resp = new MutableLiveData<>();


    public LiveData<String> getResponseLiveData() {

        return resp;

    }


    public void updateAutoBilling(Context context, String option, final int counter) {
        Log.d("aklsdjad", option + " :option");

        CRequest request = new CRequest(context, AppHandler.updateAutoBilling(),
                20);

        ArrayList<Pair<String, String>> pairs = new ArrayList<>();

        pairs.add(new Pair<>("id", Profile.getId()));
        pairs.add(new Pair<>("option", option));
        pairs.add(new Pair<>("auth", AppHandler.pass()));

        request.makeRequest(pairs);

        request.setOnResponseListener(new CRequest.OnResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.d("aklsdjad", Objects.equals(response, "true") + "");
                resp.postValue(response);

            }

            @Override
            public void onError(String error) {
                if (error.contains("TimeoutError"))
                    if (counter < 3) {
                        int i = counter;
                        i++;
                        updateAutoBilling(context, option, i);
                        return;
                    }
                resp.postValue("error");
                Log.d("aklsdjad", error);
            }
        });

    }

}
