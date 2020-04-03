package com.example.onmyway.administrateur;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.onmyway.DB.LocationDB;
import com.example.onmyway.ListAllUser;
import com.example.onmyway.R;
import com.example.onmyway.connection.Internet;


public class Home extends AppCompatActivity {

    private static final String TAG = "Home";
    private TextView textView1;
    private Toolbar toolbar;

    private LocationDB locationDB;

    private Internet internet;
   // @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        internet=new Internet(this);

        textView1=findViewById(R.id.wlcm);

        //get toolbar_layout
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textView1.setText(Administrateur.fullame);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.toolbar,menu);



        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.ajouter)
            startActivity(new Intent(this, RegisterActivity.class));

        if(item.getItemId()==android.R.id.home)
        {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory( Intent.CATEGORY_HOME );
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
        }

        if(item.getItemId()==R.id.suprimer)
            startActivity(new Intent(this, SupprimerUser.class));

        if(item.getItemId()==R.id.chercher)
            startActivity(new Intent(this, MapsActivity.class));
        if(item.getItemId()==R.id.enligne)
            startActivity(new Intent(Home.this, ListAllUser.class));


        return super.onOptionsItemSelected(item);
    }


    public void enableGPS(View view) {

        locationEnabled();

    }


    private void locationEnabled () {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE ) ;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER ) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager. NETWORK_PROVIDER ) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        if (!gps_enabled && !network_enabled) {
            new AlertDialog.Builder(Home.this )
                    .setMessage( "GPS Enable" )
                    .setPositiveButton( "Settings" , new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick (DialogInterface paramDialogInterface , int paramInt) {
                                    startActivity( new Intent(Settings. ACTION_LOCATION_SOURCE_SETTINGS )) ;
                                }
                            })
                    .setNegativeButton( "Cancel" , null )
                    .show() ;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter intentFilter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
//for brodacastReceveir
      //  registerReceiver(connectivity,intentFilter);

    }



    @Override
    protected void onStop() {
        super.onStop();
//for brodacastReceveir
       // unregisterReceiver(connectivity);
    }

    public void ajouter(View view) {
        startActivity(new Intent(this,RegisterActivity.class));
    }

    public void supprimer(View view) {
        startActivity(new Intent(this,Chercher.class));

    }
    public void chercher(View view) {

        startActivity(new Intent(this,Chercher.class));

    }



}
