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
    private String s_type;
    private int type;

    public SensorDataWrapper(Sensor sensor) {
        this.sensor = sensor;
        this.measured = sensor.getMeasured();
        this.uuid = sensor.getUuid();
        this.s_type = sensor.getStringType();
        this.type = sensor.getType();
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
