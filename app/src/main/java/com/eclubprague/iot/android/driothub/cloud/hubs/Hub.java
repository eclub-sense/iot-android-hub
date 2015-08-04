package com.eclubprague.iot.android.driothub.cloud.hubs;

import com.eclubprague.iot.android.driothub.cloud.registry.Identificable;
import com.eclubprague.iot.android.driothub.cloud.user.User;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;
import org.restlet.ext.gson.GsonConverter;

/**
 * Created by Dat on 28.7.2015.
 */
public class Hub implements Identificable {

    /*@Expose
    @SerializedName("@type") private String jsonType = "hub";*/
    @Expose protected String uuid;
    //@Expose (deserialize = false) protected String status = "connected";
    @Expose (deserialize = false) protected String username;
    @Expose (deserialize = false) protected String password;
    @Expose (deserialize = false) protected String type = "LOGIN";


    @Expose (serialize = false) private User user;

    public Hub(String uuid, User user) {
        this.uuid = uuid;
        this.user = user;
        this.username = user.getUsername();
        this.password = user.getPassword();
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    public String getStringUuid() {
        return String.format("%08d", uuid);
    }

    /*public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }*/

    public User getUser() {return user;}

    public String getType() {return type;}

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();

        return gson.toJson(this); /*"Hub [uuid=" + uuid + "]";*/
    }
}
