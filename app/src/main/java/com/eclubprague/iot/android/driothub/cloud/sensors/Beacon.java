package com.eclubprague.iot.android.driothub.cloud.sensors;

import com.eclubprague.iot.android.driothub.cloud.hubs.Hub;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.DataNameValuePair;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.SensorType;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Dat on 6.8.2015.
 */
public class Beacon extends Sensor {

    protected String email;
    protected float rssi = 0;

    public Beacon() {
        super();
    }
    public Beacon(String uuid, String secret, Hub hub) {
        super(uuid, SensorType.BEACON, secret, hub);
    }

    @Override
    public void readPayload(byte[] payload) {
        //TODO
    }

    @Override
    public String printData() {
        return ("email = " + email + ", rssi = " + Float.toString(rssi) + " dBm");
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public float getRssi() {
        return rssi;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public List<DataNameValuePair> getMeasured() {
        measured.clear();
        measured.add(new DataNameValuePair("email", email));
        measured.add(new DataNameValuePair("rssi", Float.toString(rssi)));
        return measured;
    }

    @Override
    public void setData(float[] values) {
        rssi = values[0];
    }
}