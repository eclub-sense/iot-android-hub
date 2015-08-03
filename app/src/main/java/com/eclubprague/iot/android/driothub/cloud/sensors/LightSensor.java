package com.eclubprague.iot.android.driothub.cloud.sensors;

import com.google.gson.annotations.Expose;

/**
 * Created by Dat on 28.7.2015.
 */
public class LightSensor extends Sensor {

    @Expose(deserialize = false) protected String unit = "lx";
    @Expose(deserialize = false) protected double illumination = 0;

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

    public double getIllumination() {
        return illumination;
    }

    public String getUnit() {
        return unit;
    }

    public void setData(double illumination) {
        this.illumination = illumination;
    }

    @Override
    public String toString() {
        return "Light_sensor [illumination = " + illumination + " lx" + ", uuid=" + uuid + ", type="
                + type + ", secret=" + secret + "]";
    }
}