package com.example.onmyway.Utils;

import android.app.ProgressDialog;
import android.content.Context;

public class DialogMsg {

    private ProgressDialog progressDialog;

    public void attendre(Context context,String title,String msg)
    {
        progressDialog=new ProgressDialog(context);
        progressDialog.setTitle(title);
        progressDialog.setMessage(msg+" ....");
        progressDialog.show();
    }
    public void hideDialog()
    {
        if(progressDialog!=null)
            progressDialog.dismiss();
    }
}
