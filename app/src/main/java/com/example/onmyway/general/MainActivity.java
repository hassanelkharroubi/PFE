package com.example.onmyway.general;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onmyway.R;

public class MainActivity extends AppCompatActivity {


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView img;
        img = findViewById(R.id.img);
         img.animate().alpha(4000).setDuration(0);
        Handler handler = new Handler();
         handler.postDelayed(new Runnable() {
             @Override
             public void run()
             {
                 Intent dsp = new Intent(MainActivity.this,Login.class);
                 startActivity(dsp);
                 finish();
             }
         }, 1000);
    }
}
