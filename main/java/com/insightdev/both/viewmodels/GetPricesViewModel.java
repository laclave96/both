package com.insightdev.both.viewmodels;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.insightdev.both.AppHandler;
import com.insightdev.both.CRequest;
import com.insightdev.both.GetPriceResponseModel;
import com.insightdev.both.PriceItem;
import com.insightdev.both.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class GetPricesViewModel extends ViewModel {

    MutableLiveData<GetPriceResponseModel> pricesList = new MutableLiveData<>();


    public LiveData<GetPriceResponseModel> getLiveDataPricesList() {

        return pricesList;
    }


    public void getPricesList(Context context) {
        String country_code = Profile.getCountryCode();

        Log.d("abslkdansasbdaldmk", "CC " + country_code);

        CRequest request = new CRequest(context, AppHandler.getPrices(), 15);
        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>("countryCode", country_code));

        request.makeRequest(pairs);
        request.setOnResponseListener(new CRequest.OnResponseListener() {
            @Override
            public void onResponse(String response) {

                try {

                    ArrayList<PriceItem> priceItems = new ArrayList<>();

                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray json = jsonObject.getJSONArray("plan");

                    for (int i = 0; i < json.length(); i++)
                        priceItems.add(new Gson().fromJson(json.get(i).toString(), PriceItem.class));


                    pricesList.postValue(new GetPriceResponseModel(priceItems, jsonObject.getString("currency"), jsonObject.getString("currencySymbol")));

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("alksdjañlsf", "JSON ERROR " + e.getMessage());
                }

            }

            @Override
            public void onError(String error) {

                Log.d("alksdjañlsf", "error " + error);

                //customer.postValue("error");
            }
        });
    }

}
