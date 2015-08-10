package com.eclubprague.iot.android.driothub.cloud.sensors;

import com.eclubprague.iot.android.driothub.cloud.hubs.Hub;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.DataNameValuePair;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.SensorType;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Dat on 6.8.2015.
 */
public class Gyroscope extends Sensor {

    protected String unit = "rad/s";
    protected float x = 0;
    protected float y = 0;
    protected float z = 0;

    public Gyroscope() {
        super();
    }
    public Gyroscope(String uuid, String secret, Hub hub) {
        super(uuid, SensorType.GYROSCOPE, secret, hub);
    }

    @Override
    public void readPayload(byte[] payload) {
        //TODO
    }

    @Override
    public String printData() {
        return ("x = " + x + ", y = " + y + ", z = " + z + "(rad/s)");
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public String getUnit() {
        return unit;
    }

    @Override
    public void setData(float values[]) {
        x = values[0];
        y = values[1];
        z = values[2];
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public List<DataNameValuePair> getDataList() {
        measured.clear();
        measured.add(new DataNameValuePair("x", Float.toString(x)));
        measured.add(new DataNameValuePair("y", Float.toString(y)));
        measured.add(new DataNameValuePair("z", Float.toString(z)));
        return measured;
    }
}