package com.example.onmyway.administrateur;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onmyway.DB.Firebase;
import com.example.onmyway.DB.UserDB;
import com.example.onmyway.ListAllUser;
import com.example.onmyway.R;
import com.example.onmyway.UserInfo.GeoPoint;
import com.example.onmyway.UserInfo.User;
import com.example.onmyway.connection.Internet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.EventListener;
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

    private DatabaseReference refChild;
    private DatabaseReference locationRef;


    ArrayList<User> users;
    private User user;
    UserDB userDB;
//on a besoin de ca  lorsqu'on va faire des requete au firebase
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chercher);



//on va utiliser cet objet pour les requetes comme sql
        refChild= FirebaseDatabase.getInstance().getReference().child(getString(R.string.UserData));
        locationRef= FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.OnlineUserLocation));

      //  DatabaseReference node=locationRef.child("H0HHxujI3hbxNo0b1uOfxNC8MQs2\n");

        locationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    for (DataSnapshot snapshot:dataSnapshot.getChildren())
                    {
                        Intent intent=new Intent(Chercher.this,UserPosition.class);

                        GeoPoint point=snapshot.getValue(GeoPoint.class);
                        Toast.makeText(Chercher.this, "ooooooh", Toast.LENGTH_SHORT).show();

                        intent.putExtra("lat",point.getLatitude());
                        intent.putExtra("long",point.getLongitude());
                        intent.putExtra("key",snapshot.getKey());
                        startActivity(intent);


                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

            ref= FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.UserData));


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


        Toast.makeText(this, keyWord, Toast.LENGTH_SHORT).show();
        attendre();
        Query query=refChild.orderByChild("id").equalTo(keyWord);

        try {
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {


                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user2  = snapshot.getValue(User.class);


                            Toast.makeText(Chercher.this,  snapshot.getKey()+" value = "+snapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(Chercher.this, user2.getEmail(), Toast.LENGTH_SHORT).show();
                        }




                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(Chercher.this, keyWord+" n'existe pas", Toast.LENGTH_SHORT).show();

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressDialog.dismiss();
                    Toast.makeText(Chercher.this, "erreur", Toast.LENGTH_SHORT).show();


                }
            });

        }catch (NullPointerException e)
        {
            Toast.makeText(this, "Veuillez verifier votre connection", Toast.LENGTH_SHORT).show();
        }





    }
    public void attendre()
    {
        progressDialog=new ProgressDialog(this);


        progressDialog.setTitle("Chargement");
        progressDialog.setMessage("veuillez attendre ....");

        progressDialog.show();


    }






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

