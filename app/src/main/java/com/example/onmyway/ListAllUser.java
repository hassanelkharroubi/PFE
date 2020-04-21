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


import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onmyway.DB.UserDB;
import com.example.onmyway.UserInfo.User;
import com.example.onmyway.administrateur.Chercher;
import com.example.onmyway.administrateur.Home;
import com.example.onmyway.administrateur.MapsActivity;
import com.example.onmyway.administrateur.RegisterActivity;
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
    private final String TAG="ListAllUser";

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

        ref=FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.UserData));
        userDB=new UserDB(this);

        readFromDataBase();


    }
    public void toast(String msg) {
        LayoutInflater layoutInflater= getLayoutInflater();
        View toastLayout=layoutInflater.inflate(R.layout.toast, (ViewGroup) findViewById(R.id.showtoast));
        TextView textView= toastLayout.findViewById(R.id.toastMsg);
        textView.setText(msg+" ");
        Toast toast=new Toast(this);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastLayout);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0);
        toast.show();
    }



    public void readFromDataBase()
    {
        users=userDB.getAllUsers();
        if(users.size()==0)
        {
            //show progress dialog

            ref.addValueEventListener(new ValueEventListener(){
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    attendre();

                    if(!dataSnapshot.hasChildren())
                    {
                       progressDialog.dismiss();

                       CustomToast.toast("pas de cheffaures! veuillez ajouter neveau",ListAllUser.this);
                       startActivity(new Intent(ListAllUser.this, Home.class));

                        return;
                    }


                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren())
                    {
                        user = userSnapshot.getValue(User.class);
                        usersFireBase.add(user);

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
        else setAdapter(users);





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
            startActivity(new Intent(this, Chercher.class));
        if(item.getItemId()==R.id.chercher)
            startActivity(new Intent(this, MapsActivity.class));
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        users=new ArrayList<>();
        readFromDataBase();

    }
}
