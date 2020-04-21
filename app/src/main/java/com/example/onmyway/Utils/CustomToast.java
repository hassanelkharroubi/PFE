package com.example.onmyway.Utils;


import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onmyway.R;

public class CustomToast {

    public static void toast(String msg, Context context)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
       // LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        View toastLayout=layoutInflater.inflate(R.layout.toast,null);
        TextView textView= toastLayout.findViewById(R.id.toastMsg);
        textView.setText(msg+" ");
        Toast toast=new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastLayout);
        toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, -20);
        toast.show();
    }
}
