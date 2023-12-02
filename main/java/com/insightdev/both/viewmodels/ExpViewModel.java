package com.insightdev.both.viewmodels;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.insightdev.both.AppHandler;
import com.insightdev.both.CRequest;

import java.util.ArrayList;

public class ExpViewModel extends ViewModel {
    private MutableLiveData<String> contactData = new MutableLiveData<>();

    public LiveData<String> getContactDataLiveData() {
        return contactData;

    }

    public void getContactData(Context context, String id) {

        if (id.isEmpty())
            return;


        CRequest request = new CRequest(context, AppHandler.getContactWithId(), 15);
        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>("auth", AppHandler.pass()));
        pairs.add(new Pair<>("id", id));


        request.makeRequest(pairs);
        request.setOnResponseListener(new CRequest.OnResponseListener() {
            @Override
            public void onResponse(String response) {

                Log.d("añlskdañd", response);

                contactData.postValue(response);


            }

            @Override
            public void onError(String error) {

                contactData.postValue("error");

            }
        });

    }
}
