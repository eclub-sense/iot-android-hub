package com.eclubprague.iot.android.driothub.cloud.sensors.supports;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dat on 5.8.2015.
 */
public class WSDataWrapper {

    @Expose
    private String type = "DATA";
    @Expose
    private List<SensorDataWrapper> data = new ArrayList<>();

    public WSDataWrapper() {
    }

    public void addSensorData(SensorDataWrapper sensorDataWrapper) {
        data.add(sensorDataWrapper);
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
