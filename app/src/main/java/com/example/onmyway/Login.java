package com.example.onmyway;



import android.app.Dialog;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;

import android.os.Message;
import android.text.TextUtils;

import android.util.Log;
import android.util.Patterns;

import android.view.View;
import android.widget.EditText;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;

import com.example.onmyway.DB.Firebase;
import com.example.onmyway.UserInfo.HomeUser;
import com.example.onmyway.administrateur.Administrateur;
import com.example.onmyway.administrateur.Home;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import dmax.dialog.SpotsDialog;


public class Login extends AppCompatActivity {

    private Toolbar toolbar;
    //ProgressDialog pd;
    //inherited from ProgressDialog
    SpotsDialog pd;

    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private static final String TAG="login";

    private String email;
    private String password;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ref= FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.UserData));
        if(checkGooglePlayServices())
        {

            finish();

        }

        if (user != null) {
            Intent intent=new Intent(Login.this, Home.class);
            intent.putExtra("type","administrateur");
            startActivity(intent);


        }







    //get toolbar_layout
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //on doit programmer le bach home arrow on toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        editTextEmail=findViewById(R.id.email);
        editTextPassword=findViewById(R.id.password);





    }

    private boolean checkGooglePlayServices() {

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int status = apiAvailability.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            Log.e(TAG, apiAvailability.getErrorString(status));

            // ask user to update google play services.
            Dialog dialog = apiAvailability.getErrorDialog(this,100,1);
            if(dialog!=null)
            {

                dialog.setTitle("Please update your google play services");
                dialog.setCancelable(true);
                dialog.show();
            }

            return false;
        } else {
            Log.i(TAG, apiAvailability.getErrorString(status)+"hello");
            // google play services is updated.
            //your code goes here...
            return true;
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        if(requestCode==1)
        {
            Log.i(TAG,"thus is your request code "+1);
        }
    }

    public void login(View view) {



        email=editTextEmail.getText().toString();
        password=editTextPassword.getText().toString();
        if(!isEmail(email) || password.isEmpty())
        {
            msg("veuillez valider soit email soit le mot de passe");
            return;


        }

        //we have to hide this sportdialog when we don't have connection
        new SpotsDialog.Builder()
                .setContext(this)
                .setTheme(R.style.CustomPD)
                .build()
                .show();


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {


                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            if(email.equals(Administrateur.email))
                            {
                                Intent intent=new Intent(Login.this,Home.class);
                                startActivity(intent);
                                finish();

                            }
                            else
                            {

                                Query query= ref.orderByKey().equalTo(mAuth.getUid());
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChildren())
                                        {

                                            Intent intent=new Intent(Login.this, HomeUser.class);
                                            intent.putExtra("type","user");
                                            intent.putExtra("email",email);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else
                                        {
                                            Toast.makeText(Login.this, "cet utilisateur n'existe pas", Toast.LENGTH_SHORT).show();
                                            mAuth.signOut();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }

                        }
                        else
                            {
                                msg("le mot de passe ou email est incorrect ");

                          }

                    }
                });


    }

    //fonction de verification email
    public static boolean isEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public void msg(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();


    }

    //check google play services
    private boolean checkPlayServices()
    {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (apiAvailability.isUserResolvableError(resultCode))
            {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else
                {

                Toast.makeText(this, "This device is not supported.", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }






}

