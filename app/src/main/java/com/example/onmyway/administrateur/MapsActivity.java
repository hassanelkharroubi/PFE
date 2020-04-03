package com.example.onmyway.administrateur;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.onmyway.DB.LocationDB;
import com.example.onmyway.ListAllUser;
import com.example.onmyway.R;
import com.example.onmyway.UserInfo.GeoPoint;
import com.example.onmyway.UserInfo.SaveOfflineLocation;
import com.example.onmyway.UserInfo.User;
import com.example.onmyway.UserInfo.UserLocation;
import com.example.onmyway.connection.Internet;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION= 200;

    private static final int GPS_REQUEST_CODE = 100;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 10f;
    LocationManager locationManager;

    private GoogleMap mMap;

    private MarkerOptions markerOptions;
    private SupportMapFragment mapFragment;
    //vars
    private Boolean mLocationPermissionGranted= false;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location  mLastKnownLocation;
    private Toolbar toolbar;
    //check if gps provider ad network provider are enabeld
    private boolean gps_enabled = false;

    //check internet connection we use class Internet
    private Internet internet;
    private Boolean connected = false;

    private GeoPoint geoPoint;
    private UserLocation userLocation;
    private LocationDB locationDB;
    //check if user is not moving
    //reduce the min time access to firebase
    private GeoPoint prevGepoint;

    //***********for dataBase fire base and dataBase authentification***************
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

//****************************************************here start methods*********************************************************


    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest();
        //pour sauvgarder des point de  location
        geoPoint=new GeoPoint();
        prevGepoint=new GeoPoint();
        userLocation=new UserLocation();


        //check conection
        internet=new Internet(this);

        connected=internet.conected();
        //intantiate db for LocationDB
        locationDB=new LocationDB(this);

        mDatabase= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        // Construct a FusedLocationProviderClient.

        //ask for permissions FINE_LOCATION;
        getLocationPermission();
        if(mLocationPermissionGranted)
        {
            //enable GPS
            if(GPSEnabled())
            {
                initMap();

            }
        }

       locationCallback = new LocationCallback()
        {
            @Override
            public void onLocationResult(LocationResult locationResult)
            {

                super.onLocationResult(locationResult);
                //test if we have internet
                connected=internet.conected();
                for (Location location : locationResult.getLocations())
                {


                        if (location != null ) {
                            msg(location.getLongitude() + " " + location.getLatitude());
                            moveCamera(new LatLng(location.getLongitude(),location.getLatitude()),DEFAULT_ZOOM);


                            geoPoint.setLatitude(location.getLatitude());
                            geoPoint.setLongitude(location.getLongitude());



                            if(!connected)
                            {
                                msg("Vous etes n'est pas connecte au internet");

                                if( !geoPoint.equals(prevGepoint))
                                {
                                    locationDB.addUserLocation(new UserLocation(geoPoint,new User("hassan","hassan@gmail.com","hassan123","zt265568")));
                                    prevGepoint=geoPoint;
                                    connected=internet.conected();
                                }

                            }
                            else
                            {
                                ArrayList<GeoPoint> geoPointsList= locationDB.getUserGeoPoints();

                                if(geoPointsList.size()>0)
                                {

                                    SaveOfflineLocation saveOfflineLocation=new SaveOfflineLocation(geoPointsList,mDatabase,internet,locationDB,connected);
                                    saveOfflineLocation.start();


/*
                                    mDatabase.child("OfflineUserLocation").child(locationDB.getUserCIN()).child(String.valueOf(date.getTime())).setValue(geoPointsList)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    msg("geoPoint was saved with succus !" );
                                                    //sucess so we have to delete all locations from local data base
                                                    locationDB.deleteLocations();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    msg("we ca't save ur geoPoint !"+e.getMessage());
                                                    connected=internet.conected();
                                                }
                                            });
*/

                                }

                            }


                            new Thread()
                            {
                                @Override
                                public void run() {
                                    super.run();
                                    mDatabase.child("OnlineUserLocation").child(currentUser.getUid()).setValue(geoPoint)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    msg("geoPoint was saved with succus !" );
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    msg("we ca't save ur geoPoint !"+e.getMessage());
                                                }
                                            });
                                }
                            }.start();


                        }

                }
            }
        };

    }

    //init googme map
    private void initMap() {

        markerOptions = new MarkerOptions();
        mapFragment.getMapAsync(this);

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getDeviceLocation();
        updateLocationUI();

    }

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
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        mLocationPermissionGranted = false;
        switch (requestCode)
        {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                msg("yes");
              mMap.setMyLocationEnabled(true);
            //   mMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
            else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {

                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {

                            mLastKnownLocation = (Location) task.getResult();

                            if(mLastKnownLocation!=null)
                            {
                                // Set the map's camera position to the current location of the device.
                               moveCamera(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude()),DEFAULT_ZOOM);
                              //  startLocationUpdates();
                            }
                            else
                            {
                            //    after disabled GPS and enabe and again mLastKnowLocation is null so we have to call initMap again
                                initMap();
                            }


                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            msg("position is null");

                      //     LatLng mDefaultLocation=new LatLng(20, -5.0717805);
                         //   mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                          //  mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    //check if gps is enabled or not without asking for  enable it
    private boolean locationManagerEnabled() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager != null) {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            return gps_enabled;
        }
        return gps_enabled;
    }

    //check if GPS is enabled

    private boolean GPSEnabled() {
        if (locationManagerEnabled())
            return true;
        new AlertDialog.Builder(MapsActivity.this)
                .setMessage("Activer GPS !.")
                .setPositiveButton("Activer",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), GPS_REQUEST_CODE);
                            }
                        })
                .setCancelable(false)
                .show();
        return false;

    }


 //handle the result of startActivityForResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GPS_REQUEST_CODE:
                {
                if(locationManagerEnabled())
                    initMap();
                break;
            }
        }
    }


    //move camera to right place

    private void moveCamera(LatLng latLng, float zoom) {

            markerOptions=new MarkerOptions();
        markerOptions.position(latLng);
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    //location request class
    private void locationRequest()
    {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(15000); //use a value fo about 10 to 15s for a real app
        locationRequest.setFastestInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }





    @Override
    protected void onResume() {
        super.onResume();
        //call for check internet
        connected=internet.conected();
        startLocationUpdates();
    }


    private void startLocationUpdates() {

        try {
            mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    public void stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }



    //pour afficher le menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);
        //  menu.removeItem(R.id.ajouter);

        return super.onCreateOptionsMenu(menu);
    }
    //pour selection un element de toolbar

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            onBackPressed();

        if (item.getItemId() == R.id.ajouter)
            startActivity(new Intent(this, RegisterActivity.class));


        if (item.getItemId() == R.id.suprimer)
            startActivity(new Intent(this, SupprimerUser.class));

        if (item.getItemId() == R.id.chercher)
            startActivity(new Intent(this, MapsActivity.class));

        if (item.getItemId() == R.id.enligne)
            startActivity(new Intent(this, ListAllUser.class));


        return super.onOptionsItemSelected(item);
    }

    private void msg(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }


}
