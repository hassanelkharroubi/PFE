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


import com.example.onmyway.DB.CustomFirebase;
import com.example.onmyway.ListAllUser;
import com.example.onmyway.Login;
import com.example.onmyway.R;
import com.example.onmyway.connection.Internet;


public class Home extends AppCompatActivity {

    private static final String TAG = "Home";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        TextView textView1 = findViewById(R.id.wlcm);
        //get toolbar_layout
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Accueil");
        textView1.setText(Administrateur.fullame);

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

    public void signOut(View view) {
        CustomFirebase.getUserAuth().signOut();
        startActivity(new Intent(this, Login.class));
        finish();
    }


    public void transporter(View view) {
        startActivity(new Intent(Home.this, ListAllUser.class));
    }
}
