package com.insightdev.both;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.widget.Toast;

public class NetworkReceiver {
    private Context context;
    private ConnectivityManager connectivityManager;
    public OnConnectivityListener listener;
    public interface OnConnectivityListener{
        void Available(Network network);
        void Lost(Network network);
    }
    public void setOnConnectivityListener(OnConnectivityListener listener) {
        this.listener = listener;
    }

    public NetworkReceiver(Context context) {
        setContext(context);
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }
    public void register(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        } else {
            NetworkRequest request = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build();
            connectivityManager.registerNetworkCallback(request, networkCallback);
        }
    }

    public void unregister(){
        connectivityManager.unregisterNetworkCallback(networkCallback);
    }

    public ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            listener.Available(network);

        }

        @Override
        public void onLost(Network network) {
            if (context != null) {
                Toast.makeText(context, "Sin Conexión", Toast.LENGTH_LONG).show();
            }
            listener.Lost(network);
        }
    };

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
