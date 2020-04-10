package com.example.onmyway.administrateur;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.onmyway.DB.UserDB;
import com.example.onmyway.R;
import com.example.onmyway.UserInfo.GeoPoint;
import com.example.onmyway.UserInfo.User;
import com.example.onmyway.connection.Internet;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;


public class Chercher extends AppCompatActivity {

    private TextView chercherV;
    private TextView fullnameV;
    private TextView emailV;
    private TextView cinV;

    //input search of user(cin)
    private String keyWord;
    private String idUserInFireBase;

    private LinearLayout operationV;
    private DatabaseReference ref;
    private DatabaseReference refUserData;
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
        refUserData= FirebaseDatabase.getInstance().getReference().child(getString(R.string.UserData));
        locationRef= FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.OnlineUserLocation));

        chercherV=findViewById(R.id.search);
        fullnameV=findViewById(R.id.fullname);
        cinV=findViewById(R.id.cin);
        emailV=findViewById(R.id.email);
        operationV=findViewById(R.id.operation);

        userDB=new UserDB(this);
        user=new User();
        users=new ArrayList<>();
        users=userDB.getAllUsers();
        idUserInFireBase=null;

        if(users.size()==0)
        {

            ref= FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.UserData));
            if(ref.getPath().isEmpty())
            {
                Toast.makeText(this, "hhhhhhhhhhh", Toast.LENGTH_SHORT).show();
            }



            ref.addValueEventListener(new ValueEventListener(){
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren())
                    {
                        if(userSnapshot.exists())
                        {
                            user = userSnapshot.getValue(User.class);
                            //save users in UserDB
                            userDB.addUser(user);
                            //add to array users
                            users.add(user);

                        }
                        else
                        {
                            Toast.makeText(Chercher.this, "user data n'existe pas", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }


                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

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
    public void chercherUser(View view)
    {
        keyWord=chercherV.getText().toString();

        if(!keyWord.isEmpty())
        {
            //query by keyword in userDB
            keyWord=keyWord.toLowerCase();
            users=userDB.getAllUsers();

            if(users.size()==0)
            {
                toast("vous n'avez aucun cheuffaur");

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

        toast("veuillez tapez le CIN ");


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

        findUserInFireBaseByCin(true);

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
        attendre();
        findUserInFireBaseByCin(false);
    }
    public void attendre()
    {
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Chargement");
        progressDialog.setMessage("veuillez attendre ....");
        progressDialog.show();
    }

    //this boolean is used for to detect if we are going to show on map(false) or going to delete user(true)
    private void findUserInFireBaseByCin(final boolean delete)
    {
        Query query=null;
            query=refUserData.orderByChild("id").equalTo(keyWord);
            if(query==null)
            {

                toast("cet utilisateur n'existe pas");
                return;
            }

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot: dataSnapshot.getChildren())
                    {
                        if(snapshot.exists())
                        {

                            idUserInFireBase=snapshot.getKey();
                            if(delete)
                            {
                                refUserData.child(idUserInFireBase).removeValue();
                                progressDialog.dismiss();
                                return;
                            }
                            //search for geocoordiate

                            locationRef.orderByKey().equalTo(idUserInFireBase).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    for (DataSnapshot snapshot: dataSnapshot.getChildren())
                                    {
                                        if(snapshot.exists())
                                        {

                                            Intent intent=new Intent(Chercher.this,UserPosition.class);
                                            intent.putExtra("id",idUserInFireBase);
                                            GeoPoint newGeoPoint=snapshot.getValue(GeoPoint.class);
                                            intent.putExtra("lat",newGeoPoint.getLatitude());
                                            intent.putExtra("long",newGeoPoint.getLongitude());
                                            //hide progressbar
                                            progressDialog.dismiss();
                                            startActivity(intent);

                                        }
                                        else
                                        {
                                            progressDialog.dismiss();
                                            Toast.makeText(Chercher.this, "cette utilisateur n'pas de position actuel ", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    progressDialog.dismiss();
                                    Toast.makeText(Chercher.this, "Veuillez verfier votre connection", Toast.LENGTH_SHORT).show();

                                }
                            });


                        }


                            progressDialog.dismiss();



                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Chercher.this, "Veuillez verfier votre connection", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
            });



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

