package com.eclubprague.iot.android.driothub.cloud.sensors;

import com.eclubprague.iot.android.driothub.cloud.hubs.Hub;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.DataNameValuePair;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.SensorType;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Dat on 6.8.2015.
 */
public class Barometer extends Sensor {

    protected String unit = "hPa";
    protected float pressure = 0;

    public Barometer() {
        super();
    }
    public Barometer(String uuid, String secret, Hub hub) {
        super(uuid, SensorType.PRESSURE, secret, hub);
    }

    @Override
    public void readPayload(byte[] payload) {
        //TODO
    }

    @Override
    public String printData() {
        return ("pressure = " + pressure + " hPa");
    }

    public float getPressure() {
        return pressure;
    }

    public String getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public List<DataNameValuePair> getDataList() {
        measured.clear();
        measured.add(new DataNameValuePair("pressure", Float.toString(pressure)));
        measured.add(new DataNameValuePair("unit", unit));
        return measured;
    }

    @Override
    public void setData(float[] values) {
        pressure = values[0];
    }
}