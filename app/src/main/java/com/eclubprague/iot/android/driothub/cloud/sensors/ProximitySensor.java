package com.eclubprague.iot.android.driothub.cloud.sensors;

import com.eclubprague.iot.android.driothub.cloud.sensors.supports.DataNameValuePair;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.SensorType;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by Dat on 28.7.2015.
 */
public class ProximitySensor extends Sensor {

    @Expose(deserialize = false) protected String unit = "cm";
    @Expose(deserialize = false) protected float proximity = 0;

    public ProximitySensor() {
        super();
    }
    public ProximitySensor(String uuid, String secret) {
        super(uuid, SensorType.PROXIMITY, secret);
    }

    @Override
    public void readPayload(byte[] payload) {
        //TODO
    }

    @Override
    public String printData() {
        return ("proximity = " + proximity + " cm");
    }

    public float getProximity() {
        return proximity;
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
        data.add(new DataNameValuePair("proximity", Float.toString(proximity)));
        return data;
    }

    @Override
    public void setData(float[] values) {
        proximity = values[0];
    }
}