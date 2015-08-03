package com.eclubprague.iot.android.driothub.cloud.sensors;

import com.google.gson.annotations.Expose;

/**
 * Created by Dat on 28.7.2015.
 */
public class ProximitySensor extends Sensor {

    @Expose(deserialize = false) protected String unit = "cm";
    @Expose(deserialize = false) protected double proximity = 0;

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

    public double getProximity() {
        return proximity;
    }

    public String getUnit() {
        return unit;
    }

    public void setData(double proximity) {
        this.proximity = proximity;
    }

    @Override
    public String toString() {
        return "Light_sensor [proximity = " + proximity + " cm" + ", uuid=" + uuid + ", type="
                + type + ", secret=" + secret + "]";
    }
}