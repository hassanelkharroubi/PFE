package com.example.onmyway.Service;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.onmyway.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class GPSUpdate{


    private Context context;
        //for google map
        private GoogleMap mMap;

        private MarkerOptions markerOptions;
        //loaction permission
        private LocationRequest locationRequest;
        private LocationCallback locationCallback;
        private FusedLocationProviderClient mFusedLocationProviderClient;
        private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION= 200;

        private static final int GPS_REQUEST_CODE = 100;
        private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
        private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
        private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
        private static final float DEFAULT_ZOOM = 15f;


        public GPSUpdate(Context context  ,GoogleMap mMap ) {
            this.context=context;
            this.mMap=mMap;
            locationRequest();
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        }//end of constrcutor


        private void moveCamera(LatLng latLng, float zoom, GoogleMap mMap) {

            if(markerOptions!=null)
                markerOptions= new MarkerOptions();
            markerOptions.position(latLng);
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }//end moveCamera()






        private void getLocationPermission(Boolean mLocationPermissionGranted) {

            if (ContextCompat.checkSelfPermission(context.getApplicationContext(), Constants.FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                mLocationPermissionGranted = true;
            } else {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Constants.FINE_LOCATION}, Constants.REQUEST_FINE_LOCATION);
            }
        }//end of getLocationPermission





        //location request class
        private void locationRequest()
        {
            locationRequest = new LocationRequest();
            locationRequest.setInterval(Constants.UPDATE_INTERVAL); //use a value fo about 10 to 15s for a real app
            locationRequest.setFastestInterval(Constants.FASTEST_INTERVAL);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }//end of locationRequest();

        public void startLocationUpdates(LocationCallback locationCallback) {

            this.locationCallback=locationCallback;

            try {
                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
            }
            catch (NullPointerException e)
            {
                e.printStackTrace();
            }
        }//end of startLocationUpdates()
        public void stopLocationUpdates() {
            mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }//end of stopLocationUpdates()

        //for background services
        public void startService() {

            Intent serviceIntent = new Intent(context, MyBackgroundLocationService.class);

            ContextCompat.startForegroundService(context, serviceIntent);
        }//end of startService()

        public void stopService() {
            Intent serviceIntent = new Intent(context, MyBackgroundLocationService.class);
            context.stopService(serviceIntent);

        }//end of stopService()




}


