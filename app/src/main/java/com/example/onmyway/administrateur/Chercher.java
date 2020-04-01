package com.example.onmyway.administrateur;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onmyway.DB.UserDB;
import com.example.onmyway.R;
import com.example.onmyway.UserInfo.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Chercher extends AppCompatActivity {

    private TextView chercherV;
    private TextView fullnameV;
    private TextView emailV;
    private TextView cinV;

    //input search of user(cin)
    private String keyWord;

    private LinearLayout operationV;
    private DatabaseReference ref;

    ArrayList<User> users;
    private User user;
    UserDB userDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chercher);






        chercherV=findViewById(R.id.search);
        fullnameV=findViewById(R.id.fullname);
        cinV=findViewById(R.id.cin);
        emailV=findViewById(R.id.email);
        operationV=findViewById(R.id.operation);

        userDB=new UserDB(this);
        user=new User();
        users=new ArrayList<>();
        users=userDB.getAllUsers();

        if(users.size()==0)
        {

            ref= FirebaseDatabase.getInstance().getReference().child("Users");

            ref.addValueEventListener(new ValueEventListener(){
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren())
                    {
                        user = userSnapshot.getValue(User.class);
                        //save users in UserDB
                        userDB.addUser(user);
                        //add to array users
                        users.add(user);

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }


    public void chercherUser(View view)
    {
        keyWord=chercherV.getText().toString();
        Toast.makeText(this, keyWord, Toast.LENGTH_SHORT).show();
        if(!keyWord.isEmpty())
        {
            keyWord=keyWord.toLowerCase();
            users=userDB.getAllUsers();

            if(users.size()==0)
            {
                Toast.makeText(this, "vous n'avez aucun cheuffaur", Toast.LENGTH_SHORT).show();
                return;
            }


                user=chercher(keyWord);

                if(user!=null)
                {
                    fullnameV.setText(user.getfullName().toUpperCase());
                    cinV.setText(user.getId());
                    emailV.setText(user.getEmail());
                    //SHOW ALL VIEWYS
                    fullnameV.setVisibility(View.VISIBLE);
                    cinV.setVisibility(View.VISIBLE);
                    emailV.setVisibility(View.VISIBLE);
                    operationV.setVisibility(View.VISIBLE);

                }


            return;


        }

        Toast.makeText(this,"veuillez tapez le CIN ", Toast.LENGTH_SHORT).show();


    }

    private User chercher(String str)
    {
        int i=0;

        while(users.size()>i)
        {
            String cin=users.get(i).getId();
            if(cin!=null)
            {

                if(cin.toLowerCase().equals(str))
                    return users.get(i);

            }



            i++;
        }

        return null;
    }

    public void supprimerUser(View view) {

        //delete user(we should delete from firebase
        Toast.makeText(this, "ths is keyword "+keyWord, Toast.LENGTH_SHORT).show();
       // userDB.deleteUser(keyWord);
        Toast.makeText(this, "numbre of row deleted is "+userDB.deleteUser(keyWord), Toast.LENGTH_SHORT).show();

        Log.i("allUsers",keyWord);


        fullnameV.setText("");
        cinV.setText("");
        emailV.setText("");
        chercherV.setText("");

        fullnameV.setVisibility(View.GONE);
        cinV.setVisibility(View.GONE);
        emailV.setVisibility(View.GONE);
        operationV.setVisibility(View.GONE);
    }

    private String getCinFromIntent()
    {

        if(getIntent().hasExtra("cin"))
            return getIntent().getStringExtra("cin");
        return null;

    }

    public void afficherSurMap(View view) {
    }

/*    private User findUser(String id)
    {
        User user1=new User();
        if(users.size()>0)
        {
            Toast.makeText(this, "je suis la", Toast.LENGTH_SHORT).show();
            for (int i=0;i<users.size();i++)
            {
                user1=users.get(i);
                if(user1.getId().toLowerCase().equals(id))
                {

                    return user1;
                }

            }
            return null;


        }
        return null;

    }*/


    @Override
    protected void onResume() {
        super.onResume();
        String cin=getCinFromIntent();

        if(cin!=null)
        {
            User searchUser=chercher(cin.toLowerCase());
            //search keyword
            keyWord=cin;


            if(searchUser!=null)
            {
                chercherV.setText(cin);
                fullnameV.setText(searchUser.getfullName().toUpperCase());
                cinV.setText(searchUser.getId());
                emailV.setText(searchUser.getEmail());
                //SHOW ALL VIEWYS
                fullnameV.setVisibility(View.VISIBLE);
                cinV.setVisibility(View.VISIBLE);
                emailV.setVisibility(View.VISIBLE);
                operationV.setVisibility(View.VISIBLE);

            }


        }



    }
}

