package com.example.onmyway;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class Home extends AppCompatActivity {

    private static final String TAG = "Home";
    private TextView textView1;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        textView1=findViewById(R.id.textView1);

        //get toolbar_layout
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        textView1.setText("Welcome MR . "+Administrateur.email);


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
            startActivity(new Intent(this,RegisterActivity.class));
        if(item.getItemId()==R.id.suprimer)
            startActivity(new Intent(this,SupprimerUser.class));


        return super.onOptionsItemSelected(item);
    }
}
