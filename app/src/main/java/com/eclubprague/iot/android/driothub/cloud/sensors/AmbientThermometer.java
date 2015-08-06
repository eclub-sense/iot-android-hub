package com.eclubprague.iot.android.driothub.cloud.sensors;

import com.eclubprague.iot.android.driothub.cloud.sensors.supports.DataNameValuePair;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.SensorType;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Dat on 6.8.2015.
 */
public class AmbientThermometer extends Sensor {

    protected String unit = "°C";
    protected float temperature = 0;

    public AmbientThermometer() {
        super();
    }
    public AmbientThermometer(String uuid, String secret) {
        super(uuid, SensorType.AMBIENT_THERMOMETER, secret);
    }

    @Override
    public void readPayload(byte[] payload) {
        //TODO
    }

    @Override
    public String printData() {
        return ("temperature = " + temperature + " °C");
    }

    public float getTemperature() {
        return temperature;
    }

    public String getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public List<DataNameValuePair> getDataList() {
        data.clear();
        data.add(new DataNameValuePair("temperature", Float.toString(temperature)));
        return data;
    }

    @Override
    public void setData(float[] values) {
        temperature = values[0];
    }
}