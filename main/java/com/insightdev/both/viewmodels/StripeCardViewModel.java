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
import com.insightdev.both.PaymentModel;
import com.insightdev.both.Profile;
import com.insightdev.both.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class StripeCardViewModel extends ViewModel {

    private MutableLiveData<JSONObject> resp = new MutableLiveData<>();

    private MutableLiveData<JSONObject> gPayresp = new MutableLiveData<>();

    public LiveData<JSONObject> getResponseLiveData() {

        return resp;

    }

    public LiveData<JSONObject> getGPayLiveData() {

        return gPayresp;

    }


    public void sendPaymentMethod(Context context, PaymentModel paymentModel, int count) {

       


        CRequest request = new CRequest(context, AppHandler.payScript(), 15);

        ArrayList<Pair<String, String>> pairs = new ArrayList<>();

        pairs.add(new Pair<>("auth", AppHandler.pass()));
        pairs.add(new Pair<>("id", Profile.person.getId()));
        pairs.add(new Pair<>("payment_method_id", paymentModel.getPaymentMethodId()));
        pairs.add(new Pair<>("idempotency_key", paymentModel.getIdempotencyKey()));
        pairs.add(new Pair<>("amount", paymentModel.getAmount()));
        pairs.add(new Pair<>("name", paymentModel.getName()));
        pairs.add(new Pair<>("currency", paymentModel.getCurrency()));
        pairs.add(new Pair<>("duration", String.valueOf(paymentModel.getPlanDuration())));


        request.makeRequest(pairs);

        Log.d("anslkdadafa", paymentModel.getPaymentMethodId() + " / count " + count);

        request.setOnResponseListener(new CRequest.OnResponseListener() {
            @Override
            public void onResponse(String response) {


                new Thread(() -> Tools.saveSettings("payment_model_pending", "", context)).start();

                try {

                    JSONObject object = new JSONObject(response);

                    resp.postValue(object);

                } catch (JSONException e) {
                    Log.d("anslkdadafa", "errJson" + e.getMessage());
                    resp.postValue(null);
                }

            }

            @Override
            public void onError(String error) {
                final int[] finalCount = {count};

                if (error.contains("TimeoutError") || error.contains("NoConnectionError"))
                    if (finalCount[0] < 3)
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                finalCount[0]++;
                                sendPaymentMethod(context, paymentModel, finalCount[0]);
                            }
                        }, 1);
                    else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Tools.saveSettings("payment_model_pending", new Gson().toJson(paymentModel), context);
                            }
                        }).start();
                        resp.postValue(null);
                    }
                else
                    resp.postValue(null);

            }
        });

    }

    public void chargeUser(Context context, String token, final int counter) {
        CRequest request = new CRequest(context, AppHandler.chargeUser(), 15);

        ArrayList<Pair<String, String>> pairs = new ArrayList<>();

        pairs.add(new Pair<>("token", token));


        request.makeRequest(pairs);

        request.setOnResponseListener(new CRequest.OnResponseListener() {
            @Override
            public void onResponse(String response) {

                Log.d("anslkdadafa", "ok " + response);
                try {

                    JSONObject object = new JSONObject(response);


                    gPayresp.postValue(object);

                } catch (JSONException e) {
                    Log.d("anslkdadafa", "errJson" + e.getMessage());
                    gPayresp.postValue(null);
                }

            }

            @Override
            public void onError(String error) {
                final int[] finalCount = {counter};
                Log.d("anslkdadafaasfan2p", "error " + error);
                if (error.contains("TimeoutError") || error.contains("NoConnectionError"))
                    if (finalCount[0] < 3) {
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                finalCount[0]++;
                                chargeUser(context, token, finalCount[0]);
                            }
                        }, 1);
                        return;
                    }
                resp.postValue(null);

            }
        });

    }

}
