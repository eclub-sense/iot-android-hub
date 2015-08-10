package com.eclubprague.iot.android.driothub.cloud.sensors.supports;

import com.eclubprague.iot.android.driothub.cloud.sensors.Sensor;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Dat on 5.8.2015.
 */
public class SensorDataWrapper {

    private List<DataNameValuePair> measured;
    private transient Sensor sensor;
    private String uuid;

    public SensorDataWrapper(Sensor sensor) {
        this.sensor = sensor;
        this.measured = sensor.getDataList();
        this.uuid = sensor.getUuid();
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
