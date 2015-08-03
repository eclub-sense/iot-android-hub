package com.eclubprague.iot.android.driothub.cloud.sensors;

import com.google.gson.annotations.Expose;

import java.util.Arrays;

/**
 * Created by Dat on 28.7.2015.
 */
public class ESCLed extends Sensor implements WriteableSensor {

    @Expose(deserialize = false) protected Switch led;

    public ESCLed(String uuid, String secret) {
        super(uuid, SensorType.LED, secret);
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
                + ", battery=" + battery + ", hubID=" + hubID + ", hub=" + hub + ", reserved="
                + Arrays.toString(reserved) + "]";
    }
}
