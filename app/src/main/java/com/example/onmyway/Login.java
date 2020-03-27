package com.example.onmyway;



import android.content.Intent;

import android.os.Build;
import android.os.Bundle;

import android.text.TextUtils;

import android.util.Patterns;

import android.view.View;
import android.widget.EditText;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;

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

import dmax.dialog.SpotsDialog;


public class Login extends AppCompatActivity {

    private Toolbar toolbar;
    //ProgressDialog pd;
    //inherited from ProgressDialog
    SpotsDialog pd;

    private FirebaseAuth mAuth;
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
        if(!checkPlayServices())
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
                                intent.putExtra("type","administrateur");
                                startActivity(intent);
                                finish();

                            }
                            else
                            {
                                Intent intent=new Intent(Login.this, HomeUser.class);
                                intent.putExtra("type","user");
                                intent.putExtra("email",email);
                                startActivity(intent);
                                finish();

                            }



                          //  FirebaseUser user = mAuth.getCurrentUser();
                          //  updateUI(user);
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

