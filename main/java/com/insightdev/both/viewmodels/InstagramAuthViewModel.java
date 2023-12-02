package com.insightdev.both.viewmodels;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.insightdev.both.CRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InstagramAuthViewModel extends ViewModel {

    private MutableLiveData<String> resp = new MutableLiveData<>();

    final static String INSTAGRAM_URL = "https://api.instagram.com/oauth/access_token";

    final static String INSTAGRAM_GRAPH_URL = "https://graph.instagram.com/";

    final static String CLIENT_ID = "548723226855110";

    final static String CLIENT_SECRET = "6d268297358fc687840cb938c1568715";

    final static String REDIRECT_URI = "https://both.social/";

    public LiveData<String> getResponseLiveData() {

        return resp;
    }


    public void getAccesToken(Context context, String code) {

        CRequest request = new CRequest(context, INSTAGRAM_URL, 15);
        ArrayList<Pair<String, String>> pairs = new ArrayList<>();


        pairs.add(new Pair<>("client_id", CLIENT_ID));
        pairs.add(new Pair<>("client_secret", CLIENT_SECRET));
        pairs.add(new Pair<>("grant_type", "authorization_code"));
        pairs.add(new Pair<>("redirect_uri", REDIRECT_URI));
        pairs.add(new Pair<>("code", code));


        request.makeRequest(pairs);
        request.setOnResponseListener(new CRequest.OnResponseListener() {
            @Override
            public void onResponse(String response) {


                try {
                    JSONObject object = new JSONObject(response);

                    String access_token = object.getString("access_token");

                    String user_id = object.getString("user_id");

                    getUsername(context, access_token, user_id);

                } catch (JSONException e) {
                    e.printStackTrace();
                    resp.postValue("");
                }

            }

            @Override
            public void onError(String error) {
                resp.postValue("");
                Log.d("alskdamdñasdnak", "error " + error);

            }
        });
    }

    public void getUsername(Context context, String access_token, String user_id) {

        CRequest request = new CRequest(context, INSTAGRAM_GRAPH_URL + user_id+"?fields=id,username&access_token="+access_token, 15);


        request.makeGETRequest();
        request.setOnResponseListener(new CRequest.OnResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.d("alskdamdñasdnak", "resp " + response);

                try {
                    JSONObject object = new JSONObject(response);

                    resp.postValue(object.getString("username"));

                } catch (JSONException e) {
                    e.printStackTrace();
                    resp.postValue("");
                }

            }

            @Override
            public void onError(String error) {

                resp.postValue("");
                Log.d("alskdamdñasdnak", "error " + error);

            }
        });
    }

}
