package com.example.onmyway.User.View;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onmyway.DB.DestinationDB;
import com.example.onmyway.GoogleDirection.FetchURL;
import com.example.onmyway.GoogleDirection.TaskLoadedCallback;
import com.example.onmyway.R;
import com.example.onmyway.Utils.Constants;
import com.example.onmyway.Utils.CustomToast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;

public class ChooseDestinationLocation extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private static final String TAG = "chooseLocation";






    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private MarkerOptions locationMarker;
    private Marker marker;
    Polyline currentPolyline;

    //for database
    private DestinationDB destinationDB;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_destination_location);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        locationMarker=new MarkerOptions();
        mapFragment.getMapAsync(this);

        destinationDB=new DestinationDB(this);




    }//end of onCreate()

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        LatLng maroc=new LatLng(31.7218851,-11.6443955);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(maroc, Constants.DEFAULT_ZOOM - 10));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                LatLng destination=destinationDB.getDestination();
                if(destination==null)
                {
                    Log.d(TAG,"destination is null");
                }
                else
                {
                    destinationDB.deleteDestination();
                }
                destinationDB.addDestination(latLng);

                if(marker!=null)
                    marker.remove();
                locationMarker.position(latLng);
                marker=  mMap.addMarker(locationMarker);
                Geocoder geocoder=new Geocoder(ChooseDestinationLocation.this);
                try {

                    String address=geocoder.getFromLocation(latLng.latitude,latLng.longitude,1).get(0).getAddressLine(0);


                    confirm(address,latLng,address);


                } catch (IOException e) {
                    e.printStackTrace();

                    sendIntent(latLng,"click commencer ou choisir une destination");
                }


            }
        });
    }//end of onMapReady()

    public static String getUrl(LatLng origin,LatLng dest,String mode,String api_key) {


        String ori="origin="+origin.latitude+","+origin.longitude;
        String destination="destination="+dest.latitude+","+dest.longitude;

        String direction=ori+"&"+destination;

        String key="key="+api_key;
        String modeDirection="mode="+mode;
        String parameters=direction+"&"+modeDirection;
        String outputFormat="json";
        Log.i(TAG,"inside getUrl");
       return  "https://maps.googleapis.com/maps/api/directions/"+outputFormat+"?"+parameters+"&"+key;


    }//end of getUrl()

    private void confirm(String msg,final LatLng latLng,final String address)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                sendIntent(latLng,address);
                finish();

            }
        });
        builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //zoom in for more places
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,Constants.DEFAULT_ZOOM));

            }
        });


        builder.setMessage("Veuillez confimer le choix de destination "+msg).create().show();


    }//end of confirm()

    private void sendIntent(LatLng latLng,String address)
    {
        Intent intent=new Intent(this,HomeUser.class);
        intent.putExtra("latlng",latLng);
        intent.putExtra("address",address);
        startActivity(intent);
    }

    @Override
    public void onTaskDone(String distance,String duration,Object... values) {

        Log.i(TAG,"inside inteface");

        if(currentPolyline!=null)
            currentPolyline.remove();
       currentPolyline= mMap.addPolyline((PolylineOptions) values[0]);
    }//end of onTaskDone();

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent=getIntent();
        if(intent.hasExtra(UserPosition.TAG))
        {
            LatLng destination=destinationDB.getDestination();
            LatLng origin=intent.getParcelableExtra("origin");
            Log.d(TAG,"hello ="+destination.toString()+" "+origin.toString());
            if(origin!=null)
            {
                String url=getUrl(origin,destination,"driving",getString(R.string.google_map_api));
                Log.d(TAG,url);
                new FetchURL(this).execute(url,"driving");
            }
            else
                CustomToast.toast("pas de direction ",this);

        }


    }//end of onResume()





}
