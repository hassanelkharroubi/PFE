package com.example.onmyway.DB;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.onmyway.CustomToast;
import com.example.onmyway.Login;
import com.example.onmyway.R;
import com.example.onmyway.UserInfo.HomeUser;
import com.example.onmyway.UserInfo.User;
import com.example.onmyway.administrateur.Administrateur;
import com.example.onmyway.administrateur.Home;
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

public class CustomFirebase implements ValueEventListener {


    private Context context;
    private Object [] objects;
    private boolean success;



    public static DatabaseReference getDataRefLevel1(String ref)
    {
        return FirebaseDatabase.getInstance().getReference().child(ref);

    }




    public static FirebaseUser getCurrentUser()
    {

        return FirebaseAuth.getInstance().getCurrentUser();

    }
    public static FirebaseAuth getUserAuth()
    {
        return FirebaseAuth.getInstance();
    }

    //id is passed in ref as ref.child(id)
    public  Object [] readDataById( Object []  objects,int objectLength, DatabaseReference ref)
    {
        this.objects=new Object[objectLength];
        ref.addValueEventListener(this);
        return this.objects;
    }


    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        if(dataSnapshot.exists())
        {
            int i=0;
            for (DataSnapshot data:dataSnapshot.getChildren())
            {
               objects[i]= data.getValue(Object.class);
               i++;
            }

        }

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        CustomToast.toast("erreur de connection",context);

    }//end of onCancelled()



public static void  DeleteUser(String email,String password)
{
    getUserAuth().signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        getUserAuth().getCurrentUser().delete();
                    }
                    else
                        getUserAuth().signOut();

                }
            });
}//end of deleteUser



/*    //method for sign in
    public static void signIn(final String email, final String password, final Context context)
    {
        Firebase.getUserAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {


                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            if(email.equals(Administrateur.email))
                            {
                                Intent intent=new Intent(context, Home.class);
                                context.startActivity(intent);


                            }
                            else
                            {

                                Query query= getDataRefLevel1(context.getResources().getString(R.string.UserData)).orderByKey().equalTo(getUserAuth().getUid());
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChildren())
                                        {
                                            for (DataSnapshot userData:dataSnapshot.getChildren())
                                            {
                                                User user=userData.getValue(User.class);
                                                if(user!=null)
                                                {
                                                    Intent intent=new Intent(context, HomeUser.class);
                                                    intent.putExtra("email",user.getEmail());
                                                    intent.putExtra("fullName",user.getfullName());
                                                    intent.putExtra("cin",user.getId());
                                                    context.startActivity(intent);
                                                  //  context.finish();

                                                }

                                            }


                                        }
                                        else
                                        {

                                            CustomToast.toast("cet utilisateur n'existe pas",context.getApplicationContext());
                                            getUserAuth().signOut();
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

                            CustomToast.toast("le mot de passe ou email est incorrect ",context);

                        }

                    }
                });

    }//end of sigIn()*/
}
