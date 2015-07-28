package com.eclubprague.iot.android.driothub.cloud.sensors;

import com.google.gson.annotations.Expose;

/**
 * Created by Dat on 28.7.2015.
 */
public class Accelerometer extends Sensor {

    @Expose(deserialize = false) protected String unit = "m/s^2";
    @Expose(deserialize = false) protected double x = 0;
    @Expose(deserialize = false) protected double y = 0;
    @Expose(deserialize = false) protected double z = 0;

    public Accelerometer() {
        super();
    }
    public Accelerometer(int uuid, String secret) {
        super(uuid, SensorType.ACCELEROMETER, secret);
    }

    @Override
    public void readPayload(byte[] payload) {
        //TODO
    }

    @Override
    public String printData() {
        return ("x = " + x + ", y = " + y + ", z = " + z + "(m/s^2)");
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String getUnit() {
        return unit;
    }

    public void setData(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "Accelerometer [x = " + x + ", y = " + y + ", z = " + z + "(m/s^2)" + ", uuid=" + uuid + ", type="
                + type + ", secret=" + secret + "]";
    }
}