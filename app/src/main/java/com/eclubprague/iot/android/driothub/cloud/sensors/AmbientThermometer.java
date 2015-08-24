package com.eclubprague.iot.android.driothub.cloud.sensors;

import com.eclubprague.iot.android.driothub.cloud.hubs.Hub;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.DataNameValuePair;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.SensorType;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Dat on 6.8.2015.
 */
public class AmbientThermometer extends Sensor {

    protected String unit = "\u00B0" + "C";
    protected float temperature = 0;

    public AmbientThermometer(String uuid, String secret, Hub hub) {
        super(uuid, SensorType.AMBIENT_THERMOMETER, secret, hub);
    }

    @Override
    public void readPayload(byte[] payload) {
        //TODO
    }

    @Override
    public String printData() {
        return ("temperature = " + temperature + " " + unit);
    }

    public float getTemperature() {
        return temperature;
    }

    public String getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public List<DataNameValuePair> getMeasured() {
        measured.clear();
        measured.add(new DataNameValuePair("temperature", Float.toString(temperature)));
        measured.add(new DataNameValuePair("unit", unit));
        return measured;
    }

    @Override
    public void setData(float[] values) {
        temperature = values[0];
    }
}