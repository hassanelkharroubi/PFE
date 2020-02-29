package com.example.onmyway;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toolbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Handler handler;
    Runnable runnable;
    ImageView img;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        img = findViewById(R.id.img);
         img.animate().alpha(4000).setDuration(0);

         handler = new Handler();
         handler.postDelayed(new Runnable() {
             @Override
             public void run()
             {
                 Intent dsp = new Intent(MainActivity.this,Login.class);
                 startActivity(dsp);
                 finish();
             }
                                             },1000);
    }
}
