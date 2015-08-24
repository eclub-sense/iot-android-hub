package com.eclubprague.iot.android.driothub.cloud.sensors;

import com.eclubprague.iot.android.driothub.cloud.hubs.Hub;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.DataNameValuePair;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.SensorType;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Dat on 28.7.2015.
 */
public class ProximitySensor extends Sensor {

    protected String unit = "cm";
    protected float proximity = 0;

    public ProximitySensor() {
        super();
    }
    public ProximitySensor(String uuid, String secret, Hub hub) {
        super(uuid, SensorType.PROXIMITY, secret, hub);
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
        return new Gson().toJson(this);
    }

    @Override
    public List<DataNameValuePair> getMeasured() {
        measured.clear();
        measured.add(new DataNameValuePair("proximity", Float.toString(proximity)));
        measured.add(new DataNameValuePair("unit", unit));
        return measured;
    }

    @Override
    public void setData(float[] values) {
        proximity = values[0];
    }
}