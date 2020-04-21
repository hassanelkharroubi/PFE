package com.example.onmyway.administrateur.View;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.onmyway.DB.CustomFirebase;
import com.example.onmyway.R;
import com.example.onmyway.User.Models.GeoPoint;
import com.example.onmyway.User.Models.User;
import com.example.onmyway.Utils.Constants;
import com.example.onmyway.Utils.CustomToast;
import com.example.onmyway.Utils.DialogMsg;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

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
    //check if gps provider ad network provider are enabeld
    private boolean gps_enabled = false;
    //for dialog msg
    private DialogMsg dialogMsg;


    //***********for dataBase fire base and dataBase authentification***************
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private String userAuthKey=null;
    private User user;
    private GeoPoint geoPoint;

//****************************************************here start methods*********************************************************


    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("position de chauffeur");
        dialogMsg=new DialogMsg();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        initMap();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest();

        mDatabase= FirebaseDatabase.getInstance().getReference();
        user =new User();
        geoPoint=new GeoPoint();

        mAuth=FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //test for intent is gaming from Chercher(show user om map)

        Intent intent=getIntent();
        if(intent.hasExtra("id"))
        {
            userAuthKey=intent.getStringExtra("id");

            Log.d(TAG,userAuthKey);
            showUserOnMap();
        }

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

                for (Location location : locationResult.getLocations())
                {

                        if (location != null ) {

                        }

                }
            }
        };//end location callback

    }//end of onCreate()

    private void showUserOnMap() {

        Log.d(TAG,"inside showUserOnMap+"+userAuthKey);

        dialogMsg.attendre(this,"recherche","en train de chercher position de chauffeur");

        CustomFirebase.getDataRefLevel1(getString(R.string.OnlineUserLocation))
                .child(userAuthKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChildren())
                {
                    dialogMsg.hideDialog();
                    CustomToast.toast("ce chauffeur n'pas commencer le travail",MapsActivity.this);
                    return;
                }
                dialogMsg.hideDialog();
                Log.d(TAG,"children ="+dataSnapshot.toString());
                geoPoint=dataSnapshot.getValue(GeoPoint.class);


                    if (geoPoint != null) {
                        CustomFirebase.getDataRefLevel1(getString(R.string.UserData)).child(userAuthKey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                user=dataSnapshot.getValue(User.class);
                                moveCamera(new LatLng(geoPoint.getLongitude(),geoPoint.getLatitude()), Constants.DEFAULT_ZOOM);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                moveCamera(new LatLng(geoPoint.getLongitude(),geoPoint.getLatitude()), Constants.DEFAULT_ZOOM);

                            }
                        });

                    }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                CustomToast.toast("veuillez verfier votre connection ",MapsActivity.this);
                dialogMsg.hideDialog();

            }
        });//end of  mDatabase.child(userAuthKey)



    }//end of showOmMap()

    //init googme map
    private void initMap() {

        markerOptions = new MarkerOptions();
        mapFragment.getMapAsync(this);

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
      //  getDeviceLocation();

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

    }



    private void getDeviceLocation() {
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

        Marker marker= mMap.addMarker(markerOptions);
        if(user!=null)
        {
            marker.setTitle(user.getfullName());
            marker.setSnippet("speed: "+geoPoint.getSpeed());

        }


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
        Log.d(TAG,"Onresume");
        if(dialogMsg!=null)
         dialogMsg.hideDialog();

       // startLocationUpdates();
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
       // stopLocationUpdates();
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
            startActivity(new Intent(this, Chercher.class));

        if (item.getItemId() == R.id.chercher)
            startActivity(new Intent(this, Chercher.class));

        if (item.getItemId() == R.id.enligne)
        {
            showAllUsers();

        }


        return super.onOptionsItemSelected(item);
    }

    private void showAllUsers() {

        CustomFirebase.getDataRefLevel1(getString(R.string.UserData))
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren())
                {
                    for (DataSnapshot userData:dataSnapshot.getChildren())
                    {
                        Log.d(TAG,userData.toString());
                        CustomFirebase.getDataRefLevel1(getString(R.string.OnlineUserLocation))
                                .child(userData.getKey())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChildren())
                                        {
                                            geoPoint=dataSnapshot.getValue(GeoPoint.class);
                                            user=userData.getValue(User.class);
                                            moveCamera(new LatLng(geoPoint.getLongitude(),geoPoint.getLatitude()),Constants.DEFAULT_ZOOM-4);
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


}
