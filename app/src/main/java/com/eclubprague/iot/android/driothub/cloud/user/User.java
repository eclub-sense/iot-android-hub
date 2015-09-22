package com.eclubprague.iot.android.driothub.cloud.user;

import com.google.gson.Gson;

/**
 * Created by Dat on 3.8.2015.
 */
public class User {
    protected String email;
    protected String password;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
