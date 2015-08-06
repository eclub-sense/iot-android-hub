package com.eclubprague.iot.android.driothub.cloud.sensors;

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
    public Barometer(String uuid, String secret) {
        super(uuid, SensorType.PRESSURE, secret);
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
        data.clear();
        data.add(new DataNameValuePair("pressure", Float.toString(pressure)));
        return data;
    }

    @Override
    public void setData(float[] values) {
        pressure = values[0];
    }
}