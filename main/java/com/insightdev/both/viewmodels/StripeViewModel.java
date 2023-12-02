package com.insightdev.both.viewmodels;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;
import com.insightdev.both.AppHandler;
import com.insightdev.both.CRequest;
import com.stripe.android.paymentsheet.PaymentSheet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StripeViewModel extends ViewModel {

    final String CUSTOMER_URL = "https://api.stripe.com/v1/customers";

    final String EPHM_URL = "https://api.stripe.com/v1/ephemeral_keys";

    final String PAY_URL = "https://api.stripe.com/v1/payment_intents";

    final String SECRET_KEY = "sk_test_51KujHcHjbd308KiSMgdQb0LVl2XOqYwoDJR377watzNwSjG8O9hAqKocsdA2yXeAd4jFZGn6aa4tQNTc57BJ12ly00104zLkTn";

    final String SECRET_KEY_REAL = "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";


    private MutableLiveData<ArrayList<String>> customer = new MutableLiveData<>();


    public LiveData<ArrayList<String>> getCustomerLiveData() {
        return customer;

    }


    public void createCustomer(Context context) {

        CRequest request = new CRequest(context, CUSTOMER_URL, 15);
        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>("Authorization", "Bearer " + SECRET_KEY));
        //pairs.add(new Pair<>("Content-Type", "application/json"));
        // pairs.add(new Pair<>("Content-Length", "0"));
        request.makeRequestPuttingHeaders(pairs);
        request.setOnResponseListener(new CRequest.OnResponseListener() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject object = new JSONObject(response);

                    getEphKey(context, object.getString("id"));
                    Log.d("alksdjañlsf", object.getString("id"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String error) {

                Log.d("alksdjañlsf", "error " + error);

                //customer.postValue("error");
            }
        });
    }


    public void getEphKey(Context context, String key) {
        if (key.isEmpty())
            return;

        CRequest request = new CRequest(context, EPHM_URL, 15);

        ArrayList<Pair<String, String>> headers = new ArrayList<>();
        ArrayList<Pair<String, String>> params = new ArrayList<>();

        headers.add(new Pair<>("Authorization", "Bearer " + SECRET_KEY));
        headers.add(new Pair<>("Stripe-Version", "2020-08-27"));

        params.add(new Pair<>("customer", key));
        //params.add(new Pair<>("amount", "1009"));
        //params.add(new Pair<>("currency", "eur"));
        //params.add(new Pair<>("automatic_payment_methods[enabled]", "true"));


        request.makeRequestPuttingHeadersAndParams(headers, params);

        request.setOnResponseListener(new CRequest.OnResponseListener() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject object = new JSONObject(response);
                    Log.d("alksdjañlsf", object.getString("id"));
                    paymentIntent(context, key, object.getString("id"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String error) {

                Log.d("alksdjañlsf", "error Eph" + error);

                // customer.postValue("error");
            }
        });
    }


    public void paymentIntent(Context context, String customerId, String ephKey) {

        if (customerId.isEmpty())
            return;

        CRequest request = new CRequest(context, PAY_URL, 15);

        ArrayList<Pair<String, String>> headers = new ArrayList<>();
        ArrayList<Pair<String, String>> params = new ArrayList<>();

        //headers
        headers.add(new Pair<>("Authorization", "Bearer " + SECRET_KEY));
        //params
        params.add(new Pair<>("customer", customerId));
        params.add(new Pair<>("amount", "10000"));
        params.add(new Pair<>("currency", "eur"));
        //params.add(new Pair<>("automatic_payment_methods[enabled]", "true"));
        params.add(new Pair<>("payment_method_types[]", "sepa_debit"));
        //params.add(new Pair<>("payment_method_types[]", "bancontact"));
       // params.add(new Pair<>("payment_method_types[]", "ideal"));
        //params.add(new Pair<>("payment_method_types[]", "klarna"));
        //params.add(new Pair<>("payment_method_types[]", "sepa_debit"));


        request.makeRequestPuttingHeadersAndParams(headers, params);

        request.setOnResponseListener(new CRequest.OnResponseListener() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject object = new JSONObject(response);
                    Log.d("alksdjañlsf", object.getString("client_secret"));

                    ArrayList<String> list = new ArrayList<>();
                    list.add(customerId);
                    list.add(object.getString("client_secret"));
                    list.add(ephKey);
                    customer.postValue(list);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("alksdjañlsf", "json " + e.getMessage());
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
