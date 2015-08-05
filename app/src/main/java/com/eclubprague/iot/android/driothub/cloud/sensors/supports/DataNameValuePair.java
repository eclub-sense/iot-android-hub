package com.eclubprague.iot.android.driothub.cloud.sensors.supports;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import org.json.JSONArray;

/**
 * Created by Dat on 5.8.2015.
 */
public class DataNameValuePair {

    @Expose private String name;
    @Expose private String value;

    public DataNameValuePair(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
