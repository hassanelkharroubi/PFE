package com.example.onmyway;

import android.app.ProgressDialog;
import android.content.Context;

public class DialogMsg {

    private ProgressDialog progressDialog;
    private Context context;

    public void attendre(Context context,String title,String msg)
    {
        this.context=context;
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
