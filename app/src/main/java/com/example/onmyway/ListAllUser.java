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

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.onmyway.DB.UserDB;
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
    private final String TAG="allUsers";

    //for sqlite database
    private UserDB userDB;
    private ArrayList<User> usersFireBase,users;
    private  User user;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_all_user);

        //get toolbar_layout
        toolbar=findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView=findViewById(R.id.recycler);

        //store user catched from firebase
        user=new User();
        //add all user from firebase to usersFireBase
        usersFireBase=new ArrayList<>();
        //for local data base(UserDB)
        users=new ArrayList<>();

        ref=FirebaseDatabase.getInstance().getReference().child("Users");
        userDB=new UserDB(this);

        readFromDataBase();

    }


    public void readFromDataBase()
    {
        users=userDB.getAllUsers();
        if(users.size()==0)
        {
            //show progress dialog
            attendre();

            ref.addValueEventListener(new ValueEventListener(){
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren())
                    {
                        user = userSnapshot.getValue(User.class);
                        usersFireBase.add(user);
                        //add to data base
                    }

                    userDB.addUsers(usersFireBase);
                    setAdapter(usersFireBase);
                    progressDialog.dismiss();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Toast.makeText(ListAllUser.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                }

            });
        }
        else
        {
            Log.i(TAG,"inside adpater and users.size()!=0 ");
            setAdapter(users);

        }



    }

    private void setAdapter(ArrayList<User> list)
    {
        adapter=new UserRecyclerAdapter(list, ListAllUser.this);

        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(ListAllUser.this));

    }
    public void attendre()
    {
        progressDialog=new ProgressDialog(this);


        progressDialog.setTitle("Chargement");
        progressDialog.setMessage("veuillez attendre ....");

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

    @Override
    protected void onResume() {

        super.onResume();
        Log.i(TAG,"inside OnResume");
        users=new ArrayList<>();

        readFromDataBase();

        Toast.makeText(this, "hello : "+users.size(), Toast.LENGTH_SHORT).show();
    }
}
