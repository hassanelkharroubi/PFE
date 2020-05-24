package com.example.onmyway.GoogleDirection;

import com.google.android.gms.maps.model.LatLng;

public class ShowDirection {


    public static String getUrl(LatLng origin, LatLng dest, String mode, String api_key) {


        String ori = "origin=" + origin.latitude + "," + origin.longitude;
        String destination = "destination=" + dest.latitude + "," + dest.longitude;

        String direction = ori + "&" + destination;

        String key = "key=" + api_key;
        String modeDirection = "mode=" + mode;
        String parameters = direction + "&" + modeDirection;
        String outputFormat = "json";
        return "https://maps.googleapis.com/maps/api/directions/" + outputFormat + "?" + parameters + "&" + key;


    }//end of getUrl()

}
