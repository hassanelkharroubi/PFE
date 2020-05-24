package com.example.onmyway.Service;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class GeoCoding extends AsyncTask<Object, Void, Object> {


    GeoCodingDoneListener geoCodingDoneListener;
    private Context context;

    public GeoCoding(Context context) {
        this.context = context;
        this.geoCodingDoneListener = (GeoCodingDoneListener) context;
    }

    @Override
    protected Object doInBackground(Object... objects) {
        Geocoder geocoder = new Geocoder(context);
        List<Address> addressList;
        if (objects[0] instanceof String) {
            try {
                addressList = geocoder.getFromLocationName((String) objects[0], 1);
                Log.d("doin", objects[0].toString());
                if (addressList.size() > 0) {
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    return latLng;
                }
                return null;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }
        if (objects[0] instanceof LatLng) {
            Log.d("doin", objects[0].toString());
            LatLng latLng = (LatLng) objects[0];
            try {

                addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (addressList.size() > 0) {
                    String address = addressList.get(0).getAddressLine(0);
                    return address;
                }
                return latLng;

            } catch (IOException e) {
                e.printStackTrace();
                return latLng;

            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {

        geoCodingDoneListener.geoCodingDone(o);

        super.onPostExecute(o);
    }
}
