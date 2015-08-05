package com.eclubprague.iot.android.driothub.cloud.sensors.supports;

import com.eclubprague.iot.android.driothub.cloud.sensors.Sensor;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by Dat on 5.8.2015.
 */
public class SensorDataWrapper {

    @Expose private List<DataNameValuePair> data;
    @Expose private Sensor sensor;

    public SensorDataWrapper(Sensor sensor) {
        this.sensor = sensor;
        this.data = sensor.getDataList();
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
