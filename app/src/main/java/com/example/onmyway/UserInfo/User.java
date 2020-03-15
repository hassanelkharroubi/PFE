package com.example.onmyway.UserInfo;

import androidx.annotation.NonNull;

public class User {

    private String fullName;
    private String email;
    private String password;
    private String id;
    public User()
    {
        //this Constructor is for DataSnapShot for firebase
        
    }

    public User(String fullname, String email, String password, String id) {
        this.fullName = fullname;
        this.email = email;
        this.password = password;
        this.id = id;
    }

    public String getfullName() {
        return fullName;
    }

    public void setfullName(String fullName) {
        this.fullName = fullName;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
