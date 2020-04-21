package com.example.onmyway.connection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Internet extends Thread{

    String TAG="CONNECTION";
    private boolean connected=false;
    private Context context;

    public Internet(Context context)
    {
        this.context=context;
    }

    @Override
    public void run() {
        super.run();
        this.connected=hasInternetAccess();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private boolean hasInternetAccess() {
        if (isNetworkAvailable())
        {
            try {
                HttpURLConnection urlc = (HttpURLConnection)
                        (new URL("http://clients3.google.com/generate_204")
                                .openConnection());
                urlc.setRequestProperty("User-Agent", "Android");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 204 &&
                        urlc.getContentLength() == 0);
            } catch (IOException e)
            {
                Log.d(TAG, "Error checking internet connection");
                return false;
            }
        }
        else
              Log.d(TAG, "No network available!");

        return false;
    }


    public  Boolean connected()
    {
        start();
        return this.connected;
    }
}
