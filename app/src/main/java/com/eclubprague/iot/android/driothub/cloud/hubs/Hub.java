package com.eclubprague.iot.android.driothub.cloud.hubs;

import com.eclubprague.iot.android.driothub.cloud.registry.Identificable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Dat on 28.7.2015.
 */
public class Hub implements Identificable {

    @Expose
    @SerializedName("@type") private String jsonType = "hub";
    @Expose protected int uuid;
    @Expose (deserialize = false) protected String status = "connected";

    public Hub(int uuid) {
        this.uuid = uuid;
    }

    @Override
    public int getIntUuid() {
        return uuid;
    }

    public String getStringUuid() {
        return String.format("%08d", uuid);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Hub [uuid=" + uuid + "]";
    }
}
