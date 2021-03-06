package com.eclubprague.iot.android.driothub.cloud.hubs;

import com.eclubprague.iot.android.driothub.cloud.registry.Identificable;
import com.eclubprague.iot.android.driothub.cloud.user.User;
import com.google.gson.Gson;

/**
 * Created by Dat on 28.7.2015.
 */
public class Hub implements Identificable {

    /*@Expose
    @SerializedName("@type") private String jsonType = "hub";*/
    protected String uuid;
    //@Expose (deserialize = false) protected String status = "connected";
    protected String email;
    protected final String type = "LOGIN";


    private transient User user;

    public Hub(String uuid, User user) {
        this.uuid = uuid;
        this.user = user;
        this.email = user.getEmail();
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    /*public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }*/

    public User getUser() {return user;}

    public String getType() {return type;}

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
