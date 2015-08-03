package com.eclubprague.iot.android.driothub.cloud.user;

import com.google.gson.annotations.Expose;

/**
 * Created by Dat on 3.8.2015.
 */
public class User {
    @Expose(deserialize = false) protected String username;
    @Expose (deserialize = false) protected String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {return username;}

    public String getPassword() {return password;}

    @Override
    public String toString() {
        return "User [username=" + username + ", password=" + password + "]";
    }
}
