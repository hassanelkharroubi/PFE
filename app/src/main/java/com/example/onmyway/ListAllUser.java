package com.example.onmyway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.onmyway.UserInfo.User;


import java.util.ArrayList;

public class ListAllUser extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Toolbar toolbar;



    private ArrayList<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_all_user);
        //get toolbar_layout
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        recyclerView=findViewById(R.id.recycler);

        users=new ArrayList<User>();
        users.add(new User("hassan el kharroubi","hassan@gmail.com","jassan123","zt265568"));
        users.add(new User("hassan el kharroubi","hassan@gmail.com","jassan123","zt265568"));
        users.add(new User("hassan el kharroubi","hassan@gmail.com","jassan123","zt265568"));
        users.add(new User("hassan el kharroubi","hassan@gmail.com","jassan123","zt265568"));
        users.add(new User("hassan el kharroubi","hassan@gmail.com","jassan123","zt265568"));
        users.add(new User("hassan el kharroubi","hassan@gmail.com","jassan123","zt265568"));
        users.add(new User("hassan el kharroubi","hassan@gmail.com","jassan123","zt265568"));
        users.add(new User("hassan el kharroubi","hassan@gmail.com","jassan123","zt265568"));
        users.add(new User("hassan el kharroubi","hassan@gmail.com","jassan123","zt265568"));
        users.add(new User("hassan el kharroubi","hassan@gmail.com","jassan123","zt265568"));
        users.add(new User("hassan el kharroubi","hassan@gmail.com","jassan123","zt265568"));
        UserRecyclerAdapter adapter=new UserRecyclerAdapter(users);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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

        if(item.getItemId()==android.R.id.home)
        {
            onBackPressed();

        }

        if(item.getItemId()==R.id.suprimer)
            startActivity(new Intent(this,SupprimerUser.class));

        if(item.getItemId()==R.id.chercher)
            startActivity(new Intent(this,MapsActivity.class));



        return super.onOptionsItemSelected(item);
    }

}
