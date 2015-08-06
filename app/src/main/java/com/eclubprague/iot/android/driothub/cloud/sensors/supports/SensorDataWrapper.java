package com.eclubprague.iot.android.driothub.cloud.sensors.supports;

import com.eclubprague.iot.android.driothub.cloud.sensors.Sensor;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Dat on 5.8.2015.
 */
public class SensorDataWrapper {

    private List<DataNameValuePair> data;
    private Sensor sensor;

    public SensorDataWrapper(Sensor sensor) {
        this.sensor = sensor;
        this.data = sensor.getDataList();
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
