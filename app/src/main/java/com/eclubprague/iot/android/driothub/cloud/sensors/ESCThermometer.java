package com.eclubprague.iot.android.driothub.cloud.sensors;

import com.eclubprague.iot.android.driothub.cloud.sensors.supports.DataNameValuePair;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.SensorType;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by Dat on 28.7.2015.
 */
public class ESCThermometer extends Sensor {

    @Expose(deserialize = false) protected int temperature;
    @Expose (deserialize = false) protected int pressure;

    public ESCThermometer() {
        super();
    }
    public ESCThermometer(String uuid, String secret) {
        super(uuid, SensorType.THERMOMETER, secret);
    }

    @Override
    public void readPayload(byte[] payload) {
        temperature = payload[0];
        pressure = payload[1];
    }

    @Override
    public String printData() {
        return "[temperature=" + temperature + ", pressure=" + pressure + "]";
    }

    public int getTemperature() {
        return temperature;
    }

    public int getPressure() {
        return pressure;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public List<DataNameValuePair> getDataList() {
        data.clear();
        data.add(new DataNameValuePair("temperature", Integer.toString(temperature)));
        data.add(new DataNameValuePair("pressure", Integer.toString(pressure)));
        return data;
    }

    @Override
    public void setData(float[] values) {
    }
}