package com.eclubprague.iot.android.driothub.cloud.sensors;

import com.eclubprague.iot.android.driothub.cloud.sensors.supports.DataNameValuePair;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.SensorType;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by Dat on 28.7.2015.
 */
public class LightSensor extends Sensor {

    @Expose(deserialize = false) protected String unit = "lx";
    @Expose(deserialize = false) protected float illumination = 0;

    public LightSensor() {
        super();
    }
    public LightSensor(String uuid, String secret) {
        super(uuid, SensorType.LIGHT, secret);
    }

    @Override
    public void readPayload(byte[] payload) {
        //TODO
    }

    @Override
    public String printData() {
        return ("illumination = " + illumination + " lx");
    }

    public float getIllumination() {
        return illumination;
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
        data.add(new DataNameValuePair("illumination", Float.toString(illumination)));
        return data;
    }

    @Override
    public void setData(float[] values) {
        illumination = values[0];
    }
}