package com.example.onmyway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.onmyway.UserInfo.User;
import com.example.onmyway.administrateur.MapsActivity;
import com.example.onmyway.administrateur.RegisterActivity;
import com.example.onmyway.administrateur.SupprimerUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

public class ListAllUser extends AppCompatActivity {



    private ProgressDialog progressDialog;

    private RecyclerView recyclerView;

    private UserRecyclerAdapter adapter;

    private Toolbar toolbar;

    private DatabaseReference ref;



    private ArrayList<User> users;
    private  User user;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_all_user);

        attendre();
        //get toolbar_layout
        toolbar=findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ref=FirebaseDatabase.getInstance().getReference().child("Users");

        readFromDataBase();


    }


    public void readFromDataBase()
    {

        ref.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                recyclerView=findViewById(R.id.recycler);

                user=new User();

                users=new ArrayList<>();



                for (DataSnapshot userSnapshot: dataSnapshot.getChildren())
                {
                    user = userSnapshot.getValue(User.class);
                    users.add(user);


                }




                adapter=new UserRecyclerAdapter(users);

                recyclerView.setAdapter(adapter);

                recyclerView.setLayoutManager(new LinearLayoutManager(ListAllUser.this));

                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });


    }
    public void attendre()
    {
        progressDialog=new ProgressDialog(this);


        progressDialog.setTitle("chargement");
        progressDialog.setMessage("veuillez attendre..");
        progressDialog.show();



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
            onBackPressed();

        }

        if(item.getItemId()==R.id.suprimer)
            startActivity(new Intent(this, SupprimerUser.class));

        if(item.getItemId()==R.id.chercher)
            startActivity(new Intent(this, MapsActivity.class));



        return super.onOptionsItemSelected(item);
    }

}
