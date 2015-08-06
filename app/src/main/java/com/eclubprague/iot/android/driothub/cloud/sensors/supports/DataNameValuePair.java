package com.eclubprague.iot.android.driothub.cloud.sensors.supports;

import com.google.gson.Gson;

/**
 * Created by Dat on 5.8.2015.
 */
public class DataNameValuePair {

    private String name;
    private String value;

    public DataNameValuePair(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
