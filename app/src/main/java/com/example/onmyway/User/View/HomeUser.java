package com.example.onmyway.User.View;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.onmyway.DB.CustomFirebase;
import com.example.onmyway.DB.UserDB;
import com.example.onmyway.R;
import com.example.onmyway.Utils.Constants;
import com.example.onmyway.general.Login;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class HomeUser extends AppCompatActivity implements OnMapReadyCallback {
    //loaction permission
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLocationPermissionGranted= false;




    private TextView fullnameV,addressV;
    private Button startB;
    private GoogleMap mMap;

    private SupportMapFragment mapFragment;
    private LatLng intentLatLng;
    private LatLng myLocation;
    private boolean gps_enabled=false;
    LocationManager locationManager;
    private MarkerOptions markerOptions;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_user);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        initMap();
        getLocationPermission();
        if(mLocationPermissionGranted)
        {
            gps_enabled= isGPSEnabled();
            if(gps_enabled)
            {
                getLastLocation();
            }

        }


        fullnameV=findViewById(R.id.fullname);
        fullnameV.setText( "Bienvenue Mr. "+new UserDB(this).getAllUsers().get(0).getfullName());

        addressV=findViewById(R.id.address);
        addressV.setVisibility(View.GONE);

        startB=findViewById(R.id.start);
        startB.setVisibility(View.GONE);


        
        



    }//end of create();

    private void moveCamera(LatLng latLng, float zoom) {


        markerOptions= new MarkerOptions();
        markerOptions.position(latLng);
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }//end moveCamera()



    private void initMap()
    {
        mapFragment.getMapAsync(this);
    }




    public void start(View view) {

        startActivity(new Intent(this, UserPosition.class));
        finish();


    }



    public void choseLocation(View view) {
        startActivity(new Intent(this, ChooseDestinationLocation.class));
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent intent=getIntent();
        if(intent.hasExtra("address") || intent.hasExtra("latlang"))
        {
            mapFragment.getMapAsync(this);
            startB.setVisibility(View.VISIBLE);
            addressV.setVisibility(View.VISIBLE);



            LatLng latLng=intent.getParcelableExtra("latlng");
            //if we wanna back the prev activity to show direction
            intentLatLng=latLng;
            if(!intent.getStringExtra("address").isEmpty())
                addressV.setText(intent.getStringExtra("address"));
            else
                addressV.setText("latitude "+latLng.latitude+",longitude "+latLng.longitude);

        }
    }//end of onResume

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap=googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);


        Intent intent=getIntent();
        if(intent.hasExtra("latlng"))
        {

            LatLng latLng=intent.getParcelableExtra("latlng");
            mMap.addMarker(new MarkerOptions().position(latLng));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,Constants.DEFAULT_ZOOM+2));

        }
        else
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(31.7218851,-11.6443955),Constants.DEFAULT_ZOOM-10));


    }



    //section for location services
    private boolean isGPSEnabled() {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager != null)
        {

            if (gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                return gps_enabled;
            new AlertDialog.Builder(this)
                    .setMessage("Activer GPS !.")
                    .setPositiveButton("Activer",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), Constants.GPS_REQUEST_CODE);
                                }
                            })
                    .setCancelable(false)
                    .show();
        }

        return false;

    }//end of GPSEnabled()

    //handle the result of startActivityForResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.GPS_REQUEST_CODE:
            {

                gps_enabled=isGPSEnabled();
                break;
            }
        }
    }//end of onActivityResult()


    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_FINE_LOCATION);
        }
    }//end of getLocationPermission

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        mLocationPermissionGranted = false;
        switch (requestCode)
        {
            case Constants.REQUEST_FINE_LOCATION:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    mLocationPermissionGranted = true;
                }
                else
                    getLocationPermission();
            }
        }

    }//end of onRequestPermissionsResult(...);

    private void getLastLocation()
    {
        mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location=task.getResult();

                if(location!=null)
                {
                    moveCamera(new LatLng(location.getLongitude(),location.getLatitude()),Constants.DEFAULT_ZOOM);

                }

            }


        });

    }

    public void signOut(View view) {
        CustomFirebase.getUserAuth().signOut();
        startActivity(new Intent(this, Login.class));
        finish();
    }
}
