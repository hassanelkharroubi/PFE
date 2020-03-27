package com.example.onmyway.connection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.widget.Toast;

public class Connectivity extends BroadcastReceiver {

   private Context context;
    private boolean notConnect;

    public Connectivity()
    {
        notConnect=false;
    }

    public  boolean isConnect()
    {
/*        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null)
            return networkInfo.isConnected();*/
        return notConnect;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction()))

            notConnect=intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,false);

    }
}
