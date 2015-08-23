package com.eclubprague.iot.android.driothub.cloud.user;

import com.google.gson.Gson;

/**
 * Created by Dat on 3.8.2015.
 */
public class User {
    protected String email;

    public User(String email) {
        this.email =email;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
