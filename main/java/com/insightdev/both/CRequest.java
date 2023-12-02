package com.insightdev.both;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CRequest {
    private final Context context;
    private final String url;
    private final int timeMillis;

    public OnResponseListener listener;

    public interface OnResponseListener {
        void onResponse(String response);

        void onError(String error);

    }

    public void setOnResponseListener(OnResponseListener listener) {
        this.listener = listener;
    }

    public CRequest(Context context, String url, int timeSec) {
        this.context = context;
        this.url = url;
        this.timeMillis = timeSec * 1000;
    }


    public void makeRequest(final ArrayList<Pair<String, String>> args) {
        StringRequest string_request = new StringRequest(com.android.volley.Request.Method.POST, url, response -> {
            listener.onResponse(response);
        }, error -> {
            listener.onError(error.toString());

            if (error == null || error.networkResponse == null) {
                return;
            }

            String body = null;
            //get status code here
            final String statusCode = String.valueOf(error.networkResponse.statusCode);
            //get response body and parse with appropriate encoding
            try {
                body = new String(error.networkResponse.data, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // exception
            }

            Log.d("anslkdadafa", body);
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parameters = new HashMap<>();
                for (int i = 0; i < args.size(); i++) {
                    Pair<String, String> pair = args.get(i);
                    parameters.put(pair.first, pair.second);
                }

                return parameters;
            }
        };

        string_request.setRetryPolicy(new DefaultRetryPolicy(
                timeMillis,
                0,
                0));
        RequestQueue request_queue = VolleySingleton.getInstance(context).getRequestQueue();
        request_queue.add(string_request);

    }


    public void makeRequestPuttingHeaders(final ArrayList<Pair<String, String>> args) {
        StringRequest string_request = new StringRequest(com.android.volley.Request.Method.POST, url, response -> {
            listener.onResponse(response);
        }, error -> {
            listener.onError(error.toString());
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                for (int i = 0; i < args.size(); i++) {
                    Pair<String, String> pair = args.get(i);
                    parameters.put(pair.first, pair.second);
                }
                return parameters;
            }
        };

        string_request.setRetryPolicy(new DefaultRetryPolicy(
                timeMillis,
                0,
                0));
        RequestQueue request_queue = VolleySingleton.getInstance(context).getRequestQueue();
        request_queue.add(string_request);

    }

    public void makeRequestPuttingHeadersAndParams(final ArrayList<Pair<String, String>> headers, final ArrayList<Pair<String, String>> params) {
        StringRequest string_request = new StringRequest(com.android.volley.Request.Method.POST, url, response -> {
            listener.onResponse(response);
        }, error -> {
            listener.onError(error.toString());

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                for (int i = 0; i < headers.size(); i++) {
                    Pair<String, String> pair = headers.get(i);
                    parameters.put(pair.first, pair.second);
                }
                return parameters;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                for (int i = 0; i < params.size(); i++) {
                    Pair<String, String> pair = params.get(i);
                    parameters.put(pair.first, pair.second);
                }
                return parameters;
            }
        };

        string_request.setRetryPolicy(new DefaultRetryPolicy(
                timeMillis,
                0,
                0));
        RequestQueue request_queue = VolleySingleton.getInstance(context).getRequestQueue();
        request_queue.add(string_request);

    }

    public void makeGETRequest() {

        StringRequest string_request = new StringRequest(Request.Method.GET, url, response -> {
            listener.onResponse(response);
        }, error -> {
            listener.onError(error.toString());

        }) {
        };

        string_request.setRetryPolicy(new DefaultRetryPolicy(
                timeMillis,
                0,
                0));
        RequestQueue request_queue = VolleySingleton.getInstance(context).getRequestQueue();
        request_queue.add(string_request);

    }

    public String getUrl() {
        return url;
    }

    public int getTimeMillis() {
        return timeMillis;
    }


}