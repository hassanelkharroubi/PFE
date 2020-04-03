package com.example.onmyway.UserInfo;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.onmyway.DB.LocationDB;
import com.example.onmyway.connection.Internet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SaveOfflineLocation extends  Thread {

private ListGeoPoints listGeoPoints;
private DatabaseReference mDatabase;
private Internet internet;
private LocationDB locationDB;


public SaveOfflineLocation(ArrayList<GeoPoint> geoPointsList,DatabaseReference mDatabase,Internet internet,LocationDB locationDB,Boolean connected)
{
    Date date= Calendar.getInstance().getTime();
    listGeoPoints=new ListGeoPoints(date.getTime(),geoPointsList);
    this.mDatabase=mDatabase;
    this.internet=internet;
    this.locationDB=locationDB;
    connected=internet.conected();

}




    @Override
    public void run() {
        super.run();
        saveLocation();
    }

    public void saveLocation()
    {


        mDatabase.child("OfflineUserLocation").child(locationDB.getUserCIN()).child(String.valueOf(listGeoPoints.getTime())).setValue(listGeoPoints)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        //sucess so we have to delete all locations from local data base
                        locationDB.deleteLocations();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

}
