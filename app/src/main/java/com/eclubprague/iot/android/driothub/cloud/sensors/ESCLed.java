package com.eclubprague.iot.android.driothub.cloud.sensors;

import com.eclubprague.iot.android.driothub.cloud.hubs.Hub;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.DataNameValuePair;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.SensorType;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.Switch;
import com.google.gson.annotations.Expose;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Dat on 28.7.2015.
 */
public class ESCLed extends Sensor implements WriteableSensor {

    @Expose(deserialize = false) protected Switch led;

    public ESCLed(String uuid, String secret, Hub hub) {
        super(uuid, SensorType.LED, secret, hub);
    }

    public void readPayload(byte[] data) {
        led = (data[0] < 0) ? Switch.OFF : Switch.ON;

    }

    @Override
    public String printData() {
        return ( (led == Switch.OFF) ? "OFF" : "ON" );
    }


    @Override
    public void writePacket(byte[] payload) {
        // TODO Auto-generated method stub

    }

    @Override
    public void createPayload() {
        // TODO Auto-generated method stub

    }

    @Override
    public byte[] encrypt(String data) {
        // TODO Auto-generated method stub
        return null;
    }

    public Switch getLed() {
        return led;
    }

    @Override
    public String toString() {
        return "ESCLed [led=" + led + ", uuid=" + uuid + ", type=" + type + ", secret=" + secret + ", incr=" + incr
                + /*", battery=" + battery + ", hubID=" + hubID +*/ ", hub=" + hubRef.get() + ", reserved="
                + Arrays.toString(reserved) + "]";
    }

    @Override
    public List<DataNameValuePair> getMeasured() {
        measured.clear();
        return measured;
    }

    @Override
    public void setData(float[] values) {
    }
}
